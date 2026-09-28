# UI/UX Specification

## 1. Design Direction

The interface should feel **calm, private, spacious, and premium**.

The visual language should resemble a quiet personal dashboard rather than a game or a medical warning screen.

### Principles

- Large typography
- Generous spacing
- Minimal information per screen
- Soft transitions
- Dark-first design
- Neutral language
- No shame language
- No celebration effects
- No aggressive red warning states unless required for accessibility/context

## 2. Home Screen

Suggested structure:

```text
                 TODAY

             YOUR CONTROL

   ┌────────────────────────────┐
   │        CIGARETTES          │
   │                            │
   │             12             │
   │         DAYS CLEAN         │
   │                            │
   │   I'M HAVING AN URGE       │
   └────────────────────────────┘

   ┌────────────────────────────┐
   │        MASTURBATION        │
   │                            │
   │              7             │
   │         DAYS CLEAN         │
   │                            │
   │   I'M HAVING AN URGE       │
   └────────────────────────────┘

          HOME   HISTORY   SETTINGS
```

The current streak is the main visual focus.

## 3. Habit Detail Screen

```text
            CIGARETTES

                 12
             DAYS CLEAN

       Since 31 August, 8:42 PM

       Current      Longest
         12           27

       Clean Days: 84

       SEPTEMBER 2026
       M  T  W  T  F  S  S
       ✓  ✓  ✓  ✓  ✓  ✓  ✓
       ✓  ✓  ⚡ ✓  ✓  ✓  ✓
       ✓  ✓  ✓  ●  ·  ·  ·

       ✓ Clean   ⚡ Urge Managed
       × Relapse ● Today

       [ I'M HAVING AN URGE ]
```

## 4. Streak Presentation

The streak should appear large enough to be immediately readable.

Example:

```text
                 27
              DAYS CLEAN
```

Do not turn streak increases into badges, levels, or achievement popups.

## 5. Urge Start Screen

```text
             I'M HAVING AN URGE

                  CIGARETTES

             HOW STRONG?

             ○ LOW
             ○ MEDIUM
             ○ HIGH

             WAIT BEFORE ACTING

             3     5     10     15
             MIN   MIN   MIN    MIN

                    [ START ]
```

The equivalent screen for the other habit uses its own habit name.

## 6. Urge Timer Screen

```text
                 04:32

            URGE WAVE

          ~~~~╭────╮~~~~
       ~~~~~╭─╯    ╰─╮~~~~~

             CIGARETTES

          [ CANCEL ]
```

The wave should be slow and subtle. It must never feel like a game.

## 7. Outcome Screen

```text
                 HOW ARE YOU NOW?

          [ URGE PASSED ]

          [ STILL AN URGE ]

          [ RELAPSE ]
```

## 8. Trigger Selection

Use compact chips/buttons:

`Stress` `Boredom` `Alone` `Social` `Habit/Routine` `Random/Other`

No text field is required.

## 9. History

History should expose the same information in a compact, browsable format:

- Calendar
- Previous streak periods
- Urge events
- Relapses
- Trigger frequency

## 10. Settings

Keep settings intentionally small:

- Reminder preferences
- Default urge timer
- Notification behavior
- Theme preference if implemented
- Data export/import if added later
- Delete all local data
- About

No login screen is needed for MVP.
