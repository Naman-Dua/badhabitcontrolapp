# Bad Habit Control

A calm, private, offline-first native Android application to help manage two specific habits (**Cigarettes** and **Masturbation**) through urge-wave delay timers and clean streak tracking.

## Download

Get the latest installable Android APK from [**GitHub Releases**](https://github.com/Naman-Dua/badhabitcontrolapp/releases/latest).

## Core Features

- **Dual Independent Habit Tracking**: Independent streaks, history, and records for Cigarettes and Masturbation.
- **Calm, High-Contrast Dashboard**: Prominent "DAYS CLEAN" counter with zero gamification (no badges, XP, or quotes).
- **Urge Delay Flow**: 3, 5, 10, or 15-minute urge-wave intervention timer with smooth breathing wave animation and quick trigger selection.
- **Calendar & Analytics**: Browsable month calendar with accessible state markers (`✓ Clean`, `⚡ Urge Managed`, `× Relapse`, `● Today`) and trigger frequency distribution.
- **100% Offline & Private**: All data stored locally on-device via Room SQLite. Zero accounts, cloud sync, or analytics.

## Tech Stack

- **Platform**: Android (Min SDK 26, Target SDK 34)
- **Language**: Kotlin 2.0.20
- **UI**: Jetpack Compose & Material 3
- **Database**: Room 2.6.1 (SQLite)
- **Preferences**: Jetpack DataStore

## Build & Run

Clone the repository and run:

```bash
# Run unit tests
./gradlew testDebugUnitTest

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```
