# Daily Routine App

An Android app built with Jetpack Compose that implements a daily routine checklist system.

## Features

- **Daily Routine Management**: Create and manage multiple routines with ordered checklist items
- **Local Storage**: All data is stored locally using Room database
- **Per-Day Completion Tracking**: Item completion state is tracked per day and automatically resets on date change
- **Today View**: Primary UI showing current day's routines with completion status
- **Freemium Model**: Feature gating via Play Billing Library (free tier: 3 routines, premium: unlimited)
- **No Authentication**: MVP version with no user accounts or cloud sync
- **Material Design 3**: Modern UI with Material Design 3 and Jetpack Compose

## Tech Stack

- **UI**: Jetpack Compose with Material Design 3
- **Database**: Room (SQLite)
- **Architecture**: MVVM with Repository pattern
- **Billing**: Google Play Billing Library
- **Language**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Project Structure

```
app/
├── data/
│   ├── local/          # Room database, DAOs, entities
│   └── repository/     # Repository layer
├── domain/
│   └── model/          # Domain models
├── billing/            # Play Billing integration
└── ui/
    ├── home/           # Today view screen
    ├── routine/        # Routine detail screen
    └── theme/          # Compose theme
```

## Database Schema

- **Routines**: Stores routine metadata (id, name, created_at, is_active)
- **ChecklistItems**: Stores items for each routine (id, routine_id, description, order_index)
- **ItemCompletions**: Tracks completion state per item per day (id, item_id, completion_date, is_completed)

## Building

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on emulator or device

## Testing

Run unit tests:
```bash
./gradlew test
```

## License

This is a sample project for demonstration purposes.
