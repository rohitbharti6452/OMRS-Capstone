# TEST PLAN
## OpenMRS QA Automation Capstone

| Field | Value |
|-------|-------|
| **Application Under Test** | OpenMRS Reference Application — https://demo.openmrs.org |
| **Prepared By** | Sai Ram Burri & Rohit Bharti |
| **Date** | 2026-06-25 |
| **Version** | 1.0 |

---

## Introduction

This test plan covers the quality assurance strategy for the OpenMRS QA Automation Capstone project. OpenMRS is a production-grade, open-source Electronic Medical Record (EMR) platform used by hospitals and clinics worldwide. The capstone targets the publicly available OpenMRS Reference Application demo environment (`https://demo.openmrs.org`) as the Application Under Test (AUT).

The purpose of this document is to define what will be tested, how it will be tested, which tools will be used, and what criteria determine when testing is complete. It serves as the guiding reference for all QA activities conducted during the project, spanning REST API testing, browser-based UI testing, behavior-driven acceptance testing, performance testing, and accessibility evaluation.

This plan is authored as a collaborative effort and follows the branch-per-component Git workflow defined in the project's `PROJECT_PLAN.md`.

---

## 1. Scope

**In Scope:**
- Authentication (login/logout) via UI and REST API
- Patient management: create, read, update, delete (CRUD) via the REST API (`/openmrs/ws/rest/v1/patient`)
- Patient search via UI and API
- OpenMRS web interface functional flows on Chrome (desktop)
- API contract validation against the OpenMRS REST v1 specification
- Performance under light concurrent load (10 users) against the session endpoint
- Basic keyboard-navigation and color-contrast accessibility of the login and registration pages

**Out of Scope:**
- Mobile/native apps
- Non-patient modules (appointments, billing, pharmacy)
- Backend database queries or server-side infrastructure
- Multi-language / localization testing
- Security penetration testing beyond Basic Auth credential validation

---

## 2. Objectives

1. Verify that the OpenMRS REST API handles CRUD operations for patients correctly and returns expected HTTP status codes and JSON payloads.
2. Verify that the OpenMRS web UI allows a user to log in, register a patient, and search for patients without errors.
3. Validate system behavior under negative conditions (invalid credentials, malformed requests, missing required fields).
4. Confirm the system sustains baseline performance under 10 concurrent users without critical error rates.
5. Identify at least 3 functional or UX defects and document them formally.
6. Demonstrate BDD-style specification of acceptance criteria using Cucumber.

---

## 3. Testing Levels

| Level | Description | Tools |
|-------|-------------|-------|
| **Unit** | Not in scope — OpenMRS source is not under test | — |
| **Integration / API** | REST API tests against live demo server; validates endpoints, payloads, status codes, auth | REST Assured + JUnit 5 |
| **System / UI (E2E)** | End-to-end browser flows: login → register patient → search | Selenium WebDriver + JUnit 5 |
| **Acceptance (BDD)** | Gherkin scenarios describing user-facing behavior; treated as living documentation | Cucumber + JUnit 5 |
| **Performance** | Light load test against session endpoint to measure response time under concurrency | Apache JMeter |
| **Accessibility** | Manual keyboard-navigation audit + contrast check on login/registration pages | Chrome DevTools / WebAIM |

---

## 4. Test Types

| Type | Rationale |
|------|-----------|
| **Functional** | Primary focus — verify every feature behaves per specification |
| **Negative / Error-path** | Invalid credentials, missing fields, 404/401 responses must be explicitly verified |
| **Regression** | Full suite re-run before submission to confirm no regressions across components |
| **Smoke** | Tagged `@smoke` Cucumber scenarios cover the critical-path login flow; run first on each session |
| **Performance (Load)** | 10 concurrent users, 5-second ramp-up, 30-second duration against `GET /session` |
| **Accessibility** | Manual WCAG 2.1 Level AA spot-check: keyboard operability (Success Criterion 2.1.1) and contrast (1.4.3) |

---

## 5. Tools and Environment

