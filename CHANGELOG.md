# Changelog

All notable changes to this project will be documented in this file.

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
