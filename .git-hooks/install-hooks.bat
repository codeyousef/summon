@echo off
REM Script to install Git hooks on Windows

echo Installing Git hooks...

REM Create hooks directory if it doesn't exist
if not exist .git\hooks mkdir .git\hooks

REM Copy pre-commit hook
copy /Y .git-hooks\pre-commit .git\hooks\pre-commit

echo Git hooks installed successfully!