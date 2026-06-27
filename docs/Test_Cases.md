# TEST CASES — OpenMRS QA Automation Capstone

| Field | Value |
|-------|-------|
| **Project** | Cross-Platform QA Showcase — OpenMRS |
| **Prepared By** | Rohit Bharti |
| **Reviewed By** | Sai Ram Burri |
| **Date** | 2026-06-27 |
| **Version** | 1.0 |
| **Total Cases** | 20 |

---

## Coverage Summary

| Layer | Count |
|-------|-------|
| API Test Cases | 9 |
| UI Test Cases | 7 |
| Accessibility Test Cases | 2 |
| Cross-Device / Cross-Browser Cases | 2 |
| **Total** | **20** |

Negative test cases: ≥ 1 per layer ✅  
Minimum required: 15 ✅

---

## Template Reference

Each test case uses the following fields:
- **ID** — Unique identifier
- **Title** — Short description of what is being tested
- **Testing Level** — Integration / System / Acceptance / Accessibility
- **Test Type** — Functional / Negative / Performance / Accessibility / Cross-device
- **Preconditions** — What must be true before the test runs
- **Steps** — Numbered actions to perform
- **Expected Result** — What should happen
- **Actual Result** — Filled in after execution
- **Status** — Pass / Fail / Blocked / Not Executed

---

## API Test Cases

---

### TC-API-001 — Valid Authentication Returns Authenticated True

| Field | Details |
|-------|---------|
| **ID** | TC-API-001 |
| **Title** | GET /session with valid credentials returns authenticated: true |
| **Testing Level** | Integration |
| **Test Type** | Functional |
| **Preconditions** | Demo server is reachable at https://demo.openmrs.org |
| **Steps** | 1. Send GET request to `https://demo.openmrs.org/openmrs/ws/rest/v1/session` <br> 2. Include header: `Authorization: Basic YWRtaW46QWRtaW4xMjM=` (admin:Admin123) |
| **Expected Result** | HTTP 200 OK; response body contains `"authenticated": true` and a non-null `sessionId` |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-API-002 — Invalid Credentials Return Authenticated False

| Field | Details |
|-------|---------|
| **ID** | TC-API-002 |
| **Title** | GET /session with invalid credentials returns authenticated: false |
| **Testing Level** | Integration |
| **Test Type** | Negative |
| **Preconditions** | Demo server is reachable |
| **Steps** | 1. Send GET request to `/openmrs/ws/rest/v1/session` <br> 2. Include header: `Authorization: Basic d3Jvbmc6d3Jvbmc=` (wrong:wrong) |
| **Expected Result** | HTTP 200 OK; response body contains `"authenticated": false` |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-API-003 — Create New Patient with Valid Data

| Field | Details |
|-------|---------|
| **ID** | TC-API-003 |
| **Title** | POST /patient creates a new patient and returns a UUID |
| **Testing Level** | Integration |
| **Test Type** | Functional |
| **Preconditions** | Authenticated session available; Faker generates unique name |
| **Steps** | 1. Send POST to `/openmrs/ws/rest/v1/patient` with Basic Auth <br> 2. Request body: `{ "names": [{ "givenName": "<faker>", "familyName": "<faker>" }], "gender": "M", "birthdate": "1990-01-01" }` |
| **Expected Result** | HTTP 201 Created; response body contains a non-null `uuid` field |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-API-004 — Fetch Patient by UUID

| Field | Details |
|-------|---------|
| **ID** | TC-API-004 |
| **Title** | GET /patient/{uuid} returns the correct patient record |
| **Testing Level** | Integration |
| **Test Type** | Functional |
| **Preconditions** | A patient created via TC-API-003; UUID stored from response |
| **Steps** | 1. Send GET to `/openmrs/ws/rest/v1/patient/{uuid}` with Basic Auth <br> 2. UUID is the one returned from the create call |
| **Expected Result** | HTTP 200 OK; response contains the patient's name matching what was submitted |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-API-005 — Search Patient by Name

