package api;

import base.BaseTest;
import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import utils.ConfigLoader;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Component 3 — Automated API Test Suite
 * Covers TC-API-001 through TC-API-009 from docs/Test_Cases.md.
 * Target: OpenMRS 2.x Reference Application (O2) — https://o2.openmrs.org
 *
 * Execution order matters for TC-004, TC-005, TC-006 which share the UUID
 * created by TC-003. Tests that depend on a prior step use assumeTrue() so
 * they are skipped (not failed) if the dependency was never established.
 */
@DisplayName("OpenMRS Patient API Tests (TC-API-001 – TC-API-009)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PatientApiTest extends BaseTest {

    private static final Faker  FAKER = new Faker();
    private static final Random RNG   = new Random();

    // ── Luhn Mod-30 character set used by the OpenMRS ID generator ───────────
    private static final String LUHN_CHARS = "0123456789ACDEFGHJKLMNPRTUVWXY";

    // ── Shared state for TC-003 → TC-004, TC-005, TC-006 ───────────────────
    private static String sharedPatientUuid;
    private static String sharedGivenName;

    // ═══════════════════════════════════════════════════════════════════════
    // Private helpers
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Generates a 6-character OpenMRS ID (5-digit numeric base + Luhn Mod-30
     * check digit) that passes the server-side LuhnMod30IdentifierValidator.
     *
     * The OpenMRS variant differs from textbook Luhn in two ways:
     *  1. The rightmost character is doubled first (doubled starts true).
     *  2. When a doubled value >= 30, reduce via base-30 digit sum
     *     (val/30 + val%30) rather than a plain subtraction.
     */
    private static String generatePatientId() {
        int base = 10000 + RNG.nextInt(89999); // 10000–99999
        String baseStr = String.valueOf(base);

        int sum = 0;
        boolean doubled = true;
        for (int i = baseStr.length() - 1; i >= 0; i--) {
            int val = LUHN_CHARS.indexOf(baseStr.charAt(i));
            if (doubled) {
                val *= 2;
                if (val >= LUHN_CHARS.length()) {
                    val = val / LUHN_CHARS.length() + val % LUHN_CHARS.length();
                }
            }
            sum += val;
            doubled = !doubled;
        }
        int checkIdx = (LUHN_CHARS.length() - (sum % LUHN_CHARS.length())) % LUHN_CHARS.length();
        return baseStr + LUHN_CHARS.charAt(checkIdx);
    }

    /**
     * Builds a valid OpenMRS 2.x patient JSON payload.
     * OpenMRS 2.x requires person properties nested under "person" and at least
     * one entry in "identifiers" with a valid Luhn Mod-30 OpenMRS ID.
     */
    private static String patientJson(String givenName, String familyName,
                                      String gender, String birthdate) {
        return String.format("""
                {
                  "person": {
                    "names": [{ "givenName": "%s", "familyName": "%s" }],
                    "gender": "%s",
                    "birthdate": "%s"
                  },
                  "identifiers": [{
                    "identifier": "%s",
                    "identifierType": "%s",
                    "location": "%s",
                    "preferred": true
                  }]
                }""",
                givenName, familyName, gender, birthdate,
                generatePatientId(),
                ConfigLoader.getIdentifierTypeUuid(),
                ConfigLoader.getLocationUuid());
    }

    /**
     * POSTs a new patient and returns its UUID.
     * Shared by TC-003 (stores to field) and TC-009 (isolated lifecycle).
     */
    private static String createPatient(String givenName, String lastName) {
        return given()
                .spec(spec)
                .body(patientJson(givenName, lastName, "M", "1990-01-01"))
                .when()
                .post("/patient")
                .then()
                .statusCode(201)
                .body("uuid", notNullValue())
                .extract().jsonPath().getString("uuid");
    }

    /**
     * DELETEs (voids) a patient. OpenMRS 2.x performs a soft-delete and
     * returns 204 No Content; the record remains accessible with voided:true.
     */
    private static void deletePatient(String uuid) {
        given()
                .spec(spec)
                .queryParam("reason", "test cleanup")
                .when()
                .delete("/patient/" + uuid)
                .then()
                .statusCode(204);
    }

    /**
     * Skips the current test with an informative message when
     * TC-003 did not produce a UUID (e.g. server unreachable).
     */
    private void requireSharedPatient() {
        assumeTrue(sharedPatientUuid != null,
                "Skipping: shared patient from TC-API-003 is unavailable — check prior failure");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TC-API-001  Valid Authentication
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("TC-API-001 — GET /session with valid credentials returns authenticated:true")
    void validCredentials_sessionReturnsAuthenticatedTrue() {
        given()
                .spec(spec)
                .when()
                .get("/session")
                .then()
                .statusCode(200)
                .body("authenticated", equalTo(true))
                .body("user", notNullValue());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TC-API-002  Invalid Authentication
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(2)
    @DisplayName("TC-API-002 — GET /session with invalid credentials returns authenticated:false")
    void invalidCredentials_sessionReturnsAuthenticatedFalse() {
        // Intentionally bypasses the shared admin spec — wrong credentials only.
        // OpenMRS returns HTTP 200 with authenticated:false rather than 401.
        given()
                .baseUri(ConfigLoader.getBaseUrl())
                .contentType(ContentType.JSON)
                .auth().preemptive().basic("wrong", "wrong")
                .when()
                .get("/session")
                .then()
                .statusCode(200)
                .body("authenticated", equalTo(false));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TC-API-003  Create Patient
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(3)
    @DisplayName("TC-API-003 — POST /patient with valid data returns 201 and a UUID")
    void createPatient_returns201WithNonNullUuid() {
        sharedGivenName         = FAKER.name().firstName();
        String sharedFamilyName = FAKER.name().lastName();

        Response response = given()
                .spec(spec)
                .body(patientJson(sharedGivenName, sharedFamilyName, "M", "1990-01-01"))
                .when()
                .post("/patient")
                .then()
                .statusCode(201)
                .body("uuid", notNullValue())
                .extract().response();

        sharedPatientUuid = response.jsonPath().getString("uuid");
        assertNotNull(sharedPatientUuid, "Response must include a non-null uuid");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TC-API-004  Fetch Patient by UUID
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(4)
    @DisplayName("TC-API-004 — GET /patient/{uuid} returns 200 and the correct patient record")
    void fetchPatientByUuid_returns200WithMatchingUuidAndNotVoided() {
        requireSharedPatient();

        given()
                .spec(spec)
                .when()
                .get("/patient/" + sharedPatientUuid)
                .then()
                .statusCode(200)
                .body("uuid", equalTo(sharedPatientUuid))
                .body("voided", equalTo(false));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TC-API-005  Search Patient by Name
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(5)
    @DisplayName("TC-API-005 — GET /patient?q=NAME returns a non-empty results list containing the created patient")
    void searchByGivenName_resultsNonEmptyAndContainsCreatedUuid() {
        requireSharedPatient();

        given()
                .spec(spec)
                .queryParam("q", sharedGivenName)
                .when()
                .get("/patient")
                .then()
                .statusCode(200)
                .body("results",      not(empty()))
                .body("results.uuid", hasItem(sharedPatientUuid));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TC-API-006  Delete (Void) Patient
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(6)
    @DisplayName("TC-API-006 — DELETE /patient/{uuid} returns 204; subsequent GET shows voided:true")
    void deletePatient_returns200AndSubsequentGetShowsVoidedTrue() {
        requireSharedPatient();

        // Soft-delete (void) the shared patient — OpenMRS 2.x returns 204
        given()
                .spec(spec)
                .queryParam("reason", "test cleanup")
                .when()
                .delete("/patient/" + sharedPatientUuid)
                .then()
                .statusCode(204);

        // Verify the patient still exists but is now voided
        given()
                .spec(spec)
                .when()
                .get("/patient/" + sharedPatientUuid)
                .then()
                .statusCode(200)
                .body("voided", equalTo(true));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TC-API-007  404 for Non-Existent UUID
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(7)
    @DisplayName("TC-API-007 — GET /patient/{invalid-uuid} returns 404")
    void fetchNonExistentPatient_returns404() {
        given()
                .spec(spec)
                .when()
                .get("/patient/00000000-0000-0000-0000-000000000000")
                .then()
                .statusCode(404);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TC-API-008  400 for Missing Required Fields
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(8)
    @DisplayName("TC-API-008 — POST /patient with empty body returns 400 Bad Request")
    void createPatientEmptyBody_returns400() {
        given()
                .spec(spec)
                .body("{}")
                .when()
                .post("/patient")
                .then()
                .statusCode(400);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TC-API-009  Chained Lifecycle (POST → GET → DELETE → GET)
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(9)
    @DisplayName("TC-API-009 — Chained POST → GET → DELETE → GET verifies full patient lifecycle")
    void chainedLifecycle_createFetchDeleteVerifyVoided() {
        String firstName = FAKER.name().firstName();
        String lastName  = FAKER.name().lastName();

        // Step 1 — Create; assert UUID is returned
        String uuid = createPatient(firstName, lastName);
        assertNotNull(uuid, "Step 1: uuid must not be null after creation");

        // Step 2 — Fetch; assert record exists and is active
        given()
                .spec(spec)
                .when()
                .get("/patient/" + uuid)
                .then()
                .statusCode(200)
                .body("uuid",   equalTo(uuid))
                .body("voided", equalTo(false));

        // Step 3 — Delete (void)
        deletePatient(uuid);

        // Step 4 — Fetch again; assert patient is now voided
        given()
                .spec(spec)
                .when()
                .get("/patient/" + uuid)
                .then()
                .statusCode(200)
                .body("voided", equalTo(true));
    }
}
