# Data Model

## 1. Storage Strategy

Use **Room** for structured local data.

Use **DataStore** for lightweight user preferences/settings.

Do not upload sensitive habit data to a backend in the MVP.

## 2. Entities

### Habit

| Field | Type | Description |
|---|---|---|
| id | Long | Primary key |
| name | String | `Cigarettes` or `Masturbation` |
| createdAt | Instant | Creation timestamp |
| active | Boolean | Whether the habit is active |

### DayRecord

| Field | Type | Description |
|---|---|---|
| id | Long | Primary key |
| habitId | Long | Foreign key to Habit |
| localDate | LocalDate | Calendar date |
| status | Enum | `CLEAN`, `URGE_MANAGED`, `RELAPSE` |
| createdAt | Instant | Record creation timestamp |

Unique constraint: `(habitId, localDate)`.

### UrgeEvent

| Field | Type | Description |
|---|---|---|
| id | Long | Primary key |
| habitId | Long | Foreign key to Habit |
| createdAt | Instant | Event timestamp |
| intensity | Enum | `LOW`, `MEDIUM`, `HIGH` |
| timerSeconds | Int | Selected delay |
| trigger | Enum | Quick trigger selection |
| outcome | Enum | `PASSED`, `STILL_URGE`, `RELAPSE` |

## 3. Derived Values

Do not store current streak as the source of truth unless needed for performance. Prefer calculating it from DayRecord/event history so that data remains consistent.

### Current Streak

Starting from today, count consecutive clean calendar days according to the app's explicit date rules.

### Longest Streak

Find the longest consecutive run of clean days across historical records.

### Total Clean Days

Count unique dates recorded as clean for the selected habit.

### Urge Management Rate

```text
managed urges / total completed urge events * 100
```

Only completed events with a known outcome are included in this calculation.

## 4. Habit Independence

All queries must filter by `habitId`.

A cigarette event must never modify masturbation records and vice versa.

## 5. Date Rules

- Store event timestamps as `Instant`.
- Derive calendar dates in the device's current local timezone.
- Store calendar dates as `LocalDate` where appropriate.
- Define the app's day boundary consistently.
- Test timezone changes and daylight-saving transitions even though the target user may be in a region without DST.
