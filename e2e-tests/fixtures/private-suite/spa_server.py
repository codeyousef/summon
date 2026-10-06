#!/usr/bin/env python3
"""Static fixture server with a public-shell fallback for browser deep-link tests."""

import argparse
import http.server
import os


class ShellFallbackHandler(http.server.SimpleHTTPRequestHandler):
    def send_head(self):
        requested = self.translate_path(self.path.split("?", 1)[0].split("#", 1)[0])
        if not os.path.exists(requested) and "text/html" in self.headers.get("Accept", ""):
            self.path = "/index.html"
        return super().send_head()


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
