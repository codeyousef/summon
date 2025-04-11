# Changelog

All notable changes to this project will be documented in this file.

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