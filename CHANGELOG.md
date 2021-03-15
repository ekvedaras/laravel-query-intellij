<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Laravel Query Changelog

## [Unreleased]
### Fixed
- Resolve table name from model when inside closure like for example inside when() method. #16

## [1.0.0]
### Added
- Resolve table name from model when inside model's scope method

## [0.0.5]
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
