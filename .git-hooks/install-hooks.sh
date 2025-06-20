#!/bin/sh

# Script to install Git hooks
echo "Installing Git hooks..."

# Create hooks directory if it doesn't exist
mkdir -p .git/hooks

# Copy pre-commit hook
cp .git-hooks/pre-commit .git/hooks/
chmod +x .git/hooks/pre-commit

echo "Git hooks installed successfully!"