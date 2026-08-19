# Issue tracker: GitHub

Issues and specs for this repository live in GitHub Issues:

https://github.com/mrfdev/1MB-Locator-HUD/issues

Use the `gh` CLI for issue operations from inside this clone.

## Conventions

- Create: `gh issue create --title "..." --body "..."`
- Read: `gh issue view <number> --comments`
- List: `gh issue list --state open --json number,title,body,labels,comments`
- Comment: `gh issue comment <number> --body "..."`
- Add or remove labels: `gh issue edit <number> --add-label "..."` or `--remove-label "..."`
- Close: `gh issue close <number> --comment "..."`

Infer the repository from `git remote -v`; `gh` does this automatically inside the clone.

## Pull requests as a triage surface

**PRs as a request surface: no.**

GitHub shares one number space across issues and pull requests. When a bare reference such as `#42` is ambiguous, try `gh pr view 42` and then `gh issue view 42`.

## Skill operations

When a skill says “publish to the issue tracker,” create a GitHub issue.

When a skill says “fetch the relevant ticket,” run:

`gh issue view <number> --comments`

## Wayfinding operations

A wayfinding map is one GitHub issue with child issues representing individual tickets.

- Label the map `wayfinder:map`.
- Link tickets using GitHub sub-issues where supported.
- Otherwise, use a task list in the map and add `Part of #<map>` to each child.
- Label children with `wayfinder:research`, `wayfinder:prototype`, `wayfinder:grilling`, or `wayfinder:task`.
- Represent blockers with GitHub’s native issue dependencies where available.
- Fall back to `Blocked by: #<number>` when dependencies are unavailable.
- Claim work with `gh issue edit <number> --add-assignee @me`.
- Resolve work by commenting with the result and closing the issue.
