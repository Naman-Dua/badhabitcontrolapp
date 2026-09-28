# MVP Build Plan

## Phase 1 — Project Foundation

- Create Android Studio project.
- Configure Kotlin and Compose.
- Add Room and DataStore.
- Create database entities/DAOs.
- Seed the two default habits.

## Phase 2 — Core Home

- Build dark calm theme.
- Build two independent habit cards.
- Add large current streak display.
- Add habit detail navigation.

## Phase 3 — Habit Detail + Calendar

- Build habit detail screen.
- Build month calendar.
- Display clean/managed/relapse/today states.
- Add longest streak and total clean days.

## Phase 4 — Urge Intervention

- Add urge setup.
- Add intensity selection.
- Add trigger selection.
- Add 3/5/10/15 minute timer.
- Add wave visualization.
- Add outcome screen.
- Persist urge events.

## Phase 5 — Relapse + Streak Engine

- Implement deterministic streak calculations.
- Implement relapse behavior.
- Preserve historical streaks.
- Add tests for both habits independently.

## Phase 6 — Statistics

- Add urge totals.
- Add managed urges.
- Add relapse totals.
- Add management rate.
- Add trigger frequency.

## Phase 7 — Notifications

- Implement optional local reminders.
- Implement configurable notification preferences.

## Phase 8 — Quality

- UI tests for critical flows.
- Unit tests for streak calculations.
- Database tests.
- Rotation/recomposition checks.
- Background/resume timer checks.
- Accessibility checks.
- Offline behavior verification.

## MVP Definition of Done

A user can complete the entire product loop without internet:

`Open → See streak → Feel urge → Start intervention → Record outcome → View updated history`
