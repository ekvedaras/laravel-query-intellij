# Laravel Query

![Build](https://github.com/ekvedaras/laravel-query-intellij/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/16309.svg)](https://plugins.jetbrains.com/plugin/16309)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/16309.svg)](https://plugins.jetbrains.com/plugin/16309)

<img src="https://raw.githubusercontent.com/ekvedaras/laravel-query-intellij/main/src/main/resources/META-INF/pluginIcon.svg" width="192" height="192"/>

<!-- Plugin description -->
## Laravel + DataGrip = ♥️

<img src="https://user-images.githubusercontent.com/3586184/110513603-b4522000-8106-11eb-9678-985bf286bf4f.gif" alt="Demo" width="350" height="353"/>

This plugin provides database integration for Laravel query builder.

## Features

* Schemas, tables, views and columns completion for query builder methods
* Inspection of unknown database elements
* Table alias support
* Table name resolving from model for eloquent builder methods
* Model relation table name resolving for eloquent builder relation closure methods
* Text linking with database elements for navigation and refactoring

## Prerequisites

### Connect your database
See <https://www.jetbrains.com/help/phpstorm/connecting-to-a-database.html#connect-to-mysql-database> for instructions.

### Laravel tools

You also need either [Laravel Idea](https://laravel-idea.com) plugin (paid) or [Laravel IDE helper](https://github.com/barryvdh/laravel-ide-helper) added to your project and run 
```shell
php artisan ide-helper:generate
php artisan ide-helper:meta
php artisan ide-helper:models
php artisan ide-helper:eloquent
```
which will generate some helper files so your IDE could see Eloquent methods.

Laravel Query plugin needs either of those to work otherwise, it cannot understand for which methods to trigger autocompletion.
<!-- Plugin description end -->

## Future plans

* Improve schema builder support to read current migration file as well instead of just table from database
* Add morph columns support for schema builder integration
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
