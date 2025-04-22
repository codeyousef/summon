# Changelog

All notable changes to this project will be documented in this file.

## [0.2.4.3]

### Changed
- Updated version number to 0.2.4.3
- Improved Boolean parameter handling in RouteParams.getBoolean() method to explicitly check for "true" and "false" strings (case-insensitive)

### Added
- Comprehensive test suite for routing functionality
- JVM-specific router tests including server-side session management
- Enhanced test coverage for composition context and rendering utilities

## [0.2.4.2]

### Fixed
- Fixed floating point representation issue in LazyListState.getDataAttributes method
- Ensured consistent decimal formatting across platforms

## [0.2.4.1]

### Added
- Comprehensive thread safety improvements to FlowBinding.kt
- Added Mutex for thread-safe access to the scopes map in FlowCollectionRegistry
- Implemented double-check locking pattern in getScope method for improved performance while maintaining thread safety
- Enhanced cancelScope and cancelAll methods with proper locking mechanisms
- Started comprehensive testing

### Changed
- Improved thread safety documentation with detailed notes for each method
- Enhanced error handling for race conditions in flow binding operations

## [0.2.4.0]

### Added
- Enhanced theme system with typed theme classes for more type-safe access
- Added TypographyTheme, SpacingTheme, BorderRadiusTheme, and ElevationTheme classes
- Added direct access methods for typed theme properties (getTypographyTheme, getSpacingTheme, etc.)
- Enhanced TextStyle class to support FontWeight enum, numeric values, and Color objects
- Added TextStyle.create() factory method for type-safe TextStyle creation
- Added fillMaxHeight() and fillMaxSize() modifier extensions to complement existing fillMaxWidth()
- Updated documentation with examples of both string-based and typed theme APIs
- Added component-specific type-safe modifiers to ensure modifiers are only applied to appropriate components
- Added TextComponent, MediaComponent, LayoutComponent, InputComponent, ScrollableComponent, ClickableComponent, and FocusableComponent marker interfaces
- Added BorderSide enum for specifying which side of an element to apply border properties to
- Enhanced borderWidth modifier to accept numeric values directly (e.g., 1 becomes "1px")
- Added side-specific borderWidth function that takes a BorderSide parameter
- Added individual border width functions for each side (borderTopWidth, borderRightWidth, etc.)
- Added AlignItems, AlignContent, and AlignSelf enums for more type-safe alignment
- Added verticalAlignment and horizontalAlignment modifier extensions for Row and Column components
- Enhanced alignSelf and alignContent modifiers to accept enum values
- Added comprehensive border modifier that accepts width, style, color, and radius as parameters
- Added linear gradient background functions with flexible API options
- Added color extensions (Color.hex, Color.rgb, Color.rgba) for easier color creation
- Added extensive color presets including basic colors, Material3, and Catppuccin palettes
- Added TextAlign enum with proper string value representation
- Enhanced textAlign modifier functions to accept TextAlign enum directly
- Added TextTransform enum with standard CSS text-transform values (None, Capitalize, Uppercase, Lowercase, etc.)
- Enhanced textTransform modifier functions to accept TextTransform enum directly
- Enhanced letterSpacing modifier functions to accept numeric values directly (e.g., 1 becomes "1px")
- Added BackgroundClip enum with support for text clipping
- Added backgroundClip modifier functions
- Enhanced linear gradient to support Color objects directly
- Enhanced radial gradient to support Color objects directly
- Improved linear gradient to better support angle degrees
- Added new linearGradient overload with direction-first parameter order
- Added support for using Color objects directly with color and backgroundColor modifiers
- Added FontWeight enum with common presets (Thin, ExtraLight, Light, Normal, Medium, SemiBold, Bold, ExtraBold, Black)
- Enhanced lineHeight to accept numeric values directly (e.g., 1.2)
- Added support for numeric font-weight values (100-900)
- Added RadialGradientShape and RadialGradientPosition enums for more type-safe radial gradients
- Enhanced radial gradient to accept numeric values for positions (e.g., 0, 70)
- Added FlexWrap enum with standard CSS flex-wrap values (NoWrap, Wrap, WrapReverse)
- Enhanced flexWrap modifier functions to accept FlexWrap enum directly
- Added BorderStyle enum with standard CSS border-style values (None, Hidden, Dotted, Dashed, Solid, Double, Groove, Ridge, Inset, Outset)
- Enhanced border-related functions to accept BorderStyle enum directly
- Added Cursor enum with standard CSS cursor values (Auto, Default, Pointer, Text, Wait, etc.)
- Enhanced cursor modifier functions to accept Cursor enum directly
- Added TransitionProperty enum with standard CSS transition property values (All, None, Transform, Opacity, etc.)
- Added TransitionTimingFunction enum with standard CSS timing function values (Ease, Linear, EaseIn, EaseOut, etc.)
- Added time unit extensions for Number (s, ms) for easier time value creation
- Enhanced transition-related functions to accept enums and numeric values directly
- Added parameterized transition function with support for all transition properties

### Removed
- Removed old unnecessary border extensions (borderTop, borderRight, borderBottom, borderLeft)
- Removed duplicate border functions from LayoutModifiers.kt

