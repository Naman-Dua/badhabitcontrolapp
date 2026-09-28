# Bad Habit Control

A calm, private, offline-first native Android application engineered to help individuals overcome two specific behavioral habits—**Cigarettes** and **Masturbation**—through immediate urge-delay intervention, deterministic streak tracking, and non-judgmental historical records.

[![Build & Test](https://github.com/Naman-Dua/badhabitcontrolapp/actions/workflows/build.yml/badge.svg)](https://github.com/Naman-Dua/badhabitcontrolapp/actions/workflows/build.yml)
[![Release APK](https://github.com/Naman-Dua/badhabitcontrolapp/actions/workflows/release.yml/badge.svg)](https://github.com/Naman-Dua/badhabitcontrolapp/actions/workflows/release.yml)
[![Latest Release](https://img.shields.io/github/v/release/Naman-Dua/badhabitcontrolapp?color=4EBA88&label=Latest%20APK)](https://github.com/Naman-Dua/badhabitcontrolapp/releases/latest)
[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B%20%28API%2026%2B%29-3DDC84?logo=android)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## Table of Contents

- [Overview & Philosophy](#overview--philosophy)
- [Download & Installation](#download--installation)
- [Key Features](#key-features)
- [Explicit Non-Goals & Privacy Boundaries](#explicit-non-goals--privacy-boundaries)
- [System Architecture](#system-architecture)
  - [Project Directory Structure](#project-directory-structure)
  - [Architecture Layers](#architecture-layers)
  - [Database Schema (Room SQLite)](#database-schema-room-sqlite)
  - [Deterministic Streak Engine](#deterministic-streak-engine)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started (Local Development)](#getting-started-local-development)
  - [1. Clone Repository](#1-clone-repository)
  - [2. Configure Local SDK Environment](#2-configure-local-sdk-environment)
  - [3. Run Automated Tests](#3-run-automated-tests)
  - [4. Build APKs](#4-build-apks)
  - [5. Install to Connected Android Device](#5-install-to-connected-android-device)
- [Testing](#testing)
- [CI/CD & Release Automation](#cicd--release-automation)
- [Troubleshooting & FAQ](#troubleshooting--faq)
- [Contributing](#contributing)
- [License](#license)

---

## Overview & Philosophy

Most habit and addiction tracking apps rely heavily on gamification—flashy animations, XP bars, badges, motivational quotes, or public leaderboards. For sensitive, deeply personal habits like smoking or compulsive masturbation, this approach frequently backfires: failure triggers shame ("You broke your 30-day streak!"), while artificial milestones distract from the actual psychological urge.

**Bad Habit Control** operates on a fundamentally different philosophy:
- **Calm & Spacious**: Deep matte dark surfaces, clear typography, and zero visual clutter.
- **Urge Delay as Core Intervention**: When craving strikes, the app provides a focused delay timer (3, 5, 10, or 15 minutes) with a slow, breathing sinusoidal wave animation to help ride out the neurochemical urge wave.
- **No Guilt / No Shame**: A relapse resets only the active streak for that specific habit; all prior clean days, longest streak records, and intervention events remain permanently preserved.
- **Strictly Local & Private**: Sensitive personal records never leave your physical device. No account creation, no analytics tracking, no third-party SDKs, and no cloud databases.

---

## Download & Installation

You can download the compiled production APK directly to your Android device without compiling from source:

1. Navigate to [**GitHub Releases**](https://github.com/Naman-Dua/badhabitcontrolapp/releases/latest).
2. Download the latest `BadHabitControl-vX.Y.Z.apk` asset.
3. On your Android phone, open the downloaded file and confirm installation (allow installation from your browser/files app if prompted).
4. Launch **Bad Habit Control** directly from your app drawer.

---

## Key Features

### 1. Dual Independent Habit Dashboard
- Separate, isolated tracking for **Cigarettes** and **Masturbation**.
- Large, bold streak counter displaying `DAYS CLEAN` and the baseline tracking timestamp.
- Immediate one-tap access to the `I'M HAVING AN URGE` intervention flow.
- Relapsing or logging an urge for one habit **never** touches or alters the other.

### 2. Urge Wave Delay Timer
- **Intensity Rating**: Categorize cravings as `Low`, `Medium`, or `High`.
- **Flexible Waiting Intervals**: Selectable pauses: `3 MIN`, `5 MIN`, `10 MIN`, or `15 MIN`.
- **Zero-Friction Trigger Selection**: One-tap trigger chips (`Stress`, `Boredom`, `Alone`, `Social`, `Habit/Routine`, `Random/Other`) without requiring free-text typing.
- **Calm Sinusoidal Wave Canvas**: A smooth, procedurally drawn dual sine wave that animates with a slow, meditative breathing rhythm.
- **Monotonic Clock**: The countdown derives remaining time from `SystemClock.elapsedRealtime()`, ensuring accuracy across screen rotation, recomposition, and backgrounding.
- **Outcome Classification**:
  - `Urge Passed`: Logs a managed urge and preserves the clean calendar day.
  - `Still an Urge`: Acknowledges ongoing difficulty without penalizing or breaking the clean streak.
  - `Relapse`: Records an explicit relapse neutrally without shame language.

### 3. Decoupled Month Calendar
- Browsable month-by-month grid with intuitive arrow controls.
- **Decoupled Today Marker**: The current date is framed by a distinct slate-blue indicator ring (`● Today`).
- **Accessible State Symbols**:
  - `✓` Clean: No relapse recorded.
  - `⚡` Urge Managed: Successfully resisted an urge on that date.
  - `×` Relapse: Relapse recorded for that date.
- States rely on clear unicode symbols combined with muted colors, ensuring complete accessibility for color-blind users.

### 4. History & Analytics
- Tabbed switcher to review metrics for each habit independently.
- Quick statistical summary: Current Streak, Longest Streak, Total Clean Days, and Urge Management Rate (`%`).
- Trigger frequency breakdown ranking the top emotional and environmental cues.
- Chronological timeline of recent urge events and timestamped outcomes.

### 5. Settings & Data Control
- Configurable default urge delay duration (stored via Android Jetpack DataStore).
- Optional toggle for local countdown completion alerts.
- One-click `CLEAR ALL LOCAL DATA` with neutral confirmation dialog for complete data sanitization.

---

## Explicit Non-Goals & Privacy Boundaries

To maintain uncompromising trust, privacy, and simplicity, this application explicitly **excludes**:

- ❌ No user registration, email requirements, or social login.
- ❌ No backend servers, REST APIs, or GraphQL endpoints.
- ❌ No Google Firebase, Crashlytics, Telemetry, or tracking SDKs.
- ❌ No motivational quotes, aphorisms, or daily notification spam.
- ❌ No gamification: no badges, points, XP, trophies, or community leaderboards.
- ❌ No mandatory journaling or long-form free-text diaries.

---

## System Architecture

### Project Directory Structure

```text
badhabitcontrol/
├── .github/
│   └── workflows/
│       ├── build.yml                 # CI workflow: tests & builds APKs on pushes/PRs
│       └── release.yml               # CD workflow: builds & publishes tagged releases
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml   # Manifest registering app icon, permissions, MainActivity
│   │   │   ├── java/com/example/badhabitcontrol/
│   │   │   │   ├── BadHabitApp.kt    # Application class initializing Room repository
│   │   │   │   ├── MainActivity.kt   # Edge-to-edge Compose host activity
│   │   │   │   ├── data/
│   │   │   │   │   ├── entity/
│   │   │   │   │   │   └── Entities.kt       # Room Entities: Habit, DayRecord, UrgeEvent
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── AppDatabase.kt    # Room DB with default habit seeding
│   │   │   │   │   │   └── Daos.kt           # Room DAOs with atomic @Transaction operations
│   │   │   │   │   ├── model/
│   │   │   │   │   │   └── Models.kt         # Enums (DayStatus, UrgeTrigger, UrgeOutcome) & DTOs
│   │   │   │   │   └── repository/
│   │   │   │   │       └── HabitRepository.kt # Concrete repository wrapping Room & DataStore
│   │   │   │   ├── domain/
│   │   │   │   │   └── streak/
│   │   │   │   │       └── StreakCalculator.kt # Pure deterministic streak & analytics engine
│   │   │   │   └── ui/
│   │   │   │       ├── components/
│   │   │   │       │   ├── CalendarView.kt   # Month calendar with accessible symbol markers
│   │   │   │       │   └── UrgeWaveCanvas.kt # Sinusoidal breathing wave animation canvas
│   │   │   │       ├── habit/
│   │   │   │       │   └── HabitDetailScreen.kt # Habit detail, calendar, and relapse action
│   │   │   │       ├── history/
│   │   │   │       │   └── HistoryScreen.kt  # Analytics dashboard & chronological event log
│   │   │   │       ├── home/
│   │   │   │       │   └── HomeScreen.kt     # Dual habit card dashboard
│   │   │   │       ├── navigation/
│   │   │   │       │   └── AppNavigation.kt  # Bottom navigation scaffold & screen router
│   │   │   │       ├── settings/
│   │   │   │       │   └── SettingsScreen.kt # Delay preferences & local data reset
│   │   │   │       ├── theme/
│   │   │   │       │   ├── Color.kt          # Matte charcoal, sage green, and amber palette
│   │   │   │       │   └── Theme.kt          # Material 3 dark-first typography & scheme
│   │   │   │       ├── urge/
│   │   │   │       │   └── UrgeFlowScreens.kt # Urge setup, wave countdown, and outcome picker
│   │   │   │       └── viewmodel/
│   │   │   │           └── ViewModels.kt     # Compose state holders & ViewModel factory
│   │   │   └── res/
│   │   │       ├── mipmap-*/                 # Custom minimalist launcher icons (mdpi - xxxhdpi)
│   │   │       └── values/strings.xml        # App naming strings
│   │   └── test/java/com/example/badhabitcontrol/
│   │       ├── DayRecordTransitionTest.kt    # Unit tests for transactional precedence
│   │       └── StreakCalculatorTest.kt       # 11 unit tests verifying streak & edge cases
│   ├── build.gradle.kts                      # Module build script (Compose, Room, KSP, DataStore)
│   └── proguard-rules.pro                    # ProGuard rules configuration
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties         # Gradle 8.7 distribution
├── build.gradle.kts                          # Root build script (AGP 8.5.2, Kotlin 2.0.20, KSP)
├── gradle.properties                         # JVM memory flags and AndroidX configuration
├── gradlew / gradlew.bat                     # Cross-platform Gradle wrapper scripts
├── settings.gradle.kts                       # Repository management & project inclusion
└── README.md                                 # Project documentation
```

### Architecture Layers

The application follows Google's recommended Modern Android Architecture (UDF / Unidirectional Data Flow), intentionally stripped of single-implementation interfaces and unnecessary abstractions:

```
┌────────────────────────────────────────────────────────┐
│                   Jetpack Compose UI                   │
│   (HomeScreen, HabitDetailScreen, UrgeFlow, History)   │
└───────────────────────────▲────────────────────────────┘
                            │ StateFlow / Events
┌───────────────────────────┴────────────────────────────┐
│                       ViewModels                       │
│    (HomeViewModel, HabitViewModel, UrgeViewModel)      │
└───────────────▲────────────────────────▲───────────────┘
                │                        │
┌───────────────┴──────────────┐ ┌───────┴───────────────┐
│     Domain Streak Engine     │ │   HabitRepository     │
│     (StreakCalculator)       │ │  (Single Concrete)   │
└──────────────────────────────┘ └───────┬───────────────┘
                                         │
                         ┌───────────────┴───────────────┐
                         ▼                               ▼
              ┌─────────────────────┐         ┌─────────────────────┐
              │  Room SQLite DB     │         │ Jetpack DataStore   │
              │ (Habit, DayRecord,  │         │ (User Preferences:  │
              │   UrgeEvent DAOs)   │         │  Default Delay Sec) │
              └─────────────────────┘         └─────────────────────┘
```

### Database Schema (Room SQLite)

The database schema is partitioned into three relational tables:

#### 1. `habits` Table
Pre-populated on initial database creation with IDs `1` (Cigarettes) and `2` (Masturbation).

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `INTEGER` | PRIMARY KEY | Unique habit identifier |
| `name` | `TEXT` | NOT NULL | Habit name |
| `createdAt` | `INTEGER` | NOT NULL | Epoch millisecond timestamp of habit creation |
| `active` | `INTEGER` | NOT NULL DEFAULT 1 | Whether tracking is currently active |

#### 2. `day_records` Table
Stores day-level calendar statuses. Has a unique compound index on `(habitId, localDate)` to guarantee that rapid taps or multiple events on the same calendar day never generate duplicate records.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `INTEGER` | PRIMARY KEY AUTOINCREMENT | Unique record ID |
| `habitId` | `INTEGER` | FOREIGN KEY (`habits.id`) | Foreign key to associated habit |
| `localDate` | `TEXT` | NOT NULL | Calendar date in ISO format (`YYYY-MM-DD`) |
| `status` | `TEXT` | NOT NULL | `CLEAN`, `URGE_MANAGED`, or `RELAPSE` |
| `createdAt` | `INTEGER` | NOT NULL | Epoch millisecond timestamp of record update |

#### 3. `urge_events` Table
Preserves the complete, tamper-proof history of every intervention attempted by the user.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `INTEGER` | PRIMARY KEY AUTOINCREMENT | Event ID |
| `habitId` | `INTEGER` | FOREIGN KEY (`habits.id`) | Foreign key to associated habit |
| `createdAt` | `INTEGER` | NOT NULL | Timestamp of event completion |
| `intensity` | `TEXT` | NOT NULL | `LOW`, `MEDIUM`, or `HIGH` |
| `timerSeconds` | `INTEGER` | NOT NULL | Selected wait duration (`180`, `300`, `600`, `900`) |
| `trigger` | `TEXT` | NOT NULL | `STRESS`, `BOREDOM`, `ALONE`, `SOCIAL`, `HABIT_ROUTINE`, `RANDOM_OTHER` |
| `outcome` | `TEXT` | NOT NULL | `PASSED`, `STILL_URGE`, or `RELAPSE` |

### Deterministic Streak Engine

Rather than mutating a fragile counter in preferences, streaks are computed deterministically from date records by [StreakCalculator.kt](file:///d:/newapp/app/src/main/java/com/example/badhabitcontrol/domain/streak/StreakCalculator.kt):

1. **Clean Status**: Both `CLEAN` and `URGE_MANAGED` count as clean calendar days.
2. **Current Streak**:
   - If today is recorded as `RELAPSE`: Current streak is immediately `0`.
   - If today is clean (`CLEAN` or `URGE_MANAGED`): Streak starts at `1` and traverses backwards consecutively until an untracked gap or `RELAPSE` is encountered.
   - If today has no entry yet (e.g., middle of the day): Streak counts consecutive clean days backwards starting from yesterday.
3. **Longest Streak**: Computes the maximum consecutive clean run across the entire historical record set. It is permanently preserved when a relapse occurs.
4. **Same-Day Transactional Precedence**: Handled via Room `@Transaction` in [Daos.kt](file:///d:/newapp/app/src/main/java/com/example/badhabitcontrol/data/local/Daos.kt):
   - A `RELAPSE` recorded after an `URGE_MANAGED` on the same date atomically updates the day status to `RELAPSE`.
   - An `URGE_MANAGED` recorded on a day already marked as `RELAPSE` will **never** downgrade the relapse status.

---

## Tech Stack

| Component | Technology | Version | Purpose |
|---|---|---|---|
| **Language** | Kotlin | `2.0.20` | Core programming language |
| **Build System** | Gradle | `8.7` | Build automation |
| **Android Plugin** | AGP | `8.5.2` | Android build configuration |
| **Target SDK** | Android 14 (API 34) | `34` | Modern platform compatibility |
| **Minimum SDK** | Android 8.0 (API 26) | `26` | Java 8+ / `java.time` runtime support |
| **UI Framework** | Jetpack Compose | BOM `2024.09.00` | Declarative UI |
| **Compiler Plugin** | Compose Compiler | `2.0.20` | Official Kotlin 2.x Compose compiler |
| **Design System** | Material 3 | Compose BOM | Calm dark color scheme and components |
| **Database** | Room | `2.6.1` | Local SQLite abstraction with KSP compiler |
| **Preferences** | Jetpack DataStore | `1.1.1` | Asynchronous key-value settings storage |
| **Navigation** | Navigation Compose | `2.8.0` | File-based declarative routing |
| **Async / Streams** | Kotlin Coroutines & Flow | `1.8.1` | Reactive state management |
| **Testing** | JUnit 4 & Coroutines Test | `4.13.2` | Pure unit and state-transition tests |

---

## Prerequisites

Before building from source, ensure your development environment has:

- **Operating System**: Windows 10/11, macOS, or Ubuntu Linux.
- **Java Development Kit (JDK)**: JDK 17 (recommended) or JDK 21.
- **Android SDK**: Android SDK Platform 34 and Build-Tools `34.0.0` installed (typically managed through Android Studio or `sdkmanager`).
- **Git**: Git 2.30+ installed.

---

## Getting Started (Local Development)

### 1. Clone Repository

```bash
git clone https://github.com/Naman-Dua/badhabitcontrolapp.git
cd badhabitcontrolapp
```

### 2. Configure Local SDK Environment

Create a `local.properties` file in the root directory pointing to your local Android SDK location:

**Windows (`local.properties`):**
```properties
sdk.dir=C\:\\Users\\<YOUR_USERNAME>\\AppData\\Local\\Android\\Sdk
```

**macOS / Linux (`local.properties`):**
```properties
sdk.dir=/Users/<YOUR_USERNAME>/Library/Android/sdk
# or /home/<YOUR_USERNAME>/Android/Sdk on Linux
```

> [!NOTE]
> `local.properties` is already included in `.gitignore` and must never be committed.

### 3. Run Automated Tests

Execute the unit test suite verifying the streak engine and transactional state transitions:

**Windows PowerShell:**
```powershell
.\gradlew.bat testDebugUnitTest
```

**macOS / Linux:**
```bash
chmod +x gradlew
./gradlew testDebugUnitTest
```

Expected output:
```text
> Task :app:compileDebugUnitTestKotlin
> Task :app:testDebugUnitTest
BUILD SUCCESSFUL in 12s
```

### 4. Build APKs

**Build Debug APK:**
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

**Build Production Release APK:**
```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

### 5. Install to Connected Android Device

Ensure USB debugging is enabled on your phone and verify your device is recognized:

```bash
adb devices
```

Install and launch the release build:

```bash
adb install -r -d app/build/outputs/apk/release/app-release.apk
adb shell am start -n com.example.badhabitcontrol/.MainActivity
```

---

## Testing

The codebase includes targeted automated unit test suites covering the core domain logic:

- **[StreakCalculatorTest.kt](file:///d:/newapp/app/src/test/java/com/example/badhabitcontrol/StreakCalculatorTest.kt)**:
  - First-day creation with clean, unrecorded, or relapse states.
  - Multi-day clean streak counting.
  - Traversal when today has no record yet (counts through yesterday).
  - Relapse on today dropping current streak to 0 while preserving historical longest streak.
  - Streak recovery on subsequent days following a relapse.
  - Accurate calculation of total clean calendar days.
  - Urge management rate calculation (`(managed / completed) * 100`).
  - Trigger frequency distribution aggregation.
- **[DayRecordTransitionTest.kt](file:///d:/newapp/app/src/test/java/com/example/badhabitcontrol/DayRecordTransitionTest.kt)**:
  - Atomicity of same-day status transitions.
  - `RELAPSE` overriding an earlier `URGE_MANAGED` on the same date.
  - `URGE_MANAGED` never overwriting a previously recorded `RELAPSE`.
  - Habit isolation: verifying that events recorded for Cigarettes never alter Masturbation records.

Run all tests anytime with:
```bash
./gradlew test
```

---

## CI/CD & Release Automation

The repository includes two GitHub Actions workflows configured in `.github/workflows/`:

### 1. Continuous Integration (`build.yml`)
- **Trigger**: Every push or pull request to the `main` branch.
- **Actions**:
  - Sets up Temurin JDK 17.
  - Strips local machine Java path definitions.
  - Runs `./gradlew testDebugUnitTest`.
  - Compiles both debug and release APKs.
  - Uploads the resulting APKs as workflow artifacts available for 14 days.

### 2. Automated Release Pipeline (`release.yml`)
- **Trigger**: When a version tag matching `v*` (e.g., `v1.0.0`) is pushed to the repository, or via manual `workflow_dispatch`.
- **Actions**:
  - Compiles the signed release APK.
  - Automatically creates a GitHub Release entry with auto-generated release notes.
  - Attaches `BadHabitControl-vX.Y.Z.apk` directly to the release page for direct download.

To trigger a new release:
```bash
git tag v1.0.1
git push origin v1.0.1
```

---

## Troubleshooting & FAQ

### 1. `PowerShell: npm.ps1 / gradlew cannot be loaded because running scripts is disabled`
- **Cause**: Windows PowerShell execution policy defaults to restricted for third-party scripts.
- **Fix**: Use `.\gradlew.bat` in Command Prompt / PowerShell, or bypass the execution policy for the current session:
  ```powershell
  Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
  ```

### 2. `The specified Java home does not exist`
- **Cause**: A hardcoded `org.gradle.java.home` in `gradle.properties` points to a path not found on your current machine.
- **Fix**: Open `gradle.properties` and remove or update the `org.gradle.java.home` line to match your system's JDK installation.

### 3. `adb: no devices/emulators found`
- **Cause**: USB debugging is disabled or the computer is not yet authorized on the device.
- **Fix**: Unplug and reconnect the USB cable. On your Android device, look for the dialog prompt: **"Allow USB debugging?"** and check **"Always allow from this computer"**, then tap **Allow**.

### 4. `INSTALL_FAILED_UPDATE_INCOMPATIBLE`
- **Cause**: An existing build of the app signed with a different signature is currently installed on the phone.
- **Fix**: Uninstall the existing app from your device first (`adb uninstall com.example.badhabitcontrol`), then run `adb install` again.

---

## Contributing

Contributions that uphold the core product principles (**calm, private, non-gamified, offline-first**) are welcome.

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/improvement-name`.
3. Verify that all unit tests pass: `./gradlew testDebugUnitTest`.
4. Commit your changes: `git commit -m "feat: description of improvement"`.
5. Push to your branch: `git push origin feature/improvement-name`.
6. Open a Pull Request.

---

## License

This project is licensed under the [MIT License](LICENSE).
