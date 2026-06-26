# PROJECT PLAN — OpenMRS QA Automation Capstone

**Start Date:** 2026-06-25  
**Target Submission:** 2026-07-02 (7-day plan)  
**Approach:** Commit code daily, one component per day, review before moving on

---

## Branch Strategy

```
main          ← production-ready, only merge from feature branches
├── feature/component-1-test-plan
├── feature/component-2-test-cases
├── feature/component-3-api-tests
├── feature/component-4-ui-tests
├── feature/component-5-bdd
├── feature/component-6-defect-reports
├── feature/component-7-execution-summary
├── feature/component-8-load-test
├── feature/component-9-accessibility
└── feature/component-10-presentation
```

**Commit convention:** `[ComponentX] short description`  
**Example:** `[Component3] add POST /patient API test with Faker data`

---

## Day-by-Day Plan

---

### Day 1 — 2026-06-25 | Setup + Test Plan (Component 1)

**Goal:** Working environment + Component 1 delivered and pushed

**Tasks:**
- [x] Clone repo, push README + PROJECT_PLAN (this commit)
- [ ] Install Java 21, Maven, Chrome if not present — verify with `java -version` and `mvn -version`
- [ ] Spend 20 min exploring https://demo.openmrs.org — log in, register a patient, use search
- [ ] Open Swagger API docs: `System Administration → Advanced Administration → REST Web Services → API Documentation`
- [ ] Try `GET /session` and `GET /patient` manually in Swagger or Postman
- [ ] Write `Test_Plan.pdf` — 8 sections (scope, objectives, testing levels, test types, tools, entry/exit criteria, risks, deliverables)
- [ ] Create `docs/` folder, add `Test_Plan.pdf`
- [ ] Commit on branch `feature/component-1-test-plan` → merge to main

**Deliverable:** `docs/Test_Plan.pdf` ✅

---

### Day 2 — 2026-06-26 | Maven Project Setup + API Tests (Component 3)

**Goal:** Maven project compiles and all API tests pass

**Tasks:**
- [ ] Scaffold Maven project under `openmrs-qa-automation/`
- [ ] Add `pom.xml` with: REST Assured, JUnit 5, Selenium, Cucumber, WebDriverManager, JavaFaker, ExtentReports
- [ ] Run `mvn clean compile` — fix any errors before writing tests
- [ ] Create `ApiClient.java` — handles Base64 Basic Auth header automatically
- [ ] Write `PatientApiTest.java` — 9 tests covering:
  - `GET /session` — valid credentials → `authenticated: true`
  - `GET /session` — invalid credentials → `authenticated: false`
  - `POST /patient` — create patient, assert UUID returned
  - `GET /patient/{uuid}` — fetch by UUID, assert name matches
  - `GET /patient?q=NAME` — search, assert results non-empty
  - `POST /patient/{uuid}` — update patient field
  - `DELETE /patient/{uuid}` — soft-delete, assert voided
  - `GET /patient/{uuid}` — after delete, assert voided=true
  - Chained test: create → fetch → delete in one flow
- [ ] Run `mvn test -Dtest=PatientApiTest` — all 9 must pass
- [ ] Take screenshot of passing tests → `screenshots/api_tests_passing.png`
- [ ] Add `openmrs-qa-automation/README.md` explaining how to run
- [ ] Commit on `feature/component-3-api-tests` → merge to main

**Deliverable:** `openmrs-qa-automation/` with passing API tests ✅

---

### Day 3 — 2026-06-27 | UI Tests + Page Object Model (Component 4)

**Goal:** 6 Selenium UI tests passing with POM structure

**Tasks:**
- [ ] Verify locators manually first (Section 4.1 of Study Guide):
  - Open https://demo.openmrs.org in Chrome → Inspect username, password, login button IDs
  - Update `LoginPage.java` locators if they differ
- [ ] Create Page Object classes:
  - `BasePage.java` — `waitForElement()`, `waitForUrl()`, `takeScreenshot()`
  - `LoginPage.java` — `login(user, pass)`, `getErrorMessage()`
  - `RegisterPatientPage.java` — `fillName()`, `fillGender()`, `fillBirthdate()`, `confirm()`
  - `SearchPatientPage.java` — `search(name)`, `getResultCount()`
