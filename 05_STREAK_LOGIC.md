# Streak and Event Logic

## 1. Definitions

### Clean Day
A calendar day in which the selected habit has not been recorded as a relapse.

### Managed Urge
An urge intervention that ends with `PASSED` or is otherwise explicitly recorded as successfully resisted.

### Relapse
The user explicitly records that they returned to the selected habit after attempting to avoid it.

## 2. Current Streak

The current streak belongs to one habit only.

A relapse breaks the streak for that habit.

The other habit is unaffected.

## 3. Historical Streaks

Historical streak periods remain available after a relapse.

Example:

```text
Previous streak: 27 days
Relapse
Current streak: 0 days
Previous 27-day streak: preserved
```

## 4. Recommended Source-of-Truth Model

Use dated records rather than mutating a single counter whenever possible.

This avoids inconsistencies after:

- Editing dates
- Timezone changes
- App reinstalls with restored data
- Missed days
- Multiple events on one day

## 5. Same-Day Rules

Define one explicit policy before implementation.

Recommended MVP policy:

- One habit can have many urge events on one day.
- A day can have one final day-level status for calendar display.
- If a relapse occurs on a day, that day displays as `RELAPSE`.
- Managed urges can still be visible in event history even when the day is ultimately a relapse day.

## 6. Edge Cases

Test at minimum:

- First-ever day
- Midnight crossing during a timer
- Relapse immediately after a clean streak
- Multiple urges in one day
- Multiple relapses in one day
- App reopened after midnight
- Device timezone change
- App killed during timer
- Calendar opened on a previous month
- Both habits receiving events on the same date
