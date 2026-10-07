#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd -- "$script_dir/.." && pwd)"
artifact_dir="$repo_root/.gradle/private-suite/browser-artifacts/devtools-inspector"
image='mcr.microsoft.com/playwright@sha256:f1e7e01021efd65dd1a2c56064be399f3e4de00fd021ac561325f2bfbb2b837a'

python3 - "$script_dir/node_modules/playwright/package.json" <<'PY'
import json, sys
with open(sys.argv[1]) as source:
    version = json.load(source)['version']
if version != '1.56.1':
    raise SystemExit('This pinned browser image requires Playwright 1.56.1; run npm ci in e2e-tests.')
PY

mkdir -p -- "$artifact_dir"
set +e
docker run --rm --init --network none --user "$(id -u):$(id -g)" \
  --cap-drop ALL --security-opt no-new-privileges \
  --cpus 2 --memory 2g --memory-swap 2g --pids-limit 256 --shm-size 256m \
  --env SUMMON_BROWSER_PROFILE=development \
  --env SUMMON_PLAYWRIGHT_CONFIG=playwright.devtools.config.ts \
  --mount "type=bind,source=$script_dir,target=/work,readonly" \
  --mount "type=bind,source=$artifact_dir,target=/artifacts" \
  --workdir /work "$image" bash ./run-private-suite-in-container.sh "$@" \
  2>&1 | tee "$artifact_dir/run.log"
status=${PIPESTATUS[0]}
set -e

commit="$(git -C "$repo_root" rev-parse HEAD)"
dirty=false
if [[ -n "$(git -C "$repo_root" status --porcelain)" ]]; then dirty=true; fi
lock_sha="$(sha256sum "$script_dir/package-lock.json" | cut -d' ' -f1)"
python3 - "$artifact_dir/manifest.json" "$image" "$commit" "$dirty" "$lock_sha" "$status" <<'PY'
import datetime, json, pathlib, sys
manifest = {
    'schemaVersion': 1,
    'recordedAt': datetime.datetime.now(datetime.timezone.utc).isoformat(),
    'profile': 'devtools-development',
    'image': sys.argv[2],
    'commit': sys.argv[3],
    'sourceDirty': sys.argv[4] == 'true',
    'packageLockSha256': sys.argv[5],
    'exitCode': int(sys.argv[6]),
    'network': 'none',
    'workers': 1,
    'retries': 0,
    'limits': {'cpus': 2, 'memoryBytes': 2147483648, 'pids': 256, 'shmBytes': 268435456},
    'results': 'results.json',
    'browserVersions': 'browser-versions.json',
    'runtimeMetrics': 'runtime-metrics.json',
    'log': 'run.log',
}
pathlib.Path(sys.argv[1]).write_text(json.dumps(manifest, indent=2) + '\n')
PY
exit "$status"
