# OpenMRS QA Automation — Cross-Platform QA Showcase

**Course:** Quality Engineer (AI & Test Automation)  
**Capstone:** Week 6 Day 5 — Cross-Platform QA Showcase  
**Application Under Test:** OpenMRS Reference Application Demo — https://demo.openmrs.org  
**Stack:** Java 21 · Maven · JUnit 5 · Selenium WebDriver · REST Assured · Cucumber · JMeter

---

## Project Overview

This capstone demonstrates end-to-end QA automation against a real, production-grade open-source Electronic Medical Record (EMR) system used by hospitals worldwide. The project covers two testing layers:

- **API Layer** — REST Assured tests against the OpenMRS REST API (`/openmrs/ws/rest/v1`)
- **UI Layer** — Selenium WebDriver tests automating the OpenMRS web interface (login, patient registration, search)

---

## Rubric — 10 Components (100 Points)

| # | Component | Points | Status |
|---|-----------|--------|--------|
| 1 | Test Plan (Written Document) | 10 | ⬜ |
| 2 | Test Cases (Written Document, min 15) | 15 | ⬜ |
| 3 | Automated API Test Suite (min 8 tests) | 15 | ⬜ |
| 4 | Automated UI Test Suite (min 5 tests) | 15 | ⬜ |
| 5 | BDD Feature File (Cucumber) | 10 | ⬜ |
| 6 | Defect Reports (min 3) | 10 | ⬜ |
| 7 | Test Execution Summary | 10 | ⬜ |
| 8 | Load Test Results (JMeter) | 5 | ⬜ |
| 9 | Accessibility Testing Evidence | 5 | ⬜ |
| 10 | Presentation Slides (5–8 slides) | 5 | ⬜ |
| | **Total** | **100** | |

---

## Repository Structure

```
OMRS-Capstone/
├── docs/
│   ├── Test_Plan.pdf               # Component 1
│   ├── Test_Cases.xlsx             # Component 2
│   ├── defect_reports.txt          # Component 6
│   ├── test_execution_summary.txt  # Component 7
│   └── accessibility_findings.txt  # Component 9
├── openmrs-qa-automation/          # Maven project (Components 3, 4, 5)
│   ├── src/
│   │   ├── test/java/
│   │   │   ├── api/
│   │   │   │   └── PatientApiTest.java       # Component 3 — 9 API tests
│   │   │   ├── ui/
│   │   │   │   └── LoginUITest.java          # Component 4 — 6 UI tests
│   │   │   ├── pages/                        # Page Object Model classes
│   │   │   │   ├── BasePage.java
│   │   │   │   ├── LoginPage.java
│   │   │   │   ├── RegisterPatientPage.java
│   │   │   │   └── SearchPatientPage.java
│   │   │   ├── bdd/steps/                    # Cucumber step definitions
│   │   │   └── utils/
│   │   │       ├── ApiClient.java
│   │   │       └── DriverFactory.java
│   │   └── test/resources/
│   │       ├── features/
│   │       │   └── patient_management.feature # Component 5 — BDD
│   │       └── config.properties
│   ├── pom.xml
│   └── README.md
├── load-test/
│   └── OpenMRS_LoadTest.jmx        # Component 8 — JMeter plan
├── screenshots/                    # Evidence screenshots
├── PROJECT_PLAN.md
└── README.md
```

---

## Prerequisites

- Java 21 (verify: `java -version`)
- Maven 3.9+ (verify: `mvn -version`)
- Google Chrome (latest)
- ChromeDriver (auto-managed via WebDriverManager)
- JMeter 5.6+ (for load testing only)

---

## Quick Start

### Run All Tests
```bash
cd openmrs-qa-automation
mvn clean test
```

### Run API Tests Only
```bash
mvn test -Dtest=PatientApiTest
```

### Run UI Tests (visible browser)
```bash
mvn test -Dtest=LoginUITest -Dheadless=false
```

### Run UI Tests (headless)
```bash
mvn test -Dtest=LoginUITest -Dheadless=true
```

### Run BDD Scenarios
```bash
mvn test -Dtest=RunCucumberTest
```

---

## Application Under Test

| Property | Value |
|----------|-------|
| URL | https://demo.openmrs.org |
| Username | admin |
| Password | Admin123 |
| API Base URL | https://demo.openmrs.org/openmrs/ws/rest/v1 |
| Auth | HTTP Basic Auth (Base64 encoded) |

> **Note:** The demo server resets periodically (~24 hours). Tests use Faker-generated data to avoid collisions.

---

## Key Design Decisions

- **Page Object Model (POM)** — All UI locators live in dedicated page classes, not scattered across tests
- **Explicit Waits only** — `Thread.sleep()` is banned; `WebDriverWait` is used throughout via `BasePage.java`
- **Faker data** — Every test run generates unique patient names/dates to avoid shared-server collisions
- **REST Assured** — API tests read like English: `given().auth().preemptive().basic(...).when().get(...).then().statusCode(200)`

---

## Submission Checklist

- [ ] `Test_Plan.pdf` — 8 sections complete
- [ ] `Test_Cases.xlsx` — 15+ cases with all fields filled
- [ ] `openmrs-qa-automation/` — builds and all tests pass
- [ ] `screenshots/` — API pass, UI pass, BDD pass screenshots included
- [ ] `defect_reports.txt` — 3+ reports with all fields
- [ ] `test_execution_summary.txt` — real numbers filled in
- [ ] `load-test/OpenMRS_LoadTest.jmx` + HTML report screenshot
- [ ] `accessibility_findings.txt` — 5 keyboard interactions + contrast note
- [ ] `presentation.pptx` or `presentation.pdf` — 5–8 slides
