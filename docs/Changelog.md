# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

### Removed


## [1.21.8|1.12.8] - 2024-07-12

### Added

### Changed
- fixed Wear OS font scaling issues

### Removed
- Removed notification permission from WearOS


## [1.21.7|1.12.7] - 2024-06-14

### Added

### Changed

### Removed


## [1.21.6|1.12.6] - 2024-06-14

### Added

### Changed

### Removed


## [1.21.5|1.12.5] - 2024-05-17

### Added

### Changed
- Default to empty duration when creating step
- fixed crash when editing recipe
- fixed issues with increased font size in the list screen
- hopefully fixed issues with recipes and timer going invisible after a while

### Removed


## [1.21.3|1.12.3] - 2024-04-27

### Added
- Added support for Predictive Back
### Changed
- Changed design of a few elements to better reflect Material You
- Fixed issue where Support dialog could show up in PiP mode
- changed NumberInput behaviour to make it less frustrating
### Removed


## [1.20.1|1.11.1] - 2023-10-06

### Added
- Timer will now try to continue in the background when the app isn't in the foreground

### Changed
- Updated translations

### Removed


## [1.20.0|1.11.0] - 2023-09-13

### Added
- Timer will now try to continue in the background when the app isn't in the foreground

### Changed

### Removed


## [1.19.2|1.10.2] - 2023-08-06

### Added
- added a small animation to the end of the recipe on weaOS
- added two new icons - tea, and a teapot. Thanks to the patronage of @SeriousBug

### Changed
- WearOS will now show recipes in the same order as Android
- Improved rotary input on WearOS
- Improved UI/UX of timer page on WearOS
- Fixed ambient mode support on WearOS
- Fixed Up Next not scaling with the recipe multipliers
- Changed icon of "other" step type

### Removed


## [1.19.1|1.10.1] - 2023-07-07

### Added

### Changed
- fixed crash on WearOS
- added haptics on long press step
- fixed recipe edit style on tablets/foldables

### Removed

## [1.19.0|1.10.0] - 2023-07-01

### Added
- New default recipes - 1 Cup V60, Clever Dripper
- Added a button to open description on WearOS
- Inputs to change weight of the recipe in recipe multiplier modal

### Changed
- Tweaked transition animation between screens
- Tweaked modals across the app
- Tweak design of adding steps - sorry for broken muscle memory
- Fixed layout on tablets and foldables
- Improved number input responsibility

### Removed
- Removed slider from weight multiplier in favor of chips and input

## [1.18.7|1.9.7] - 2023-05-26

### Added
- Added tooltips when long pressing icons in icon picker

### Changed
- fixed Bottom sheet not going under system bars
- Fixed "show next step" toggle in settings
- Fixed time multiplier not changing seconds in step name

### Removed
- Removed background behind navigation bar if user uses gesture navigation


## [1.18.6|1.9.6] - 2023-05-15

### Added

### Changed
- Shortcuts will now get removed when removing the recipe
- Changing steps during recipe now requires a long press

### Removed


## [1.18.1|1.9.1] - 2023-03-26

### Added
- Added Hindi translation - Thanks @AmaryllisVFX
- Added Chinese translation - Thanks @mrz5802
- Added Ukrainian translation - Thanks @Kefir2105
- Added French translation - Thanks @J. Lavoie
- Added Portuguese translation - Thanks @fnogcps

### Changed
- Improved translations

### Removed


## [1.18.0|1.9.0] - 2023-03-01

### Added
- Next step will now be displayed near the timer (you can turn that off in settings)
- added missing German translations
- Added "Support Cofi" Dialog when user finishes their second recipe and in the settings
- Added button to download WearOS app in settings
- Added box with info that app has been updated with a link to this changelog
### Changed
- Tweaked Bottom Sheets across the app to be more Material You-like
### Removed


## [1.14.0|1.5.0] - 2023-02-03

### Added
- two new icons: a cup, and a cezve - sponsored by @YurishoSan

### Changed
- Step value can now be expressed with a decimal point
- fixed issues with how multiplier worked
- updated dependencies

### Removed


## [1.13.2|1.4.2] - 2023-01-05

### Added
- Norwegian (Bokmål) translation - thanks @deltainium

### Changed
- fixed crash on WearOS 2 devices
- changed style of timer on Android app
- added animations to step change in both WearOS and Android app
- fixed highlight when clicking on an edge of the Start FAB
### Removed


## [1.13.1|1.4.1] - 2022-12-27

### Added
- Italian translation - thanks @cmendoza2000
- New recipe icons to choose - sponsored by @ThatOneCalculator

### Changed

### Removed


## [1.13.0|1.4.0] - 2022-12-16

### Added
- Added settings page to the WearOS app
- Added multipliers to the WearOS app
- Added Position indicator, Vignette, and Rotary control to the WearOS app

### Changed
- Change milliseconds display

### Removed


## [1.11.2] - 2022-12-06

### Added
- Added Always on Display when timer is running
- If wearOS device doesn't support AoD then keep screen on during timer

### Changed
- fixed keyboard issue when adding steps after certain number
- changed Icon Bottom Sheet during recipe edit slightly

### Removed


## [1.11.1] - 2022-11-29

### Added
- Added a wearOS app
- separated Play Store and Open Source builds

### Changed
- fixed performance issues with timer when there are a lot of short steps
- fixed potential crash when editing the recipe
- fixed weight calculation when using multipliers

### Removed


## [1.11.0] - 2022-11-27 [Pre-release]

### Added
- Added a wearOS app
- separated Play Store and Open Source builds
- fixed performance issues with timer when there are a lot of short steps

### Changed

### Removed


## [1.9.1] - 2022-11-02

### Added

### Changed
- fixed already done weight calculation when using multiplier

### Removed


## [1.9.0] - 2022-11-01

### Added
- Added ability to multiply all values of the recipe without editing the recipe itself

### Changed
- Improved performance of app bar collapse
- updated dependencies

### Removed


## [1.8.0] - 2022-10-07

### Added
- Show recipe basic info in timer and on main recipe list screen
- WYSIWYG in description input
- Added "Cancel" button to Dialogs that were missing it

### Changed
- Back button now closes icon picker bottom sheet in recipe edit
- Tweaked scrolling into view of inputs in recipe edit page
- Description input is hidden by default to indicate that it isn't required
- Tweaked layout of Timer, StepAddCard, Description, Recipe Edit, Settings
- Fixed adding default recipes

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
