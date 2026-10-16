# Mobile Engineering — Mobile System Design Masterclass Series

Interactive, browser-based architectural presentation decks and standalone vector diagrams designed for mobile engineering teams and interview preparation (Flutter, React Native, and Native Android/iOS).

---

## Masterclass Decks

1. **[Optimistic Writes, Local Mutex & Distributed Consistency](./optimistic-writes.html)**
   * 0ms perceived latency using local SQLite as Single Source of Truth (SSOT).
   * Rapid-tap concurrency hazards & the out-of-order state inversion bug.
   * Per-entity Mutex lock serialization (`ConcurrentHashMap<String, Mutex>`).
   * Subway resilience: Exponential backoff with full jitter and atomic compensating rollbacks.
   * Multi-device conflict resolution: Snowflake `requestId` idempotency and Last-Write-Wins (LWW).

2. **[Cache Eviction, Offline Sync & Clock-Skew Resiliency](./presentation.html)**
   * Two-tier write-time eviction (TTL eviction + Hard size-cap pruning).
   * SQLite index optimization `(cachedAt)` and `PRAGMA auto_vacuum`.
   * The "Lying Clock" problem: Client clock tampering vs Monotonic hardware crystal clock.
   * Piggybacked network time synchronization via HTTP `Date` headers.

---

## How to Open the Decks

Open either deck directly in any modern web browser:

```bash
# Optimistic Writes & Mutex Masterclass
open docs/optimistic-writes.html

# Cache Eviction & Clock-Skew Masterclass
open docs/presentation.html
```

You can also seamlessly switch between decks using the header navigation switcher in the top bar!

---

## Deck Features & Keyboard Controls

* **Navigate:** `→` / `Space` / `Page Down` for Next slide, `←` / `Page Up` for Previous slide.
* **Notes:** Press **`N`** (or click the **📝 Notes** button) to toggle the presenter's script with talking points for each slide.
* **Fullscreen:** Press **`F`** (or click **⛶ Fullscreen**).
* **Live Stopwatch:** Built-in elapsed session timer in the header.

---

## 10-Slide Topic Breakdown: Optimistic Writes

| Slide | Topic | Key Architectural Deep Dive |
| :--- | :--- | :--- |
| **1. Intro** | Optimistic Writes Overview | The zero-latency expectation and 4 architectural pillars. |
| **2. UX Models** | Pessimistic vs Optimistic UI | Why waiting 400ms for cellular round trips destroys social feed engagement. |
| **3. State Machine** | 3-Tier Durable States | `SYNCED` $\rightarrow$ `PENDING` $\rightarrow$ `FAILED` in SQLite (Room) to survive process death. |
| **4. Concurrency Bug** | Rapid-Tap Race Hazard | Why coroutines arrive out-of-order and why naive debouncing drops user intent. |
| **5. Mutex Architecture** | Per-Post Keyed Mutex | `ConcurrentHashMap<String, Mutex>` serializing toggles per post without global thread blocking. |
| **6. Resilience** | Backoff & Jitter | Subway tunnel recovery with exponential backoff and Coroutines vs WorkManager. |
| **7. Rollbacks** | Compensating Transactions | Pre-tap state snapshotting, atomic SQLite rollbacks, and non-blocking UX. |
| **8. Distributed Sync** | Multi-Device Conflicts & LWW | Snowflake `requestId` deduplication and server-time Last-Write-Wins resolution. |
| **9. Cross-Platform** | Universal Implementations | Android Kotlin (`withLock`), iOS Swift (`actor`), Flutter (`package:synchronized`), React Native. |
| **10. Rubric** | 5 Golden Rules & Q&A | Senior+ MSD interview checklist and wrap-up discussion. |

---

## Standalone Vector Diagrams (for Confluence, Slides & Notion)

* [`docs/diagrams/optimistic-write-state-machine.svg`](./diagrams/optimistic-write-state-machine.svg): 0ms State Machine & Rollback Lifecycle (Tap → SQLite Pending → UI SSOT → Mutex → Network Reconciliation).
* [`docs/diagrams/per-post-mutex-concurrency.svg`](./diagrams/per-post-mutex-concurrency.svg): Rapid Consecutive Taps: Unsynchronized Coroutine Race Hazard vs Per-Post Mutex Pipeline.
* [`docs/diagrams/cache-eviction-architecture.svg`](./diagrams/cache-eviction-architecture.svg): Two-Tier Write-Time Eviction Flow (Insert → TTL → Size Cap → Cascade Delete).
* [`docs/diagrams/clock-skew-and-time-sync.svg`](./diagrams/clock-skew-and-time-sync.svg): Wall Clock vs Monotonic Clock & Clock Skew Formula.
