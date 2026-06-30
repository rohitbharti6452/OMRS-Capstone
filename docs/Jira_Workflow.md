# Jira ↔ GitHub Workflow

> Replace `CAPS` below with your actual Jira project key once the project is created.

## Setup (one-time, Jira admin)

1. Create the Jira project, note its **project key** (e.g. `CAPS`).
2. In Jira: **Apps → Explore more apps → "GitHub for Jira"** → install.
3. Connect it to the `rohitbharti6452/OMRS-Capstone` GitHub repo.
4. No GitHub secrets or API tokens are required — the app reads commit/branch/PR text directly.

## Branch naming

```
feature/CAPS-12-add-login-ui-tests
bugfix/CAPS-18-fix-luhn-checksum
```

The issue key (`CAPS-12`) anywhere in the branch name links the branch to that issue automatically.

## Commit messages

Include the issue key in the commit subject line:

```
[Component4] CAPS-12 Add Page Object Model UI test suite
```

### Smart commits (optional issue transitions)

Jira smart commit syntax lets a commit message move the issue or log work, in addition to linking it:

```
CAPS-12 #comment Added LoginUITest, 6/6 passing
CAPS-12 #time 2h #comment Wrote page objects
CAPS-12 #done Page Object Model UI suite merged
```

Supported transition words depend on your Jira workflow's status names (`#in-progress`, `#done`, `#close`, etc.) — check your project's workflow under **Project Settings → Workflows**.

## Pull requests

Include the issue key in the PR title:

```
[Component4] CAPS-12 Add Page Object Model UI test suite
```

This links the PR to the issue and surfaces CI status (the `OMRS QA Automation CI` check) directly on the Jira issue's Development panel.

## What this gets you

- Every Jira issue shows linked branches, commits, PRs, and their CI build status — no manual updates.
- Issue transitions can happen from commit messages (smart commits) instead of switching to the Jira UI.
- Traceability for the capstone write-up: "Component X" work maps to "CAPS-N" issues with full commit history.
