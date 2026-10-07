#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "usage: $0 <js-chromium|js-firefox|js-webkit|wasm-chromium|wasm-firefox|wasm-webkit>" >&2
  exit 2
fi
project="$1"
case "$project" in
  js-chromium|js-firefox|js-webkit|wasm-chromium|wasm-firefox|wasm-webkit) ;;
  *) echo "unsupported harness golden project: $project" >&2; exit 2 ;;
esac

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
snapshot_dir="$script_dir/tests/component-harness.spec.ts-snapshots"
artifact_dir="$script_dir/../.gradle/private-suite/browser-artifacts/component-harness-golden-update-$project"
image='mcr.microsoft.com/playwright@sha256:f1e7e01021efd65dd1a2c56064be399f3e4de00fd021ac561325f2bfbb2b837a'
mkdir -p -- "$snapshot_dir" "$artifact_dir"

docker run --rm --init --network none --user "$(id -u):$(id -g)" \
  --cap-drop ALL --security-opt no-new-privileges \
  --cpus 2 --memory 2g --memory-swap 2g --pids-limit 256 --shm-size 256m \
  --env SUMMON_BROWSER_PROFILE=development \
  --env SUMMON_PLAYWRIGHT_CONFIG=playwright.harness.config.ts \
  --mount "type=bind,source=$script_dir,target=/work,readonly" \
  --mount "type=bind,source=$snapshot_dir,target=/work/tests/component-harness.spec.ts-snapshots" \
  --mount "type=bind,source=$artifact_dir,target=/artifacts" \
  --workdir /work "$image" bash ./run-private-suite-in-container.sh \
  --project="$project" --update-snapshots
