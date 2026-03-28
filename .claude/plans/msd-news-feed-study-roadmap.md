# MSD News Feed Study Roadmap — Lead Android Developer

## Context

The user is preparing for senior+ Mobile System Design interviews at Big Tech, studying from the MSD book (Ch1–Ch3 completed). An Android News Feed reference app already exists with solid architectural foundations (MVVM, Hilt, Room, Retrofit, Compose). The goal is to systematically close the gap between what the book teaches and what's implemented, so each deep-dive topic can be articulated fluently in an interview *and* demonstrated in working code.

---

## Phase 1 — Architecture Foundations (1–2 sessions)

**Goal:** Whiteboard the full data flow cold in under 60 seconds.

**Read:**
- `data/repository/FeedRepository.kt` — interface contract, `Flow<Result<T>>` pattern
- `data/repository/FeedRepositoryImpl.kt` lines 28–73 — emit-twice cache pattern (stale-while-revalidate)
- `ui/feed/NewsFeedViewModel.kt` — how two emissions from `getFeed()` are handled
- `data/local/dao/PostDao.kt` — note `deletePostsOlderThan()` exists but is never called (first gap)

**Articulation target:** "Local DB is the single source of truth. On open, cached posts render immediately while a network fetch runs. On success, we atomically replace page-0 cache and emit fresh data." (45 seconds, no notes)

---

## Phase 2 — Local Storage & Offline Mode (2–3 sessions)

**Goal:** Design and explain full cache lifecycle — insert, TTL eviction, size cap, cascade delete.

**Read:**
- `data/local/entity/PostEntity.kt` — `cachedAt` field exists
- `data/mapper/PostMappers.kt` lines 16–30 — `cachedAt = System.currentTimeMillis()` set at write time (TTL, not LRU-by-access)
- `FeedRepositoryImpl.getFeed()` lines 51–55 — current full-wipe vs targeted TTL eviction gap

**Build:**
1. Wire TTL eviction: call `localDataSource.deletePostsOlderThan(now - CACHE_EXPIRY_MS)` after writing new posts (DAO method already exists in `PostDao.kt`)
2. Add size-cap: new DAO query `DELETE FROM posts WHERE postId IN (SELECT postId FROM posts ORDER BY cachedAt ASC LIMIT :count)`, call after TTL eviction if `getPostCount() > MAX_CACHED_POSTS`
3. Room migration: bump `NewsFeedDatabase` to version 2, add `Room.Migration(1,2)` in `DatabaseModule.kt`, remove `fallbackToDestructiveMigration()`

**Key talking point:** Cascade delete on `AttachmentEntity.postId` means evicting a post automatically cleans its attachments — draw this on a whiteboard.

**Critical files:**
- `data/local/dao/PostDao.kt`
- `data/local/source/PostsLocalDataSource.kt`
- `data/local/database/NewsFeedDatabase.kt`
- `di/DatabaseModule.kt`
- `data/repository/FeedRepositoryImpl.kt`

---

## Phase 3 — Optimistic Writes (2–3 sessions) ← deepest topic

**Goal:** Implement full PENDING/SYNCED/FAILED state machine; explain LWW conflict resolution.

**Read:**
- `ui/feed/NewsFeedViewModel.kt` `toggleLike()` — optimistic update is in-memory only, not durable
- `FeedRepositoryImpl.interactWithPost()` — reverts Room on failure, but race condition if double-tapped (two concurrent coroutines)
- `data/model/PostInteractionRequest.kt` — `requestId` field exists but never stored locally

**Build:**
1. Add `syncStatus: String = "SYNCED"` to `PostEntity.kt` (add to migration SQL from Phase 2)
2. Modify `interactWithPost()`: write `PENDING` before network call → `SYNCED` on success → `FAILED` on failure
3. Add exponential backoff retry loop in `interactWithPost()`: base 1s, max 30s, 3 attempts; mention WorkManager approach for cross-process-death retries
4. Fix double-tap race: add `Mutex` per `postId` in `FeedRepositoryImpl` to serialize like operations

**Concept task (no code):** Draw the LWW sequence diagram — `requestId` is a Snowflake ID (timestamp + device bits). Server accepts the mutation with the highest `requestId` when conflict occurs between two devices. Connects to the existing `requestId` field in `NewPostRequest` and `PostInteractionRequest`.

