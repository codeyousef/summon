#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd -- "$script_dir/.." && pwd)"
artifact_dir="$repo_root/.gradle/private-suite/browser-artifacts/matrix"
image='mcr.microsoft.com/playwright@sha256:f1e7e01021efd65dd1a2c56064be399f3e4de00fd021ac561325f2bfbb2b837a'

python3 - "$script_dir/node_modules/playwright/package.json" <<'PY'
import json, sys
with open(sys.argv[1]) as source:
    version = json.load(source)['version']
if version != '1.56.1':
    raise SystemExit('This pinned browser image requires Playwright 1.56.1; run npm ci in e2e-tests.')
PY

mkdir -p -- "$artifact_dir"
docker run --rm --init --network none --user "$(id -u):$(id -g)" \
  --cap-drop ALL --security-opt no-new-privileges \
  --cpus 2 --memory 2g --memory-swap 2g --pids-limit 256 --shm-size 256m \
  --mount "type=bind,source=$script_dir,target=/work,readonly" \
  --mount "type=bind,source=$artifact_dir,target=/artifacts" \
  --workdir /work "$image" node node_modules/playwright/cli.js test \
  --config=playwright.private-suite.config.ts --reporter=line --output=/artifacts/results "$@"
