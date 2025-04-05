@echo off
REM Run only JavaScript tests to avoid ClassCastException issues
echo Running JavaScript-specific tests (JsTest)...
call gradlew clean jsBrowserTest --tests="**.*JsTest*"
echo Done. 