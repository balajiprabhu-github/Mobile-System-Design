# Mobile Engineering — Cache Eviction, Offline Sync & Clock-Skew Masterclass

An interactive, browser-based architectural presentation deck and standalone vector diagrams designed for mobile engineering teams (Flutter, React Native, and Native).

---

## How to Open the Deck

Open [`docs/presentation.html`](./presentation.html) (or [`docs/index.html`](./index.html)) directly in any web browser:

```bash
open docs/presentation.html
```

---

## Deck Features & Keyboard Controls

* **Navigate:** `→` / `Space` / `Page Down` for Next slide, `←` / `Page Up` for Previous slide.
* **Notes:** Press **`N`** (or click the **📝 Notes** button) to open the presenter's script with talking points for each slide.
* **Fullscreen:** Press **`F`** (or click **⛶ Fullscreen**).
* **Live Stopwatch:** Built-in elapsed session timer in the header.

---

## 10-Slide Architectural Topic Breakdown

| Slide | Topic | Key Architectural Deep Dive |
| :--- | :--- | :--- |
| **1. Intro** | Mobile Cache & Time Sync | Universal constraints across Flutter, React Native, and Native. |
| **2. Eviction Policies** | TTL vs Count Cap vs LRU | Why LRU on scrolling feeds causes disk thrashing & dropped frames at 120Hz. |
| **3. Architecture** | Two-Tier Hybrid Eviction | Why write-time eviction preserves instant launch read performance. |
| **4. SQLite Optimization** | Indices & Storage Reclamation | `(cachedAt)` and `(timeStamp DESC)` indexing; `PRAGMA auto_vacuum` space reclamation. |
| **5. The Blob Gap** | Metadata vs. Media Blobs | Why SQLite row deletion leaves image/video caches orphaned; `DiskLruCache` & background idle cleanup. |
| **6. The Hidden Bug** | The "Lying Clock" Problem | How device clock tampering wipes databases; Highway KM 0 & Bike Odometer analogies. |
| **7. Two Clocks** | Wall Clock vs Monotonic Clock | Bridging hardware crystal boot stopwatches with universal UTC across all mobile stacks. |
| **8. Implementation** | Piggyback Time Sync via Interceptors | Zero dedicated NTP requests: extracting HTTP `Date` headers in Dio / Axios / OkHttp. |
| **9. Distributed Offline** | Offline Mutations, Idempotency & LWW | Snowflake `requestId` idempotency keys and Last-Write-Wins (LWW) conflict resolution. |
| **10. Summary** | 4 Golden Rules & Questions | Team checklist for bounded storage & time-safe offline mode, followed by open discussion. |

---

## Standalone Vector Diagrams (for Confluence, Slides & Notion)

* [`docs/diagrams/cache-eviction-architecture.svg`](./diagrams/cache-eviction-architecture.svg): Two-Tier Write-Time Eviction Flow (Insert → TTL → Size Cap → Cascade Delete).
* [`docs/diagrams/clock-skew-and-time-sync.svg`](./diagrams/clock-skew-and-time-sync.svg): Wall Clock vs Monotonic Clock & Clock Skew Formula.
