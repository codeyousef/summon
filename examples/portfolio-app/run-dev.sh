#!/bin/bash

echo "Starting Portfolio Example..."
echo "============================="
echo ""
echo "Using H2 in-memory database (no Docker required)"
echo ""

# Run with H2 profile to avoid Hibernate Reactive threading issues
./gradlew quarkusDev -Dquarkus.profile=h2