**Critical files:**
- `data/local/entity/PostEntity.kt`
- `data/repository/FeedRepositoryImpl.kt`
- `ui/feed/NewsFeedViewModel.kt`

---

## Phase 4 — Feed Rendering Performance (2 sessions)

**Goal:** Explain why LazyColumn implementation is production-grade and where it breaks at FB scale.

**Read:**
- `ui/feed/NewsFeedScreen.kt` lines 143–163 — `key = { it.postId }` (stable keys), `derivedStateOf` for infinite scroll trigger (avoids recomposition on every scroll event)
- `ui/feed/NewsFeedScreen.kt` lines 69–77 — `derivedStateOf` pattern for pagination trigger
- `ui/detail/PostDetailScreen.kt` lines 263–283 — video placeholder (ExoPlayer gap)
- `ui/components/PostCard.kt` lines 99–111 — Coil `AsyncImage` (async, disk-cached)

**Build:**
1. Implement `PostDetailViewModel.sharePost()` TODO — Android share sheet via `Intent.ACTION_SEND` (use `AndroidViewModel` for context, or `@ApplicationContext`)
2. Make attachment type safe: define `sealed class AttachmentType`, deserialize string→sealed at mapper layer, so `PostDetailScreen` only handles typed variants ("parse, don't validate")

**Talking point:** Litho (Facebook) pre-computes layout on a background thread. Compose achieves a similar goal through single-pass layout that avoids nested measure. Frame this comparison explicitly in interviews.

**Critical files:**
- `ui/feed/NewsFeedScreen.kt`
- `ui/detail/PostDetailScreen.kt`
- `ui/detail/PostDetailViewModel.kt`
- `data/mapper/PostMappers.kt`

---

## Phase 5 — API Design & Tests (1–2 sessions)

**Goal:** Nail MSD framework step 2 in 5 minutes; demonstrate testability awareness.

**Read:**
- `data/remote/NewsFeedApiService.kt` line 25 — `@Query("page") page: Int` is integer-offset (not cursor)

**API design to practice (cursor-based):**
```
GET  /v1/feed?after={cursor}&limit=20     → { posts, nextCursor, hasMore }
GET  /v1/posts/{postId}                   → { post with attachments }
POST /v1/posts                            → body: { requestId, content, attachments }
POST /v1/posts/{postId}/interactions      → body: { requestId, type: like|share|unlike }
```
Key decisions to articulate: cursor vs offset (no duplicate posts on insertion), single interaction endpoint (extensible), `requestId` for idempotency, REST vs GraphQL (REST sufficient for known schema).

**Build:**
1. ViewModel unit test: `toggleLike` optimistically updates then reverts on failure — use `FakeFeedRepository` implementing `FeedRepository` interface
2. DAO instrumented test: `insertPosts` then `deletePostsOlderThan` leaves only fresh posts — use `Room.inMemoryDatabaseBuilder()`

**Critical files:**
- `data/remote/NewsFeedApiService.kt`
- `app/src/test/` and `app/src/androidTest/`

---

## Interview Practice Checkpoints

| After Phase | `/msd-interview` focus |
|---|---|
| 1 | Step 3: High-Level Architecture — draw data flow |
| 2 | Deep Dive: Offline Mode & Cache Management |
| 3 | Deep Dive: Optimistic Writes (hardest follow-ups here) |
| 4 | Deep Dive: Rich Content & Scroll Performance |
| 5 | Full 45-min mock — all 5 MSD steps on News Feed |

---

## Summary

| Phase | Sessions | Key Deliverable |
|---|---|---|
| 1 — Foundations | 1–2 | Data flow whiteboard, cache-first pattern articulated |
| 2 — Offline/Cache | 2–3 | TTL eviction wired, DB migration added |
| 3 — Optimistic Writes | 2–3 | SyncStatus state machine, retry backoff, LWW diagram |
| 4 — Performance | 2 | Share implemented, typed attachments, Litho comparison |
| 5 — API + Tests | 1–2 | Cursor pagination designed, 2 tests written |

**Total: 8–12 sessions.** Phase 3 is the bottleneck — invest extra time there before the interview.