| Field | Details |
|-------|---------|
| **ID** | TC-API-005 |
| **Title** | GET /patient?q=NAME returns matching results |
| **Testing Level** | Integration |
| **Test Type** | Functional |
| **Preconditions** | A patient with known name exists (created via TC-API-003) |
| **Steps** | 1. Send GET to `/openmrs/ws/rest/v1/patient?q=<givenName>` with Basic Auth |
| **Expected Result** | HTTP 200 OK; response `results` array is non-empty; contains patient matching the query name |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-API-006 — Delete (Void) Patient by UUID

| Field | Details |
|-------|---------|
| **ID** | TC-API-006 |
| **Title** | DELETE /patient/{uuid} soft-deletes the patient record |
| **Testing Level** | Integration |
| **Test Type** | Functional |
| **Preconditions** | A patient exists with known UUID |
| **Steps** | 1. Send DELETE to `/openmrs/ws/rest/v1/patient/{uuid}` with Basic Auth |
| **Expected Result** | HTTP 200 OK; subsequent GET on the same UUID returns `"voided": true` |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-API-007 — Fetch Non-Existent Patient Returns 404

| Field | Details |
|-------|---------|
| **ID** | TC-API-007 |
| **Title** | GET /patient/{invalid-uuid} returns 404 for a non-existent patient |
| **Testing Level** | Integration |
| **Test Type** | Negative |
| **Preconditions** | None |
| **Steps** | 1. Send GET to `/openmrs/ws/rest/v1/patient/00000000-0000-0000-0000-000000000000` with Basic Auth |
| **Expected Result** | HTTP 404 Not Found |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-API-008 — Create Patient with Missing Required Fields Returns 400

| Field | Details |
|-------|---------|
| **ID** | TC-API-008 |
| **Title** | POST /patient with missing required fields returns 400 Bad Request |
| **Testing Level** | Integration |
| **Test Type** | Negative |
| **Preconditions** | Authenticated session available |
| **Steps** | 1. Send POST to `/openmrs/ws/rest/v1/patient` with Basic Auth <br> 2. Request body: `{}` (empty — no name, gender, or birthdate) |
| **Expected Result** | HTTP 400 Bad Request; error message in response body |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-API-009 — Chained Flow: Create, Fetch, Then Delete Patient

| Field | Details |
|-------|---------|
| **ID** | TC-API-009 |
| **Title** | Chained request: POST → GET → DELETE verifies full patient lifecycle |
| **Testing Level** | Integration |
| **Test Type** | Functional |
| **Preconditions** | Authenticated session; Faker-generated unique data |
| **Steps** | 1. POST /patient to create a patient → capture UUID from response <br> 2. GET /patient/{uuid} → assert name matches created patient <br> 3. DELETE /patient/{uuid} → assert HTTP 200 <br> 4. GET /patient/{uuid} → assert `voided: true` |
| **Expected Result** | All four steps succeed; patient lifecycle completes without errors |
| **Actual Result** | — |
| **Status** | Not Executed |

---

## UI Test Cases

---

### TC-UI-001 — Valid Login Loads Dashboard

| Field | Details |
|-------|---------|
| **ID** | TC-UI-001 |
| **Title** | Valid credentials on login page navigates to dashboard |
| **Testing Level** | System |
| **Test Type** | Functional |
| **Preconditions** | Browser open; https://demo.openmrs.org is reachable |
| **Steps** | 1. Navigate to https://demo.openmrs.org <br> 2. Enter username: `admin` <br> 3. Enter password: `Admin123` <br> 4. Click Login button |
| **Expected Result** | Page redirects to dashboard; URL contains `/referenceapplication/home`; welcome message visible |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-UI-002 — Invalid Password Shows Error Message

| Field | Details |
|-------|---------|
| **ID** | TC-UI-002 |
| **Title** | Invalid password on login page shows an error message (negative) |
| **Testing Level** | System |
| **Test Type** | Negative |
| **Preconditions** | Browser open; login page loaded |
| **Steps** | 1. Navigate to https://demo.openmrs.org <br> 2. Enter username: `admin` <br> 3. Enter password: `wrongpassword` <br> 4. Click Login button |
| **Expected Result** | Page stays on login; error message displayed indicating invalid credentials; no redirect to dashboard |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-UI-003 — Empty Username and Password Shows Validation Error

