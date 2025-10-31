#!/bin/bash
GPG_CMD="/usr/bin/gpg"
PASSPHRASE="$1"
KEY_FILE="$2" 
OUTPUT_FILE="$3"
INPUT_FILE="$4"

# Import key
$GPG_CMD --batch --yes --import "$KEY_FILE" 2>/dev/null

# Sign file
$GPG_CMD --batch --yes --pinentry-mode loopback --passphrase "$PASSPHRASE" --armor --detach-sign --output "$OUTPUT_FILE" "$INPUT_FILE"
exit $?