### Changed
- Improved ThemeConfig to support both string-based maps (for backward compatibility) and typed properties
- Enhanced theme documentation with comprehensive examples
- Modified text-specific modifiers (fontFamily, fontWeight, textAlign, etc.) to require TextComponent parameter
- Modified media-specific modifiers (objectFit) to require MediaComponent parameter
- Modified scrollable-specific modifiers (scrollBehavior, scrollbarWidth) to require ScrollableComponent parameter
- Updated modifier documentation to reflect type-safe modifier changes
- Version update to 0.2.4.0 to reflect significant enhancements
- Improved color system with more comprehensive options
- Enhanced styling capabilities with additional gradient options
- Updated Color.rgba to accept float alpha values (0.0-1.0) in addition to int values (0-255)

## [0.2.3.0]

### Added
- Enhanced Quarkus integration with improved component rendering
- Comprehensive HTMX support for dynamic UI interactions
- Qute template integration for hybrid rendering approaches
- Navigation support with automatic component processing after navigation
- New `HtmxContainer` component for loading content via HTMX
- New `QuteTemplate` component for rendering Qute templates in Summon components
- Extensive logging for debugging component rendering issues
- New documentation for Quarkus integration with detailed examples
- Added `Position`, `Overflow`, `Display`, `JustifyContent`, `JustifyItems`, `JustifySelf`, and `TextAlign` enums with proper string value representation
- Added radial gradient background functions with flexible API options

### Changed
- Improved HTML attribute handling in JvmPlatformRenderer
- Enhanced component processing with support for raw HTML content
- Updated documentation structure with new examples and guides
- Refactored navigation support for better integration with HTMX

### Fixed
- Fixed issue with HTMX attributes being rendered incorrectly
- Fixed component rendering issues with malformed HTML
- Fixed navigation issues with duplicate navigation bars
- Fixed issues with buttons not working due to incorrect attribute rendering
- Fixed issues with components showing "Loading..." messages instead of actual content
- Fixed HTML structure issues with nested components
- Fixed issues with escaped HTML entities in component rendering
- Fixed issue with rem number extension not working with padding and margin functions

## [0.2.2.0]

### Added
- Server-side rendering (SSR) capabilities for improved performance
- Enhanced theme system with dark mode support
- Accessibility improvements with ARIA support
- SEO features with meta tag support
- Improved documentation with more examples and guides

### Changed
- Refactored component system for better performance
- Improved styling system with more flexible modifiers
- Enhanced state management with better reactivity
- Updated dependencies to latest versions

### Fixed
- Fixed issues with component rendering in nested structures
- Fixed styling inconsistencies across platforms
- Fixed state management issues with complex state objects
- Fixed navigation issues with dynamic routes

## [0.2.1.1]

### Fixed
- Updated Quarkus integration path handling to improve route mapping
- Fixed inconsistent route handling for navigation links in Quarkus example

### Known Issues
- The Quarkus example navigation still exhibits inconsistent behavior with some routes. We're actively investigating and will address in a future update. For now, using absolute paths is recommended.

## [0.2.1.0]

### Added
- Comprehensive internationalization (i18n) framework
- Support for multiple languages with easy configuration
- Right-to-left (RTL) layout support for languages like Arabic and Hebrew
- Direction-aware modifiers (paddingStart, paddingEnd, marginStart, marginEnd, etc.)
- JSON-based translation system with nested key support
- Language switching at runtime
- Automatic HTML direction attribute handling
- Documentation and examples for i18n implementation

### Changed
- Updated README to include i18n features
- Improved directory structure for translation resources

## [0.2.0.1]

### Added
- Complete Next.js-style file-based routing system with code generation
- Automatic page discovery based on file structure
- Build-time route registration for improved performance
- Fixes for the routing Link component API
- Support for dynamic route parameters with [param] filename syntax

### Changed
- Adopted semantic versioning with patch point releases (MAJOR.MINOR.PATCH.POINT)
- Fixed Button component parameter naming consistency
- Improved error handling in router implementations

## [0.2.0]

### Added
- Cross-platform routing system with URL pattern matching
- Enhanced state management with reactive updates
- Integration with Quarkus, Ktor, and Spring Boot frameworks
- Improved accessibility features and documentation
- Additional UI components for common use cases

### Changed
- Updated documentation structure
- Standardized API across platforms
- Improved type safety in modifiers

## [0.1.6]

### Added
- Comprehensive security system with JWT authentication support
- Role-based access control (RBAC) with hierarchical roles
- Security-aware components for conditional rendering
- Security annotations for declarative security
- Route guards for protecting routes
- CORS and CSRF protection
- Session management
- Security configuration builder
- Security API reference documentation

### Changed
- Updated JWT authentication to handle tokens from backend
- Improved security documentation
- Enhanced README with security features

## [0.1.5]

### Added
- Dedicated `AutoMarginModifiers` package with specialized centering modifiers
- `marginHorizontalAutoZero()` and `marginVerticalAutoZero()` convenience functions for unambiguous usage
- Improved import guidance in documentation

### Changed
- Fixed import ambiguity issues by adding proper deprecation annotations for duplicate modifier functions
- Clarified documentation with correct import statements
- Added examples demonstrating proper usage of auto margin modifiers

## [0.1.4]

### Added
- Auto margin modifiers (`marginHorizontalAuto`, `marginVerticalAuto`, `marginAuto`)

## [0.1.3]

### Added
- Initial public release of core functionality
- Basic layout modifiers
- Styling modifiers
- Component system 