| Field | Details |
|-------|---------|
| **ID** | TC-UI-003 |
| **Title** | Submitting empty login form shows validation error (negative) |
| **Testing Level** | System |
| **Test Type** | Negative |
| **Preconditions** | Browser open; login page loaded |
| **Steps** | 1. Navigate to https://demo.openmrs.org <br> 2. Leave username and password blank <br> 3. Click Login button |
| **Expected Result** | Error or validation message is displayed; user is not logged in |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-UI-004 — Register New Patient via UI

| Field | Details |
|-------|---------|
| **ID** | TC-UI-004 |
| **Title** | Register a new patient through the registration wizard |
| **Testing Level** | System |
| **Test Type** | Functional |
| **Preconditions** | User is logged in as admin |
| **Steps** | 1. Click "Register a Patient" from dashboard <br> 2. Enter Faker-generated given name and family name <br> 3. Select gender: Male <br> 4. Enter birthdate: 01/01/1990 <br> 5. Click Confirm on the summary page |
| **Expected Result** | Patient is created; confirmation page displays the patient's name and assigned ID |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-UI-005 — Search for Existing Patient by Name

| Field | Details |
|-------|---------|
| **ID** | TC-UI-005 |
| **Title** | Searching for an existing patient by name returns matching results |
| **Testing Level** | System |
| **Test Type** | Functional |
| **Preconditions** | User is logged in; a patient with a known Faker-generated name was created in this session |
| **Steps** | 1. Click "Find Patient Record" from dashboard <br> 2. Type the patient's given name in the search box <br> 3. Wait for results to load |
| **Expected Result** | At least one result is displayed matching the searched name |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-UI-006 — Search for Non-Existent Patient Returns No Results

| Field | Details |
|-------|---------|
| **ID** | TC-UI-006 |
| **Title** | Searching for a non-existent patient returns zero results (negative) |
| **Testing Level** | System |
| **Test Type** | Negative |
| **Preconditions** | User is logged in |
| **Steps** | 1. Click "Find Patient Record" from dashboard <br> 2. Type a random UUID-like string (e.g. `zzz-nonexistent-xyz`) in the search box <br> 3. Wait for results to load |
| **Expected Result** | No patient results are displayed; "No results found" or equivalent message appears |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-UI-007 — Dashboard URL Verified After Login

| Field | Details |
|-------|---------|
| **ID** | TC-UI-007 |
| **Title** | After successful login the browser URL confirms landing on dashboard |
| **Testing Level** | System |
| **Test Type** | Functional |
| **Preconditions** | Browser open; login page loaded |
| **Steps** | 1. Navigate to https://demo.openmrs.org <br> 2. Login with admin / Admin123 <br> 3. Wait for page to load completely |
| **Expected Result** | Current URL contains `/referenceapplication/home`; page title or heading confirms dashboard |
| **Actual Result** | — |
| **Status** | Not Executed |

---

## Accessibility Test Cases

---

### TC-ACC-001 — Keyboard Navigation Through Login Form

| Field | Details |
|-------|---------|
| **ID** | TC-ACC-001 |
| **Title** | All login form fields are reachable and operable using keyboard only |
| **Testing Level** | Acceptance |
| **Test Type** | Accessibility |
| **Preconditions** | Browser open; login page at https://demo.openmrs.org loaded; mouse not used |
| **Steps** | 1. Press Tab → focus moves to Username field <br> 2. Type `admin` <br> 3. Press Tab → focus moves to Password field <br> 4. Type `Admin123` <br> 5. Press Tab → focus moves to Login button <br> 6. Press Enter → form is submitted |
| **Expected Result** | All five interactions succeed without a mouse; focus is clearly visible on each element; login completes successfully. Covers WCAG 2.1 SC 2.1.1 (Keyboard) |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-ACC-002 — Color Contrast on Login Page Meets WCAG AA

