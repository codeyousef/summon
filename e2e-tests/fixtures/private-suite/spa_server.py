#!/usr/bin/env python3
"""Static fixture server with a public-shell fallback for browser deep-link tests."""

import argparse
import base64
import hashlib
import http.server
import json
import os
import secrets
import struct
import threading
import time
from urllib.parse import urlsplit


WEBSOCKET_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"

class ShellFallbackHandler(http.server.SimpleHTTPRequestHandler):
    protocol_version = "HTTP/1.1"
    metrics_lock = threading.Lock()
    metrics = {
        "slow_aborts": 0,
        "cross_origin_hits": 0,
        "signal_connections": 0,
    }

    def do_GET(self):
        request_path = urlsplit(self.path).path
        if request_path == "/signals" and self.headers.get("Upgrade", "").lower() == "websocket":
            self._serve_signals()
            return
        if request_path == "/transport/json":
            self._json_response(200, {"transport": "ok"})
            return
        if request_path.startswith("/transport/status/"):
            status = int(request_path.rsplit("/", 1)[1])
            self._json_response(
                status,
                {"private": "must-not-appear-in-error"},
                {
                    "X-Error-Code": f"status-{status}",
                    "X-Request-ID": "request-transport-01",
                    "Retry-After": "2",
                },
            )
            return
        if request_path == "/transport/slow":
            time.sleep(2.0 if "stale=true" in self.path else 0.5)
            try:
                self._json_response(200, {"slow": True})
            except (BrokenPipeError, ConnectionResetError):
                with self.metrics_lock:
                    self.metrics["slow_aborts"] += 1
            return
        if request_path == "/transport/oversize":
            self.send_response(200)
            self.send_header("Content-Type", "application/json")
            self.send_header("Cache-Control", "no-store")
            self.end_headers()
            try:
                self.wfile.write(b'{"data":"')
                self.wfile.flush()
                for _ in range(64):
                    self.wfile.write(b"x" * 64)
                    self.wfile.flush()
                    time.sleep(0.002)
                self.wfile.write(b'"}')
            except (BrokenPipeError, ConnectionResetError):
                pass
            return
        if request_path == "/transport/redirect":
            self.send_response(302)
            self.send_header(
                "Location",
                f"http://localhost:{self.server.server_address[1]}/transport/cross-origin",
            )
            self.send_header("Content-Length", "0")
            self.send_header("Connection", "close")
            self.end_headers()
            self.close_connection = True
            return
        if request_path == "/transport/cross-origin":
            with self.metrics_lock:
                self.metrics["cross_origin_hits"] += 1
            self._json_response(200, {"unexpected": "redirect-followed"})
            return
        if request_path == "/transport/metrics":
            with self.metrics_lock:
                snapshot = dict(self.metrics)
            self._json_response(200, snapshot)
            return
        requested = self.translate_path(request_path)
        if os.path.isdir(requested):
            requested = os.path.join(requested, "index.html")
        wants_html = "text/html" in self.headers.get("Accept", "")
        if request_path.endswith(".html") or request_path == "/" or (
            not os.path.exists(requested) and wants_html
        ):
            source_path = requested if os.path.isfile(requested) else self.translate_path("/index.html")
            self._serve_shell(source_path)
            return
        super().do_GET()

    def do_POST(self):
        request_path = urlsplit(self.path).path
        if request_path != "/transport/operation":
            self.send_error(404)
            return
        length = int(self.headers.get("Content-Length", "0"))
        if length:
            self.rfile.read(length)
        self._json_response(
            200,
            {
                "operation": self.headers.get("Idempotency-Key"),
                "csrf": self.headers.get("X-CSRF-Token"),
            },
        )

    def _json_response(self, status, value, headers=None):
        payload = json.dumps(value, separators=(",", ":")).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(payload)))
        self.send_header("Cache-Control", "no-store")
        for name, header_value in (headers or {}).items():
            self.send_header(name, header_value)
        self.end_headers()
        self.wfile.write(payload)

    def _serve_signals(self):
        self.close_connection = True
        key = self.headers.get("Sec-WebSocket-Key")
        if not key:
            self.send_error(400)
            return
        accept = base64.b64encode(
            hashlib.sha1((key + WEBSOCKET_GUID).encode("ascii")).digest()
        ).decode("ascii")
        self.send_response(101, "Switching Protocols")
        self.send_header("Upgrade", "websocket")
        self.send_header("Connection", "Upgrade")
        self.send_header("Sec-WebSocket-Accept", accept)
        self.end_headers()
        with self.metrics_lock:
            self.metrics["signal_connections"] += 1
            connection = self.metrics["signal_connections"]
        try:
            for index in range(24):
                self._websocket_text(f"hint-{connection}-{index}")
            time.sleep(0.5)
            self._websocket_text(f"late-{connection}")
            self._websocket_close(1012)
        except (BrokenPipeError, ConnectionResetError):
            return

    def _websocket_text(self, text):
        payload = text.encode("utf-8")
        first = bytes([0x81])
        if len(payload) < 126:
            header = first + bytes([len(payload)])
        else:
            header = first + bytes([126]) + struct.pack("!H", len(payload))
        self.connection.sendall(header + payload)

    def _websocket_close(self, code):
        payload = struct.pack("!H", code)
        self.connection.sendall(bytes([0x88, len(payload)]) + payload)

    def _serve_shell(self, source_path):
        with open(source_path, "r", encoding="utf-8") as source:
            html = source.read()
        nonce = base64.urlsafe_b64encode(secrets.token_bytes(32)).decode("ascii").rstrip("=")
        nonce_meta = f'<meta name="summon-style-nonce" content="{nonce}">'
        html = html.replace("<head>", f"<head>{nonce_meta}", 1)
        payload = html.encode("utf-8")
        policy = "; ".join([
            "default-src 'none'",
            "script-src 'self' 'wasm-unsafe-eval'",
            "script-src-elem 'self'",
            "script-src-attr 'none'",
            f"style-src 'self' 'nonce-{nonce}'",
            f"style-src-elem 'self' 'nonce-{nonce}'",
            "style-src-attr 'unsafe-inline'",
            "img-src 'self' blob:",
            "font-src 'self'",
            "connect-src 'self' ws://127.0.0.1:*",
            "worker-src 'self'",
            "manifest-src 'self'",
            "object-src 'none'",
            "base-uri 'none'",
            "frame-ancestors 'none'",
            "form-action 'self'",
        ])
        self.send_response(200)
        self.send_header("Content-Type", "text/html; charset=utf-8")
        self.send_header("Content-Length", str(len(payload)))
        self.send_header("Content-Security-Policy", policy)
        self.send_header("Cache-Control", "no-store")
        self.send_header("X-Content-Type-Options", "nosniff")
        self.send_header("Referrer-Policy", "no-referrer")
        self.end_headers()
        self.wfile.write(payload)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--port", required=True, type=int)
    parser.add_argument("--directory", required=True)
    arguments = parser.parse_args()
    handler = lambda *args, **kwargs: ShellFallbackHandler(
        *args, directory=arguments.directory, **kwargs
    )
    with http.server.ThreadingHTTPServer(("127.0.0.1", arguments.port), handler) as server:
        server.serve_forever()


if __name__ == "__main__":
    main()
