---
name: commit
description: Create a git commit following best practices (conventional commits, scoped messages) and save progress to memory for future session recall. Use when the user wants to commit changes.
user-invocable: true
allowed-tools: Bash, Read, Glob, Grep, Write, Edit
---

You are creating a git commit following best practices and then saving the session progress to memory.

## Step 1: Gather Context

Run these in parallel:
- `git status` — see all modified/untracked files (never use -uall)
- `git diff HEAD` — see all staged and unstaged changes
- `git log --oneline -10` — see recent commit history to match style
- `git branch --show-current` — get current branch name

## Step 2: Determine What to Stage

- Use `$ARGUMENTS` as a hint for what to commit (e.g., a feature name, file paths, or "all")
- If $ARGUMENTS is empty, ask the user what to stage, or use your judgment based on related changes
- **Never** stage: `.env`, `*.key`, `*.pem`, `credentials.*`, secrets of any kind
- Prefer staging specific files by name over `git add -A` or `git add .`
- If unrelated changes are mixed, only stage the relevant ones

## Step 3: Write the Commit Message

Follow the **Conventional Commits** format:

```
<type>(<scope>): <short summary>

<optional body — explain WHY, not what>

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>
```

### Types
| Type | Use when |
|------|----------|
| `feat` | New feature or capability |
| `fix` | Bug fix |
| `refactor` | Code restructure, no behavior change |
| `test` | Adding or updating tests |
| `docs` | Documentation only |
| `chore` | Build, config, tooling, deps |
| `perf` | Performance improvement |
| `style` | Formatting, whitespace |

### Rules
- Summary line: max 72 characters, imperative mood ("add", not "added")
- Scope: the module, screen, or layer affected (e.g., `feed`, `auth`, `api`, `data-layer`)
- Body: explain *why* the change was made, not *what* (the diff shows what)
- Always include the Co-Authored-By trailer

### Example
```
feat(feed): add cursor-based pagination to news feed

Offset pagination degraded at scale and produced inconsistent results
when new posts were inserted. Cursor-based pagination uses indexed
columns and maintains stable ordering across content updates.

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>
```

## Step 4: Execute the Commit

Stage files then commit using a HEREDOC to preserve formatting:

```bash
git add <specific files>
git commit -m "$(cat <<'EOF'
<type>(<scope>): <summary>

<body if needed>

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>
EOF
)"
```

If the commit fails due to a pre-commit hook, fix the issue and create a NEW commit — never use `--no-verify` or `--amend` unless explicitly asked.

Verify success with `git status` after committing.

## Step 5: Save Progress to Memory

After a successful commit, update the progress memory file for this project.

The memory file lives at the project's memory directory. Determine the correct path:
- Find the project memory dir: `~/.claude/projects/<sanitized-path>/memory/` where `<sanitized-path>` replaces `/` with `-` in the working directory path
- The progress file is always: `progress.md`

**Read the existing progress.md first** (if it exists) then update it. If it doesn't exist, create it with this frontmatter:

```markdown
---
name: Project Progress Log
description: Running log of committed work — what's done, what changed, and current state of the project. Use to recall progress in future sessions.
type: project
---
```

**Append a new entry** (do not overwrite old entries) in this format:

```markdown
## [YYYY-MM-DD] <type>(<scope>): <summary>

**Branch:** <branch-name>
**Commit:** <short hash from git log>

**What changed:**
- <bullet describing each logical change>

**Why:**
<one sentence explaining the motivation>

**Current state:**
<1-2 sentences describing where the project is now — what works, what's next>

---
```

Also ensure `progress.md` is listed in the project's `MEMORY.md` index. If not present, add this line:
```
- [Progress Log](progress.md) — Running log of all committed work and current project state
```

## Step 6: Report Back

Tell the user:
1. What was committed (type, scope, summary)
2. The short commit hash
3. That progress has been saved to memory