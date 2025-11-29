#!/bin/bash
# Local Lighthouse testing with PageSpeed Insights-equivalent throttling
# This produces numbers that match what you'll see on web.dev/measure

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

URL="${1:-http://localhost:8080}"
OUTPUT_DIR="lighthouse-reports"

mkdir -p "$OUTPUT_DIR"

echo -e "${YELLOW}=== Lighthouse Performance Test ===${NC}"
echo "URL: $URL"
echo ""

# Mobile test with PageSpeed Insights throttling
# These settings match what PSI uses for mobile
echo -e "${YELLOW}Running Mobile test (4x CPU slowdown, Slow 4G)...${NC}"
npx -- lighthouse "$URL" \
    --only-categories=performance \
    --preset=perf \
    --throttling-method=simulate \
    --throttling.cpuSlowdownMultiplier=4 \
    --throttling.rttMs=150 \
    --throttling.throughputKbps=1638 \
    --screenEmulation.mobile=true \
    --screenEmulation.width=412 \
    --screenEmulation.height=823 \
    --output=json \
    --output=html \
    --output-path="$OUTPUT_DIR/mobile.report" \
    --chrome-flags="--headless --no-sandbox" \
    2>&1 | grep -E "(LH:Printer|error)" || true

# Extract mobile scores
MOBILE_PERF=$(jq '.categories.performance.score * 100' "$OUTPUT_DIR/mobile.report.json" | cut -d. -f1)
MOBILE_TBT=$(jq '.audits["total-blocking-time"].numericValue' "$OUTPUT_DIR/mobile.report.json" | cut -d. -f1)
MOBILE_FCP=$(jq '.audits["first-contentful-paint"].numericValue / 1000' "$OUTPUT_DIR/mobile.report.json")
MOBILE_LCP=$(jq '.audits["largest-contentful-paint"].numericValue / 1000' "$OUTPUT_DIR/mobile.report.json")

echo ""
echo -e "${YELLOW}Running Desktop test (1x CPU, Fast connection)...${NC}"
# Desktop test with PageSpeed Insights settings
npx -- lighthouse "$URL" \
    --only-categories=performance \
    --preset=desktop \
    --throttling-method=simulate \
    --throttling.cpuSlowdownMultiplier=1 \
    --throttling.rttMs=40 \
    --throttling.throughputKbps=10240 \
    --screenEmulation.mobile=false \
    --screenEmulation.width=1350 \
    --screenEmulation.height=940 \
    --output=json \
    --output=html \
    --output-path="$OUTPUT_DIR/desktop.report" \
    --chrome-flags="--headless --no-sandbox" \
    2>&1 | grep -E "(LH:Printer|error)" || true

# Extract desktop scores
DESKTOP_PERF=$(jq '.categories.performance.score * 100' "$OUTPUT_DIR/desktop.report.json" | cut -d. -f1)
DESKTOP_TBT=$(jq '.audits["total-blocking-time"].numericValue' "$OUTPUT_DIR/desktop.report.json" | cut -d. -f1)
DESKTOP_FCP=$(jq '.audits["first-contentful-paint"].numericValue / 1000' "$OUTPUT_DIR/desktop.report.json")
DESKTOP_LCP=$(jq '.audits["largest-contentful-paint"].numericValue / 1000' "$OUTPUT_DIR/desktop.report.json")

echo ""
echo -e "${YELLOW}=== Results ===${NC}"
echo ""
echo "                    Mobile          Desktop"
echo "                    ------          -------"
printf "Performance:        %-15s %s\n" "${MOBILE_PERF}%" "${DESKTOP_PERF}%"
printf "TBT:                %-15s %s\n" "${MOBILE_TBT}ms" "${DESKTOP_TBT}ms"
printf "FCP:                %-15s %s\n" "${MOBILE_FCP}s" "${DESKTOP_FCP}s"
printf "LCP:                %-15s %s\n" "${MOBILE_LCP}s" "${DESKTOP_LCP}s"
echo ""

# Performance thresholds
FAIL=0

if [ "$MOBILE_TBT" -gt 300 ]; then
    echo -e "${RED}FAIL: Mobile TBT ${MOBILE_TBT}ms > 300ms threshold${NC}"
    FAIL=1
fi

if [ "$DESKTOP_TBT" -gt 200 ]; then
    echo -e "${RED}FAIL: Desktop TBT ${DESKTOP_TBT}ms > 200ms threshold${NC}"
    FAIL=1
fi

if [ "$MOBILE_PERF" -lt 80 ]; then
    echo -e "${RED}FAIL: Mobile performance ${MOBILE_PERF}% < 80% threshold${NC}"
    FAIL=1
fi

if [ "$DESKTOP_PERF" -lt 90 ]; then
    echo -e "${RED}FAIL: Desktop performance ${DESKTOP_PERF}% < 90% threshold${NC}"
    FAIL=1
fi

if [ "$FAIL" -eq 0 ]; then
    echo -e "${GREEN}PASS: All performance thresholds met${NC}"
fi

echo ""
echo "Full reports: $OUTPUT_DIR/mobile.report.html, $OUTPUT_DIR/desktop.report.html"

exit $FAIL
