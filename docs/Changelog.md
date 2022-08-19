# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

### Removed


## [1.7.8] - 2022-08-19

### Added
- Added support for predictive back gesture animation on Android 13+

### Changed
- Updated dependencies
- fixed horizontal overscoll effect being applied when there is no horizontal scroll
- Add recipe FAB will collapse down to "small" state on scroll

### Removed



## [1.7.5] - 2022-07-22

### Added
- Added setting to enable/disable vibration on step change

### Changed
- Cleaned up Settings pages
- PiP animation should be smoother on Android 12+
- Added some animations when icon changes state (play/pause, current/done)

### Removed


## [1.7.4] - 2022-06-24

### Added

### Changed
- Fixed issues with Timer duration if user changed Animator Duration Scale

### Removed


## [1.7.3] - 2022-06-17

### Added
hotfix for crash

### Changed

### Removed


## [1.7.2] - 2022-06-17

### Added
- Added app shortcuts to last recipes

### Changed
- Fixed deeplinks only working when app is dead
- Fixed layout on foldable phones (like Z Fold)
- Target Android T, with language selection in app settings

### Removed


## [1.7.1] - 2022-05-27

### Added
- Ability to change current step by tapping on the name of the step

### Changed
- New animations of the text during step change
- Fixed issue with empty description showing up
- Fixed issue with steps with no time not changing FAB to the "play" icon

### Removed


## [1.7.0] - 2022-05-21

### Added
- New default recipe - AeroPress
- Ability to add default recipes again - in case you've edit them or want to get new default one without wiping app data
- Added layouts for landscape displays. App is now usable on Tablets
- Added ability to backup data to JSON and restore recipes from it

### Changed
- Updated more components to Material You
- New animation when changing between screens
- Minor performance improvements
- Fixed issues with layout in Timer if names are too long
- Fixed visibility issues in Timer in Dark Mode
- Timer is now prettier, and less square
- App bar now collapses when starting timer

### Removed


## [1.6.0] - 2022-05-08

### Added
- Missing german translations
- German store translation
- Ability to create blocking steps that require user input to progress

### Changed
- Fixed keyboard not hiding on step being added (and crash when it happens)
- Updated underlying dependencies,

### Removed


## [1.5.0] - 2022-02-11

### Added
- Ability to change step order
- Alert dialog when exiting edit/add screen with unsaved changes
- App version in settings
- Ability to clone recipes

### Changed
- Fix text selection colors
- Fix about page list item icon alignment
- Fix wait step ime keyboard action
- Fix saving step without a name
- Removed a few kb from app size
- Retroactively changed previous release to 1.4.0
- Scroll to progress indicator on recipe start

### Removed


## [1.4.0 (1.8.0 via error)] - 2022-01-30

### Added
- Proper changelog
- Added rudimentary validation in Recipe Edit page

### Changed
- Replaced webview implementation of Open Source Licenses with one written in Compose
- Fixed issue where app would misbehave and/or crash if user revoke PiP permission
- Changed design on step add card
- Fixed PiP setting toggle not working

### Removed

## [1.3.4] - 2022-01-18

### Added
- German language [@lneugebauer](https://github.com/lneugebauer)
- Ability to cancel out of step edit [@lneugebauer](https://github.com/lneugebauer)

### Changed
- Added missing Polish strings
- Fixed crash on going back to full screen

### Removed
