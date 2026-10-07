#!/usr/bin/env python3
"""Static fixture server with a public-shell fallback for browser deep-link tests."""

import argparse
import base64
import http.server
import os
import secrets
from urllib.parse import urlsplit

class ShellFallbackHandler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self):
        request_path = urlsplit(self.path).path
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
            "connect-src 'self'",
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
