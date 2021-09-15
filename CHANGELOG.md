<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Laravel Query Changelog

## [Unreleased]
### Added
- Add settings to allow configuring which schemas to inspect.

## [2.0.6]
### Fixed
- Do not inspect for unknown columns inside value array for `whereIn()`. #56
- Do not inspect for unknown columns and tables inside array indexes. #44
- Completion was not working on create/update methods called on relation. #41

### Updated
- Bump platform version to 2021.2 and resolve new deprecations.

## [2.0.5]
### Updated
- Complete columns in array keys (technically still values) in methods like `create()`, `update()`, etc. (#40)
- Make sure it properly resolves table in scenario like so: `$user->customer()->create([])` (#41)

## [2.0.4]
### Fixed
- Class casting exception when class aliases are involved. #45

## [2.0.3]
### Fixed
- Reference resolving was refactored a bit and the issue with stack overflow and non-idempotent computation exception resolved. #42

## [2.0.2]
### Fixed
- Hotfix to prevent non-idempotent computation exception. This will introduce reduced context knowledge when resolving column references, but should prevent exceptions until a proper refactor is implemented. #42

## [2.0.1]
### Fixed
- Do not inspect for tables in `create`, `update`, `fill`, etc. methods #38
- Do not complete columns in `create`, `update`, `fill`, etc. method array values. Only in keys #38
- Do not look for column and table references in `create`, `update`, `fill`, etc. method array values. Only in keys #38
- Attempt to detect PHP array value elements by using debugName instead of various indexes #39

## [2.0.0]
### Added
- Index completion and reference resolving in migration files

### Changed
- Upgraded to latest gradle plugin
- Upgraded  platform version to `2021.1.1`

### Fixed
- `create`, `update`, etc. `Eloquent` methods should only complete columns
- Inspection errors when using nested array method in `where` methods #35
- Class casting issues #25
- Read access not allowed issues in `DB::table('<caret>')` #26

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
