#!/usr/bin/env bash
set -uo pipefail

artifact_dir=/artifacts
mkdir -p -- "$artifact_dir/results"

node <<'JS'
const fs = require('fs');
const { chromium, firefox, webkit } = require('playwright');
(async () => {
  const versions = {};
  for (const [name, browserType] of Object.entries({ chromium, firefox, webkit })) {
    const browser = await browserType.launch({ headless: true });
    versions[name] = browser.version();
    await browser.close();
  }
  fs.writeFileSync('/artifacts/browser-versions.json', JSON.stringify(versions, null, 2) + '\n');
})().catch(error => {
  console.error(error);
  process.exit(2);
});
JS
browser_status=$?

if [[ $browser_status -eq 0 ]]; then
  PLAYWRIGHT_JSON_OUTPUT_FILE="$artifact_dir/results.json" \
    node node_modules/playwright/cli.js test \
      --config=playwright.private-suite.config.ts \
      --reporter=line,json \
      --output="$artifact_dir/results" "$@"
  test_status=$?
else
  test_status=$browser_status
fi

python3 - "$artifact_dir/runtime-metrics.json" "$test_status" <<'PY'
import json
import pathlib
import sys


def read_metric(name):
    path = pathlib.Path('/sys/fs/cgroup') / name
    try:
        return path.read_text().strip()
    except OSError:
        return None

metrics = {
    'exitCode': int(sys.argv[2]),
    'memoryPeakBytes': read_metric('memory.peak'),
    'pidsPeak': read_metric('pids.peak'),
    'cpuStat': read_metric('cpu.stat'),
}
pathlib.Path(sys.argv[1]).write_text(json.dumps(metrics, indent=2) + '\n')
PY

exit "$test_status"
