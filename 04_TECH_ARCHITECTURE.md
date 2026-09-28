# Technical Architecture

## 1. Recommended Stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Room
- Kotlin Coroutines
- ViewModel
- Repository pattern
- DataStore Preferences
- Android notification APIs

## 2. Architecture

```text
Compose UI
   ↓
ViewModel
   ↓
Use Cases / Domain Logic
   ↓
Repository
   ↓
Room / DataStore
```

Keep business rules out of composables.

## 3. Suggested Package Structure

```text
com.example.badhabitcontrol/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── HabitDao.kt
│   │   ├── DayRecordDao.kt
│   │   └── UrgeEventDao.kt
│   ├── entity/
│   └── repository/
├── domain/
│   ├── model/
│   ├── streak/
│   └── usecase/
├── ui/
│   ├── home/
│   ├── habit/
│   ├── urge/
│   ├── history/
│   ├── settings/
│   └── components/
├── notifications/
└── MainActivity.kt
```

## 4. Screens

- HomeScreen
- HabitDetailScreen
- UrgeSetupScreen
- UrgeTimerScreen
- UrgeOutcomeScreen
- HistoryScreen
- SettingsScreen

## 5. State Handling

Use immutable UI state models exposed by ViewModels.

The timer should derive remaining time from a monotonic/time-safe source rather than incrementing a counter blindly every second.

The timer must survive recomposition and behave correctly when the activity is backgrounded and resumed.

## 6. Notifications

Use local notifications only.

Examples:

- Optional daily check-in reminder
- Optional custom reminder
- Optional reminder when a selected urge timer completes if the app is backgrounded

Notifications must be opt-in/configurable.

## 7. Privacy

The MVP should:

- Store sensitive records locally.
- Avoid analytics containing habit content.
- Avoid sending habit records to third parties.
- Avoid mandatory accounts.
- Avoid unnecessary permissions.

## 8. Accessibility

- Support system font scaling.
- Maintain strong text/background contrast.
- Provide content descriptions for meaningful icons.
- Do not rely on color alone to distinguish calendar states.
- Make touch targets accessible.

## 9. Error Handling

- Never silently discard an event.
- Prevent duplicate writes from repeated taps.
- Show simple neutral error states.
- Keep local database operations transactional where multiple records must update together.
