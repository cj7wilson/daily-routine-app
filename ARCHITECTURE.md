# Daily Routine App - Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        DAILY ROUTINE APP                         │
│                     (Jetpack Compose + MVVM)                     │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                         UI LAYER (Compose)                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌────────────────────┐         ┌────────────────────┐         │
│  │   HomeScreen.kt    │         │ RoutineDetailScreen│         │
│  │                    │         │                    │         │
│  │  - Show "Today"    │◄───────►│  - View/Edit Items │         │
│  │  - List Routines   │  Nav    │  - Add/Delete Items│         │
│  │  - Toggle Complete │         │  - Show All Items  │         │
│  └─────────┬──────────┘         └─────────┬──────────┘         │
│            │                               │                     │
│            │                               │                     │
│  ┌─────────▼──────────┐         ┌─────────▼──────────┐         │
│  │  HomeViewModel     │         │ RoutineViewModel   │         │
│  │  - StateFlow UI    │         │ - Manage Items     │         │
│  │  - Load Routines   │         │ - Create Routine   │         │
│  │  - Toggle Items    │         │ - Update Data      │         │
│  └─────────┬──────────┘         └─────────┬──────────┘         │
└────────────┼──────────────────────────────┼────────────────────┘
             │                               │
             └───────────┬───────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────────┐
│                   DOMAIN LAYER                                │
├───────────────────────────────────────────────────────────────┤
│                                                                │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              RoutineRepository                          │ │
│  │                                                          │ │
│  │  - getAllRoutinesSnapshot()                            │ │
│  │  - getRoutineWithItems()                               │ │
│  │  - createRoutine()                                     │ │
│  │  - addItemToRoutine()                                  │ │
│  │  - toggleItemCompletion()                              │ │
│  │  - Date change logic (getTodayString)                 │ │
│  │  - Cleanup old completions                             │ │
│  └───────────────────┬───────────────────────────────────┘ │
└────────────────────────┼─────────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────────┐
│                   DATA LAYER (Room)                           │
├───────────────────────────────────────────────────────────────┤
│                                                                │
│  ┌──────────────────────────────────────────────────────────┐│
│  │           DailyRoutineDatabase (SQLite)                  ││
│  └──────────────────────────────────────────────────────────┘│
│                                                                │
│  ┌───────────────┐  ┌────────────────┐  ┌──────────────────┐│
│  │ RoutineDao    │  │ChecklistItemDao│  │ItemCompletionDao ││
│  │               │  │                │  │                  ││
│  │ - getAll()    │  │ - getItems()   │  │ - getForDate()   ││
│  │ - insert()    │  │ - insert()     │  │ - insert()       ││
│  │ - update()    │  │ - delete()     │  │ - update()       ││
│  │ - delete()    │  │                │  │ - cleanup()      ││
│  └───────┬───────┘  └────────┬───────┘  └────────┬─────────┘│
│          │                   │                     │          │
│  ┌───────▼───────┐  ┌────────▼───────┐  ┌────────▼─────────┐│
│  │RoutineEntity  │  │ChecklistItem   │  │ItemCompletion    ││
│  │               │  │Entity          │  │Entity            ││
│  │ - id          │  │                │  │                  ││
│  │ - name        │  │ - id           │  │ - id             ││
│  │ - createdAt   │  │ - routineId ◄──┼──┼─ itemId         ││
│  │ - isActive    │  │ - description  │  │ - completionDate ││
│  └───────────────┘  │ - orderIndex   │  │ - isCompleted    ││
│                     └────────────────┘  └──────────────────┘│
└───────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                   BILLING LAYER                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              BillingManager                               │  │
│  │                                                            │  │
│  │  - Google Play Billing Library v6.1.0                    │  │
│  │  - FREE_ROUTINE_LIMIT = 3                                │  │
│  │  - canCreateMoreRoutines()                               │  │
│  │  - launchPurchaseFlow()                                  │  │
│  │  - isPremium: StateFlow<Boolean>                         │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════

DATA FLOW EXAMPLES:

1. TOGGLE ITEM COMPLETION:
   User Tap → HomeScreen → HomeViewModel.toggleItemCompletion()
   → RoutineRepository.toggleItemCompletion()
   → ItemCompletionDao.getCompletionForDate() / update()
   → Room Database → Update UI via StateFlow

2. DAILY RESET:
   Date Changes → Repository.getTodayString() returns new date
   → ItemCompletionDao.getCompletionsForDate(newDate)
   → Returns empty list for new day
   → UI shows all items uncompleted

3. CREATE ROUTINE:
   User Input → CreateDialog → RoutineViewModel.createRoutine()
   → Check BillingManager.canCreateMoreRoutines()
   → RoutineRepository.createRoutine()
   → RoutineDao.insertRoutine()
   → Navigate to detail screen

4. VIEW TODAY'S ROUTINES:
   App Launch → HomeViewModel.init()
   → Repository.getAllRoutinesSnapshot()
   → Combines: RoutineDao.getAllActiveRoutines()
              + ChecklistItemDao.getItemsForRoutine()
              + ItemCompletionDao.getCompletionsForDate(today)
   → HomeScreen displays list

═══════════════════════════════════════════════════════════════════

KEY DESIGN PATTERNS:

✓ MVVM Architecture
✓ Repository Pattern
✓ Reactive Programming (Flows)
✓ Clean Architecture Layers
✓ Single Responsibility
✓ Dependency Injection Ready
✓ Material Design 3

═══════════════════════════════════════════════════════════════════

TECHNOLOGIES:

- Kotlin 1.9.20
- Jetpack Compose (BOM 2023.10.01)
- Room 2.6.0
- Material Design 3
- Coroutines & Flow
- Google Play Billing 6.1.0
- Navigation Compose 2.7.5
- AndroidX Lifecycle

═══════════════════════════════════════════════════════════════════
```

## Entity Relationships

```
Routines (1) ─────► (N) ChecklistItems
                          │
                          │
                          ▼
                    (N) ItemCompletions
                        (per date)

One Routine has many ChecklistItems
One ChecklistItem has many ItemCompletions (one per day)
Cascade delete: Delete Routine → deletes all Items → deletes all Completions
```

## Freemium Model

```
┌─────────────┐                    ┌──────────────┐
│  FREE USER  │                    │ PREMIUM USER │
├─────────────┤                    ├──────────────┤
│ 3 Routines  │                    │ Unlimited    │
│ Unlimited   │                    │ Routines     │
│ Items/Routine                    │ Unlimited    │
│ Full Features                    │ Items        │
│ Local Storage                    │ Full Features│
└─────────────┘                    └──────────────┘
      │                                   ▲
      │                                   │
      └─────► Upgrade via Play Billing ──┘
```

## Daily Reset Mechanism

```
Day 1:                Day 2:
┌─────────────┐       ┌─────────────┐
│ 2025-12-29  │       │ 2025-12-30  │
├─────────────┤       ├─────────────┤
│ Item 1: ✓   │       │ Item 1: ○   │ ← Reset!
│ Item 2: ✓   │       │ Item 2: ○   │ ← Reset!
│ Item 3: ○   │       │ Item 3: ○   │
└─────────────┘       └─────────────┘
     │
     └─ Stored in ItemCompletions with date

Query: WHERE completionDate = '2025-12-30'
Result: Empty → All items show uncompleted
```