- [ ] Create `DriverFactory.java` — reads `-Dheadless` flag, initializes ChromeDriver
- [ ] Write `LoginUITest.java` — 6 tests:
  - Valid login → dashboard loads
  - Invalid password → error message shown
  - Empty username → error shown
  - Login → verify dashboard URL contains `/referenceapplication/home`
  - Register patient → confirm patient appears in search
  - Search non-existent patient → zero results
- [ ] Run `mvn test -Dtest=LoginUITest -Dheadless=false` (watch it run)
- [ ] Run headless: `mvn test -Dtest=LoginUITest -Dheadless=true`
- [ ] Screenshots auto-saved to `screenshots/`
- [ ] Commit on `feature/component-4-ui-tests` → merge to main

**Deliverable:** `pages/` + `LoginUITest.java` + screenshots ✅

---

### Day 4 — 2026-06-28 | BDD + Test Cases Document (Components 2 & 5)

**Goal:** Cucumber scenarios running + 15+ written test cases

**Tasks (BDD — Component 5):**
- [ ] Write `patient_management.feature` with:
  - `@smoke` tag on at least one scenario
  - Regular `Scenario` — login happy path (Given/When/Then)
  - `Scenario Outline` — patient registration with 3+ data rows (Examples table)
  - Negative scenario — invalid login
- [ ] Write step definitions in `bdd/steps/PatientManagementSteps.java`
- [ ] Create `RunCucumberTest.java` — JUnit 5 Cucumber runner
- [ ] Run `mvn test -Dtest=RunCucumberTest` — all scenarios pass
- [ ] Screenshot BDD report → `screenshots/bdd_scenarios_passing.png`

**Tasks (Test Cases — Component 2):**
- [ ] Create `docs/Test_Cases.xlsx` (or `.txt`) with 15+ cases:
  - 5+ API test cases (GET, POST, DELETE, negative/404, chained)
  - 5+ UI test cases (login, register, search, form validation)
  - 2+ accessibility cases (keyboard nav, contrast check)
  - 2+ cross-device/cross-browser cases
  - 1+ negative case per layer
  - Fields: ID, Title, Level, Type, Preconditions, Steps, Expected Result, Actual Result, Status
- [ ] Commit both components on respective branches → merge to main

**Deliverables:** `features/patient_management.feature` + `docs/Test_Cases.xlsx` ✅

---

### Day 5 — 2026-06-29 | Defects + Execution Summary + Load Test (Components 6, 7, 8)

**Goal:** Written artifacts complete + JMeter load test run

**Tasks (Defect Reports — Component 6):**
- [ ] Write `docs/defect_reports.txt` with 3+ formal reports, each containing:
  - Defect ID, Title, Environment, Severity, Priority, Status: New
  - Reported By, Date, Steps to Reproduce, Expected Result, Actual Result
  - Root Cause (if known), Attachments description
- [ ] Base on real issues you observe, or realistic simulated defects

**Tasks (Execution Summary — Component 7):**
- [ ] Run full test suite: `mvn clean test`
- [ ] Fill in real numbers in `docs/test_execution_summary.txt`:
  - Total / Passed / Failed / Blocked / Not Executed counts
  - API Tests: x/9, UI Tests: x/6, BDD Scenarios: x/total
  - Defects Found by severity
  - 2–3 sentence Quality Assessment
  - 2–3 bullet Recommendations

**Tasks (Load Test — Component 8):**
- [ ] Install JMeter if not installed
- [ ] Create `load-test/OpenMRS_LoadTest.jmx` — Thread Group hitting `GET /session` (10 users, 5s ramp-up, 30s duration)
- [ ] Run JMeter, export HTML Dashboard report
- [ ] Screenshot CLI run → `screenshots/jmeter_cli_run.png`
- [ ] Screenshot HTML dashboard → `screenshots/jmeter_html_report.png`
- [ ] Commit all three components → merge to main

