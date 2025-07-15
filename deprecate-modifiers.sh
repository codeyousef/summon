#!/bin/bash

# Script to add deprecation annotations to duplicate modifier functions

echo "Adding deprecation annotations to StylingModifiers.kt..."

# First, let's backup the original file
cp src/commonMain/kotlin/code/yousef/summon/modifier/StylingModifiers.kt \
   src/commonMain/kotlin/code/yousef/summon/modifier/StylingModifiers.kt.backup

# Create a temporary file with deprecation annotations
cat > temp_deprecations.txt << 'EOF'
# Deprecation rules:
# Keep only these boxShadow overloads:
# 1. fun Modifier.boxShadow(value: String)
# 2. fun Modifier.boxShadow(horizontalOffset: String, verticalOffset: String, blurRadius: String, spreadRadius: String?, color: String, inset: Boolean = false)
# 3. fun Modifier.boxShadow(horizontalOffset: String, verticalOffset: String, blurRadius: String, spreadRadius: String?, color: Color, inset: Boolean = false)

# Keep only these transition overloads:
# 1. fun Modifier.transition(value: String)
# 2. fun Modifier.transition(property: TransitionProperty, duration: String, timingFunction: TransitionTimingFunction, delay: String)

# Keep only these linearGradient overloads:
# 1. fun Modifier.linearGradient(value: String)
# 2. fun Modifier.linearGradient(direction: String, colors: List<String>)
# 3. fun Modifier.linearGradient(direction: String, colors: List<Pair<Color, String>>)

# Keep only these radialGradient overloads:
# 1. fun Modifier.radialGradient(value: String)
# 2. fun Modifier.radialGradient(shape: String, position: String, colors: List<String>)
# 3. fun Modifier.radialGradient(shape: RadialGradientShape, position: RadialGradientPosition, colors: List<Pair<Color, String>>)
EOF

echo "Script complete. Manual editing of StylingModifiers.kt is required to add deprecation annotations."
echo "Please review the deprecation rules in temp_deprecations.txt"