<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Laravel Query Changelog

## [Unreleased]
## [2.0.0]
### Added
- Index completion and reference resolving in migration files

### Changed
- Upgraded to latest gradle plugin
- Upgraded  platform version to `2021.1.1`

### Fixed
- `create`, `update`, etc. `Eloquent` methods should only complete columns
- Inspection errors when using nested array method in `where` methods (#35)
- Class casting issues (#25)
- Read access not allowed issues in `DB::table('<caret>')` (#26)

## [1.1.0]
### Added
- Schema builder integration

## [1.0.3]
### Updated
- Slightly improve completion sorting.
### Added
- Support for `DB::table()`

## [1.0.2]
### Fixed
- Rare error when resolving parent PSI element.
- Exceptions when dealing with `PhpClassAlias`. #21
- Operator param detection. #20

## [1.0.1]
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
