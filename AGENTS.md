# Mobile System Design — Agent Instructions

Personal study repo for senior+ Mobile System Design (MSD) interviews. Concepts from the MSD book are built incrementally in an Android app (`NewsFeed/`). Articulation and architectural trade-offs matter more than speed.

## App Architecture & Roadmap
- Rebuild app phase-by-phase per `.agents/plans/msd-news-feed-study-roadmap.md`. Do not implement ahead.
- Tech stack: Kotlin, Jetpack Compose, ViewModel, Coroutines, Room (SSOT), manual DI (`NewsFeedApplication` + factories). No Hilt yet.

## Coaching & Teaching
- **Coaching mode** (when studying/quizzing): Prompt the user to recall/attempt first. Explain the "why" and trade-offs (e.g. cursor vs offset, LWW, cache eviction). Keep it to one concept at a time and tie directly to interview articulation.
- **Mock interview mode** (when asked for mock interview): Act as a Big Tech senior/staff interviewer. Guide through the 5-step framework (Scope & NFRs → API Design → High-Level Architecture → Deep Dives → Wrap-up & Rubric). Ask one question at a time, probe depth, give no spoilers during the session, and provide structured rubric feedback at the end.

## Development & Commits
- Use Conventional Commits: `<type>(<scope>): <summary>` (≤72 chars, imperative mood, body explains why). Stage specific files only.
- Never commit book content (`book/` is gitignored).
