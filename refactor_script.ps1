# Create directory structure
$baseDir = "src/commonMain/kotlin/code/yousef/summon"

# Core directory
New-Item -ItemType Directory -Force -Path "$baseDir/core"

# Components directories
New-Item -ItemType Directory -Force -Path "$baseDir/components/layout"
New-Item -ItemType Directory -Force -Path "$baseDir/components/input"
New-Item -ItemType Directory -Force -Path "$baseDir/components/feedback"
New-Item -ItemType Directory -Force -Path "$baseDir/components/display"
New-Item -ItemType Directory -Force -Path "$baseDir/components/navigation"

# Other directories
New-Item -ItemType Directory -Force -Path "$baseDir/modifier"
New-Item -ItemType Directory -Force -Path "$baseDir/state"
New-Item -ItemType Directory -Force -Path "$baseDir/theme"

# Move core files
Move-Item -Path "$baseDir/Composable.kt" -Destination "$baseDir/core/"
Move-Item -Path "$baseDir/PlatformRenderer.kt" -Destination "$baseDir/core/"
Move-Item -Path "$baseDir/PlatformRendererProvider.kt" -Destination "$baseDir/core/"
Move-Item -Path "$baseDir/State.kt" -Destination "$baseDir/state/"
Move-Item -Path "$baseDir/RememberSaveable.kt" -Destination "$baseDir/state/"
Move-Item -Path "$baseDir/FlowBinding.kt" -Destination "$baseDir/state/"
Move-Item -Path "$baseDir/StateFlowIntegration.kt" -Destination "$baseDir/state/"
Move-Item -Path "$baseDir/ViewModel.kt" -Destination "$baseDir/state/"
Move-Item -Path "$baseDir/Effects.kt" -Destination "$baseDir/core/"
Move-Item -Path "$baseDir/Lifecycle.kt" -Destination "$baseDir/core/"

# Move layout components
Move-Item -Path "$baseDir/Row.kt" -Destination "$baseDir/components/layout/"
Move-Item -Path "$baseDir/Column.kt" -Destination "$baseDir/components/layout/"
Move-Item -Path "$baseDir/Box.kt" -Destination "$baseDir/components/layout/"
Move-Item -Path "$baseDir/Grid.kt" -Destination "$baseDir/components/layout/"
Move-Item -Path "$baseDir/Spacer.kt" -Destination "$baseDir/components/layout/"
Move-Item -Path "$baseDir/ResponsiveLayout.kt" -Destination "$baseDir/components/layout/"
Move-Item -Path "$baseDir/AspectRatio.kt" -Destination "$baseDir/components/layout/"
Move-Item -Path "$baseDir/LazyRow.kt" -Destination "$baseDir/components/layout/"
Move-Item -Path "$baseDir/LazyColumn.kt" -Destination "$baseDir/components/layout/"
Move-Item -Path "$baseDir/Divider.kt" -Destination "$baseDir/components/layout/"
Move-Item -Path "$baseDir/Card.kt" -Destination "$baseDir/components/layout/"
Move-Item -Path "$baseDir/ExpansionPanel.kt" -Destination "$baseDir/components/layout/"

# Move input components
Move-Item -Path "$baseDir/TextField.kt" -Destination "$baseDir/components/input/"
Move-Item -Path "$baseDir/TextArea.kt" -Destination "$baseDir/components/input/"
Move-Item -Path "$baseDir/Checkbox.kt" -Destination "$baseDir/components/input/"
Move-Item -Path "$baseDir/RadioButton.kt" -Destination "$baseDir/components/input/"
Move-Item -Path "$baseDir/Select.kt" -Destination "$baseDir/components/input/"
Move-Item -Path "$baseDir/Switch.kt" -Destination "$baseDir/components/input/"
Move-Item -Path "$baseDir/Button.kt" -Destination "$baseDir/components/input/"
Move-Item -Path "$baseDir/DatePicker.kt" -Destination "$baseDir/components/input/"
Move-Item -Path "$baseDir/TimePicker.kt" -Destination "$baseDir/components/input/"
Move-Item -Path "$baseDir/FileUpload.kt" -Destination "$baseDir/components/input/"
Move-Item -Path "$baseDir/RangeSlider.kt" -Destination "$baseDir/components/input/"
Move-Item -Path "$baseDir/Form.kt" -Destination "$baseDir/components/input/"
Move-Item -Path "$baseDir/FormField.kt" -Destination "$baseDir/components/input/"
Move-Item -Path "$baseDir/Validator.kt" -Destination "$baseDir/components/input/"

# Move feedback components
Move-Item -Path "$baseDir/Alert.kt" -Destination "$baseDir/components/feedback/"
Move-Item -Path "$baseDir/Progress.kt" -Destination "$baseDir/components/feedback/"
Move-Item -Path "$baseDir/Badge.kt" -Destination "$baseDir/components/feedback/"
Move-Item -Path "$baseDir/Tooltip.kt" -Destination "$baseDir/components/feedback/"

# Move display components
Move-Item -Path "$baseDir/Text.kt" -Destination "$baseDir/components/display/"
Move-Item -Path "$baseDir/Image.kt" -Destination "$baseDir/components/display/"
Move-Item -Path "$baseDir/Icon.kt" -Destination "$baseDir/components/display/"
Move-Item -Path "$baseDir/TextUtils.kt" -Destination "$baseDir/components/display/"

# Move navigation components
Move-Item -Path "$baseDir/TabLayout.kt" -Destination "$baseDir/components/navigation/"
Move-Item -Path "$baseDir/Link.kt" -Destination "$baseDir/components/navigation/"

# Move modifier files
Move-Item -Path "$baseDir/Modifier.kt" -Destination "$baseDir/modifier/"
Move-Item -Path "$baseDir/ModifierExtensions.kt" -Destination "$baseDir/modifier/"
Move-Item -Path "$baseDir/ModifierComponents.kt" -Destination "$baseDir/modifier/"

# Move theme files
Move-Item -Path "$baseDir/Theme.kt" -Destination "$baseDir/theme/"
Move-Item -Path "$baseDir/ColorSystem.kt" -Destination "$baseDir/theme/"
Move-Item -Path "$baseDir/Typography.kt" -Destination "$baseDir/theme/"
Move-Item -Path "$baseDir/Spacing.kt" -Destination "$baseDir/theme/"
Move-Item -Path "$baseDir/StyleSheet.kt" -Destination "$baseDir/theme/"
Move-Item -Path "$baseDir/MediaQuery.kt" -Destination "$baseDir/theme/"

Write-Host "Refactoring completed successfully!" 