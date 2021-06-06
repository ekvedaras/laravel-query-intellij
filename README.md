# Laravel Query

![Build](https://github.com/ekvedaras/laravel-query-intellij/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/16309.svg)](https://plugins.jetbrains.com/plugin/16309)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/16309.svg)](https://plugins.jetbrains.com/plugin/16309)

<img src="https://raw.githubusercontent.com/ekvedaras/laravel-query-intellij/main/src/main/resources/META-INF/pluginIcon.svg" width="192" height="192"/>

<!-- Plugin description -->
## Laravel + DataGrip = ♥️

<img src="https://user-images.githubusercontent.com/3586184/110513603-b4522000-8106-11eb-9678-985bf286bf4f.gif" alt="Demo" width="350" height="353"/>

This plugin provides database integration for Laravel query builder.

**Don't forget to connect your database in order for this to work.**
See <https://www.jetbrains.com/help/phpstorm/connecting-to-a-database.html#connect-to-mysql-database> for instructions.

* Schemas, tables, views and columns completion for query builder methods
* Inspection of unknown database elements
* Table alias support
* Table name resolving from model for eloquent builder methods
* Model relation table name resolving for eloquent builder relation closure methods
* Text linking with database elements for navigation and refactoring
<!-- Plugin description end -->

## Future plans

* Improve schema builder support to read current migration file as well instead of just table from database
* Add morph columns support for schema builder integration
* Add support for indexes in schema builder
* Improve sorting

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Laravel Query"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/ekvedaras/laravel-query-intellij/releases/latest) and install it manually using
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
