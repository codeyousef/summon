#!/bin/bash
set -euo pipefail

PASSPHRASE="$1"
KEY_FILE="$2"
OUTPUT_FILE="$3"
INPUT_FILE="$4"

GPG_BIN="${GPG_BIN:-$(command -v gpg)}"

if [[ -z "$GPG_BIN" ]]; then
  echo "gpg not found in PATH" >&2
  exit 1
fi

if [[ ! -f "$KEY_FILE" ]]; then
  echo "Key file not found: $KEY_FILE" >&2
  exit 1
fi

GNUPGHOME_DIR="${GNUPGHOME:-}"
if [[ -z "$GNUPGHOME_DIR" ]]; then
  GNUPGHOME_DIR="$(mktemp -d)"
  trap 'rm -rf "$GNUPGHOME_DIR"' EXIT
fi
export GNUPGHOME="$GNUPGHOME_DIR"

# Import the private key
"$GPG_BIN" --batch --yes --import "$KEY_FILE"

# Extract the first secret key fingerprint
KEY_ID=$("$GPG_BIN" --batch --with-colons --list-secret-keys | awk -F: '$1=="sec"{print $5; exit}')

if [[ -z "$KEY_ID" ]]; then
  echo "Unable to determine secret key ID after import." >&2
  exit 1
fi

# Sign the artifact
"$GPG_BIN" --batch --yes --pinentry-mode loopback \
  --passphrase "$PASSPHRASE" \
  --default-key "$KEY_ID" \
  --local-user "$KEY_ID" \
  --armor --detach-sign \
  --output "$OUTPUT_FILE" \
  "$INPUT_FILE"

chmod 600 "$OUTPUT_FILE"
echo "   ✔ Signed $(basename "$INPUT_FILE") with key $KEY_ID"
