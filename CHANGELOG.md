<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Laravel Query Changelog

## [Unreleased]
### Fixed
- Column reference resolving is now context aware, so it will not resolve to all columns of the same name if it knows the table.

## [0.0.4]
### Fixed
- An edge case for class casting while resolving model relation table name.

## [0.0.3]
### Updated
- Remove upper limit for IDE version to allow EAP builds.

## [0.0.2]
### Fixed
- Stack overflow inside JoinClause closure.

## [0.0.1]
### Added
- Initial release. Main features:
    - Table and column completion and inspection on query builder methods
    - Table alias support
    - Model table name resolving
    - Relation table name resolving
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)

[Unreleased]: https://github.com/ekvedaras/laravel-query-intellij/compare/v0.0.4...HEAD
[0.0.4]: https://github.com/ekvedaras/laravel-query-intellij/compare/v0.0.3...v0.0.4
[0.0.3]: https://github.com/ekvedaras/laravel-query-intellij/compare/v0.0.2...v0.0.3
[0.0.2]: https://github.com/ekvedaras/laravel-query-intellij/compare/v0.0.1...v0.0.2
[0.0.1]: https://github.com/ekvedaras/laravel-query-intellij/releases/tag/v0.0.1