**Deliverables:** `defect_reports.txt` + `test_execution_summary.txt` + `OpenMRS_LoadTest.jmx` ✅

---

### Day 6 — 2026-06-30 | Accessibility + Presentation (Components 9 & 10)

**Goal:** All 10 components complete

**Tasks (Accessibility — Component 9):**
- [ ] Open https://demo.openmrs.org in Chrome, keyboard-navigate through the login form:
  - Tab through all fields → document focus order
  - Use Enter to submit → document result
  - Test Skip navigation links (if any)
  - Test form error announcement with keyboard
  - Test modal/dialog keyboard trap (if any)
- [ ] Note contrast issues: identify at least 1 element, estimate ratio (use Chrome DevTools or WebAIM Contrast Checker)
- [ ] Write `docs/accessibility_findings.txt` — 5 keyboard interactions + 1 contrast observation + 2 WCAG criteria assessed
- [ ] Commit on `feature/component-9-accessibility` → merge to main

**Tasks (Presentation — Component 10):**
- [ ] Create `presentation.pptx` or `presentation.pdf` with 8 slides:
  - Slide 1: Title — your name, project name, date
  - Slide 2: What you tested — AUT overview, scope
  - Slide 3: Testing strategy — tools, frameworks, why each was chosen
  - Slide 4: Test results — pass/fail table with key metrics
  - Slide 5: Key defects found — most critical/interesting bugs
  - Slide 6: Automation demo — screenshots of tests running
  - Slide 7: Lessons learned — what was hardest, what you'd do differently
  - Slide 8: Thank you + contact info
- [ ] Save to `presentation.pptx`
- [ ] Commit → merge to main

**Deliverables:** `accessibility_findings.txt` + `presentation.pptx` ✅

---

### Day 7 — 2026-07-01 | Final Polish + Submission Prep

**Goal:** Clean repo, all tests green, zip ready

**Tasks:**
- [ ] Run full test suite one final time: `mvn clean test`
- [ ] Verify all screenshots are in `screenshots/` folder:
  - `api_tests_passing.png`
  - `ui_tests_passing.png`
  - `bdd_scenarios_passing.png`
  - `jmeter_cli_run.png`
  - `jmeter_html_report.png`
- [ ] Fill in test_execution_summary.txt with final real numbers
- [ ] Final git status — ensure all files committed and pushed to main
- [ ] Create submission zip: `[YourName]_Capstone.zip`
- [ ] Practice 10-minute presentation out loud (timed, at least twice)
- [ ] Anticipate Q&A: "Why REST Assured?", "Why POM?", "What would you do differently?"

---

## Rubric Checklist (Final Review)

| Component | Min Requirement | Ready? |
|-----------|----------------|--------|
| 1 — Test Plan | 8 sections, scope clear, risks mitigated, tools justified | ⬜ |
| 2 — Test Cases | 15+ cases, all fields complete, API/UI/accessibility/cross-device | ⬜ |
| 3 — API Automation | 8+ tests pass, CRUD + negative + chained, README present | ⬜ |
| 4 — UI Automation | 5+ tests pass, POM used, explicit waits, screenshot captured | ⬜ |
| 5 — BDD | Scenario + Scenario Outline + Examples, tagged, steps pass | ⬜ |
| 6 — Defect Reports | 3+ reports, all fields complete, severity/priority justified | ⬜ |
| 7 — Execution Summary | Real numbers, honest quality assessment, actionable recs | ⬜ |
| 8 — Load Test | JMX file, CLI screenshot, HTML report screenshot, findings | ⬜ |
| 9 — Accessibility | 5 keyboard interactions, contrast note, 2 WCAG criteria | ⬜ |
| 10 — Presentation | 5–8 slides, all sections covered, professional appearance | ⬜ |

---

## Key Rules (Do Not Break)

- **No `Thread.sleep()`** — use explicit waits (`WebDriverWait`) only
- **No hard-coded patient names** — always use Faker-generated data
- **Verify UI locators manually** before running Selenium tests for the first time
- **Check locators every session** — the demo server may update between runs
- **All tests must actually pass** — no skipped or commented-out tests in submission