| Category | Tool / Version | Justification |
|----------|---------------|---------------|
| Language | Java 21 | LTS release; required by course stack |
| Build | Maven 3.9+ | Standard Java build/dependency management; enables single `mvn clean test` execution |
| Test Framework | JUnit 5 | Modern test lifecycle management; integrates with Maven Surefire and Cucumber |
| API Testing | REST Assured 5.x | Fluent DSL for HTTP; readable BDD-style assertions with no boilerplate |
| UI Testing | Selenium WebDriver 4.x | Industry-standard browser automation; supports Chrome and headless mode |
| Browser Management | WebDriverManager 5.x | Auto-downloads matching ChromeDriver; eliminates manual version maintenance |
| BDD | Cucumber 7.x (JUnit 5 runner) | Gherkin scenarios serve as living documentation readable by non-technical stakeholders |
| Test Data | JavaFaker 1.x | Generates unique patient names per run; avoids shared-server data collisions |
| Reporting | ExtentReports 5.x | HTML test report with screenshots embedded per test |
| Load Testing | Apache JMeter 5.6+ | Industry standard for load/performance testing; produces HTML dashboard |
| Accessibility | Chrome DevTools + WebAIM Contrast Checker | No-cost, immediately available; sufficient for WCAG spot-check scope |
| **AUT** | OpenMRS Demo — https://demo.openmrs.org | Credentials: admin / Admin123; resets ~every 24 hours |
| **OS** | macOS 14 / any | Tests are platform-independent via Maven |

---

## 6. Entry and Exit Criteria

**Entry Criteria (prerequisites before testing begins):**
- [ ] `java -version` returns 21.x
- [ ] `mvn -version` returns 3.9+
- [ ] `https://demo.openmrs.org` is reachable and login with admin / Admin123 succeeds
- [ ] `mvn clean compile` completes with 0 errors
- [ ] All page locators have been manually verified in the browser before running Selenium tests

**Exit Criteria (conditions that define "testing complete"):**
- [ ] All 9 API tests pass (0 failures)
- [ ] All 6 UI tests pass (0 failures)
- [ ] All Cucumber scenarios pass (0 failures)
- [ ] At least 3 defect reports written and reviewed
- [ ] Test execution summary filled with real numbers
- [ ] JMeter load test completed; error rate < 5% at 10 users
- [ ] Accessibility findings document complete (5 keyboard interactions + contrast note)

**Suspension Criteria:**
- Demo server is unreachable for > 30 minutes → suspend and retry after server reset
- More than 3 unrelated test failures due to server instability → mark as "Blocked" in execution summary

---

## 7. Risks and Mitigations

| # | Risk | Likelihood | Impact | Mitigation |
|---|------|-----------|--------|------------|
| R1 | Demo server resets mid-run, deleting test data | High | Medium | Use Faker to create fresh data each run; chain create→fetch→delete in one test so data doesn't persist |
| R2 | UI locators change between server resets | Medium | High | Verify all locators manually each session before running Selenium; use stable HTML IDs over XPath where possible |
| R3 | Demo server rate-limits or blocks repeated requests | Low | High | Keep load test conservative (10 users, 30s); add 1s think-time between API test calls |
| R4 | ChromeDriver / Chrome version mismatch | Medium | Medium | WebDriverManager auto-resolves version; pin Chrome version if mismatch persists |
| R5 | Network latency causes flaky UI tests (explicit-wait timeouts) | Medium | Medium | All waits via `WebDriverWait` with 15s timeout; no `Thread.sleep()` |
| R6 | Shared demo server state affects search results | Medium | Low | Search tests filter by Faker-generated unique names to isolate results |
| R7 | Merge conflicts or lost work due to parallel collaboration on the shared GitHub repository | Medium | Medium | Follow the branch-per-component strategy defined in `PROJECT_PLAN.md`; never push directly to `main`; open a PR for every component and review before merging; communicate using the `[ComponentX]` commit convention to avoid overlapping work |

---

## 8. Deliverables

| # | Deliverable | Location | Due |
|---|-------------|----------|-----|
| 1 | **Test Plan** (this document) | `docs/Test_Plan.md` | Day 1 (2026-06-25) |
| 2 | **Test Cases** (15+ written cases) | `docs/Test_Cases.xlsx` | Day 4 (2026-06-28) |
| 3 | **Automated API Test Suite** (9 tests) | `openmrs-qa-automation/src/test/java/api/PatientApiTest.java` | Day 2 (2026-06-26) |
| 4 | **Automated UI Test Suite** (6 tests) | `openmrs-qa-automation/src/test/java/ui/LoginUITest.java` | Day 3 (2026-06-27) |
| 5 | **BDD Feature File** | `src/test/resources/features/patient_management.feature` | Day 4 (2026-06-28) |
| 6 | **Defect Reports** (3+) | `docs/defect_reports.txt` | Day 5 (2026-06-29) |
| 7 | **Test Execution Summary** | `docs/test_execution_summary.txt` | Day 5 (2026-06-29) |
| 8 | **JMeter Load Test** | `load-test/OpenMRS_LoadTest.jmx` + screenshots | Day 5 (2026-06-29) |
| 9 | **Accessibility Findings** | `docs/accessibility_findings.txt` | Day 6 (2026-06-30) |
| 10 | **Presentation** | `presentation.pptx` | Day 6 (2026-06-30) |
