# Bad Habit Control — Product Requirements Document

## 1. Product Overview

**Bad Habit Control** is a private, calm Android app for managing two personal habits:

1. Cigarettes
2. Masturbation

The product is centered on independent streak tracking, date-based history, and an immediate urge-intervention flow. It is intentionally not a motivational quote app, journal, social app, or achievement system.

## 2. Product Goals

- Give the user a fast way to see the current status of each habit.
- Make each habit feel independent and personally owned.
- Make streak progress highly visible.
- Help the user pause when an urge occurs.
- Record urges, managed urges, and relapses without requiring writing.
- Preserve historical progress after relapse.
- Work offline and keep sensitive data on-device in the MVP.

## 3. Non-Goals

The MVP must not include:

- Motivational quotes/messages
- Badges
- Achievements
- XP, levels, points, or leaderboards
- Milestone systems
- Journaling/free-text diary entries
- Social/community features
- Public profiles
- App lock
- Mandatory account creation
- Cloud sync
- Habit recommendation feeds

## 4. Core Experience

### Home

The home screen presents two independent habit cards. Each card shows:

- Habit name
- Large current streak
- `DAYS CLEAN`
- Since date
- `I'm Having an Urge` action
- Entry point to the habit's detail screen

### Habit Detail

Each habit has its own:

- Current streak
- Longest streak
- Total clean days
- Since date/time
- Date-based calendar
- Urge history
- Relapse history
- Trigger statistics
- Urge management statistics

### Urge Intervention

The user can start an intervention from either habit. The flow is:

1. Identify the habit.
2. Select urge intensity: Low, Medium, High.
3. Select a delay: 3, 5, 10, or 15 minutes.
4. Start a calm countdown with an urge-wave visualization.
5. Select an outcome:
   - Urge Passed
   - Still an Urge
   - Relapse
6. Optionally/select a quick trigger.
7. Save the event locally.

## 5. Trigger Options

No typing is required. Use selectable options:

- Stress
- Boredom
- Alone
- Social situation
- Habit/routine
- Random/other

## 6. Calendar States

Each habit has its own calendar. A day can show:

- Clean
- Urge Managed
- Relapse
- Today

The calendar is historical and browsable by month.

## 7. Relapse Rules

A relapse affects only the selected habit.

When a relapse is recorded:

- Current streak resets for that habit.
- Previous streak history remains.
- Longest streak remains.
- Total historical clean days remain.
- Previous urge events remain.
- Previous relapse events remain.
- The other habit is untouched.

A relapse is a recorded event, not deletion of prior effort.

## 8. Statistics

Per habit, display:

- Current streak
- Longest streak
- Total clean days
- Total urges
- Urges managed
- Relapses
- Urge management rate
- Recent history
- Most frequent selected trigger

## 9. Information Architecture

Primary navigation:

- Home
- History
- Settings

Home and History can deep-link into either habit.

## 10. MVP Success Criteria

The MVP is complete when a user can:

1. Open the app and see both streaks.
2. Open either habit independently.
3. View that habit's calendar.
4. Start an urge intervention.
5. Select intensity and timer duration.
6. Select a trigger without writing.
7. Complete an intervention and save its outcome.
8. Record a relapse.
9. See the current streak update correctly.
10. See historical streaks remain intact.
11. Close and reopen the app without losing local data.