| Field | Details |
|-------|---------|
| **ID** | TC-ACC-002 |
| **Title** | Login button text has sufficient color contrast ratio (WCAG 2.1 SC 1.4.3) |
| **Testing Level** | Acceptance |
| **Test Type** | Accessibility |
| **Preconditions** | Login page open in Chrome; Chrome DevTools or WebAIM Contrast Checker available |
| **Steps** | 1. Open https://demo.openmrs.org in Chrome <br> 2. Right-click the Login button → Inspect <br> 3. Note the foreground color and background color from CSS <br> 4. Enter both colors into https://webaim.org/resources/contrastchecker <br> 5. Read the contrast ratio result |
| **Expected Result** | Contrast ratio ≥ 4.5:1 for normal text (WCAG AA). Record the actual ratio in Actual Result. |
| **Actual Result** | — |
| **Status** | Not Executed |

---

## Cross-Device / Cross-Browser Test Cases

---

### TC-CD-001 — Login Flow on Google Chrome (Desktop)

| Field | Details |
|-------|---------|
| **ID** | TC-CD-001 |
| **Title** | Login and dashboard load correctly on Chrome desktop |
| **Testing Level** | System |
| **Test Type** | Cross-Browser |
| **Preconditions** | Google Chrome (latest) installed; ChromeDriver managed by WebDriverManager |
| **Steps** | 1. Launch Chrome via Selenium WebDriver <br> 2. Navigate to https://demo.openmrs.org <br> 3. Login with admin / Admin123 <br> 4. Verify dashboard loads |
| **Expected Result** | Login succeeds; dashboard renders correctly; no console errors related to layout or scripts |
| **Actual Result** | — |
| **Status** | Not Executed |

---

### TC-CD-002 — Login Flow on Mozilla Firefox (Desktop)

| Field | Details |
|-------|---------|
| **ID** | TC-CD-002 |
| **Title** | Login and dashboard load correctly on Firefox desktop (cross-browser) |
| **Testing Level** | System |
| **Test Type** | Cross-Browser |
| **Preconditions** | Mozilla Firefox (latest) installed; GeckoDriver managed by WebDriverManager |
| **Steps** | 1. Launch Firefox via Selenium WebDriver (set `browser=firefox` in config) <br> 2. Navigate to https://demo.openmrs.org <br> 3. Login with admin / Admin123 <br> 4. Verify dashboard loads |
| **Expected Result** | Login succeeds; dashboard renders identically to Chrome; no Firefox-specific rendering issues |
| **Actual Result** | — |
| **Status** | Not Executed |

---

## Test Execution Tracking

| ID | Title | Status |
|----|-------|--------|
| TC-API-001 | Valid auth returns authenticated:true | Not Executed |
| TC-API-002 | Invalid credentials return authenticated:false | Not Executed |
| TC-API-003 | POST /patient creates patient with UUID | Not Executed |
| TC-API-004 | GET /patient/{uuid} returns correct record | Not Executed |
| TC-API-005 | GET /patient?q=NAME returns results | Not Executed |
| TC-API-006 | DELETE /patient/{uuid} soft-deletes patient | Not Executed |
| TC-API-007 | Invalid UUID returns 404 | Not Executed |
| TC-API-008 | Missing fields returns 400 | Not Executed |
| TC-API-009 | Chained create → fetch → delete lifecycle | Not Executed |
| TC-UI-001 | Valid login loads dashboard | Not Executed |
| TC-UI-002 | Invalid password shows error | Not Executed |
| TC-UI-003 | Empty form shows validation error | Not Executed |
| TC-UI-004 | Register new patient via wizard | Not Executed |
| TC-UI-005 | Search for existing patient | Not Executed |
| TC-UI-006 | Search for non-existent patient | Not Executed |
| TC-UI-007 | Dashboard URL verified after login | Not Executed |
| TC-ACC-001 | Keyboard navigation through login form | Not Executed |
| TC-ACC-002 | Color contrast on login button | Not Executed |
| TC-CD-001 | Login on Chrome desktop | Not Executed |
| TC-CD-002 | Login on Firefox desktop | Not Executed |
