#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd -- "$script_dir/.." && pwd)"
snapshot_dir="$script_dir/tests/component-harness.spec.ts-snapshots"
image='mcr.microsoft.com/playwright@sha256:f1e7e01021efd65dd1a2c56064be399f3e4de00fd021ac561325f2bfbb2b837a'

snapshot_hash() {
  tar --sort=name --mtime='UTC 1970-01-01' -cf - -C "$snapshot_dir" . | sha256sum | cut -d' ' -f1
}

before="$(snapshot_hash)"
for visual_case in changed missing; do
  artifact_dir="$repo_root/.gradle/private-suite/browser-artifacts/component-harness-visual-$visual_case"
  rm -rf -- "$artifact_dir"
  mkdir -p -- "$artifact_dir"
  set +e
  docker run --rm --init --network none --user "$(id -u):$(id -g)" \
    --cap-drop ALL --security-opt no-new-privileges \
    --cpus 2 --memory 2g --memory-swap 2g --pids-limit 256 --shm-size 256m \
    --env SUMMON_BROWSER_PROFILE=development \
    --env SUMMON_PLAYWRIGHT_CONFIG=playwright.harness.config.ts \
    --env "SUMMON_VISUAL_FAILURE_CASE=$visual_case" \
    --mount "type=bind,source=$script_dir,target=/work,readonly" \
    --mount "type=bind,source=$artifact_dir,target=/artifacts" \
    --workdir /work "$image" bash ./run-private-suite-in-container.sh \
    --project=js-chromium --grep='visual comparator rejects' \
    2>&1 | tee "$artifact_dir/run.log"
  status=${PIPESTATUS[0]}
  set -e
  if [[ $status -eq 0 ]]; then
    echo "visual $visual_case case unexpectedly passed" >&2
    exit 1
  fi
  if [[ "$visual_case" == changed ]]; then
    [[ -n "$(find "$artifact_dir/results" -type f -name '*-actual.png' -print -quit)" ]]
    [[ -n "$(find "$artifact_dir/results" -type f -name '*-diff.png' -print -quit)" ]]
  fi
done

after="$(snapshot_hash)"
if [[ "$before" != "$after" ]]; then
  echo "ordinary visual failure verification changed tracked golden bytes" >&2
  exit 1
fi
printf 'PASS changed-and-missing visual failures; golden hash=%s\n' "$after"
