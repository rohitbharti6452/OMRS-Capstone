package bdd.steps;

import com.github.javafaker.Faker;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import pages.RegisterPatientPage;
import pages.SearchPatientPage;
import utils.DriverFactory;

import static org.junit.jupiter.api.Assertions.*;

public class PatientManagementSteps {

    private WebDriver driver;
    private LoginPage loginPage;
    private RegisterPatientPage registerPage;
    private SearchPatientPage searchPage;
    private final Faker faker = new Faker();

    @Before
    public void setUp() {
        driver = DriverFactory.createDriver();
        loginPage = new LoginPage(driver);
        registerPage = new RegisterPatientPage(driver);
        searchPage = new SearchPatientPage(driver);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ── Background ──────────────────────────────────────────────────────────

    @Given("the OpenMRS login page is displayed")
    public void theOpenMRSLoginPageIsDisplayed() {
        loginPage.navigateTo();
    }

    // ── Login steps ─────────────────────────────────────────────────────────

    @When("I enter username {string} and password {string}")
    public void iEnterUsernameAndPassword(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
    }

    @When("I select the session location")
    public void iSelectTheSessionLocation() {
        loginPage.selectOutpatientClinicLocation();
    }

    @When("I click the login button")
    public void iClickTheLoginButton() {
        loginPage.clickLogin();
    }

    @Then("I should be on the dashboard")
    public void iShouldBeOnTheDashboard() {
        assertTrue(driver.getCurrentUrl().contains("home.page"),
                "Expected URL to contain 'home.page', was: " + driver.getCurrentUrl());
    }

    @Then("I should see the error message {string}")
    public void iShouldSeeTheErrorMessage(String expectedMessage) {
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error message banner should be visible");
        assertEquals(expectedMessage, loginPage.getErrorMessageText());
    }

    @Then("the login outcome should be {string}")
    public void theLoginOutcomeShouldBe(String outcome) {
        if ("success".equals(outcome)) {
            assertTrue(driver.getCurrentUrl().contains("home.page"),
                    "Expected successful login, landed on: " + driver.getCurrentUrl());
        } else {
            assertFalse(driver.getCurrentUrl().contains("home.page"),
                    "Expected login to fail, but reached: " + driver.getCurrentUrl());
        }
    }

    // ── Registration steps ──────────────────────────────────────────────────

    @Given("I am logged in as {string} with password {string}")
    public void iAmLoggedInAs(String username, String password) {
        loginPage.login(username, password);
    }

    @When("I navigate to the patient registration page")
    public void iNavigateToThePatientRegistrationPage() {
        registerPage.navigateTo();
    }

    @When("I enter a generated first and last name")
    public void iEnterAGeneratedFirstAndLastName() {
        registerPage.enterGivenName(faker.name().firstName());
        registerPage.enterFamilyName(faker.name().lastName());
    }

    @When("I click next to the gender section")
    public void iClickNextToTheGenderSection() {
        registerPage.clickNext();
    }

    @When("I select the gender {string}")
    public void iSelectTheGender(String genderCode) {
        registerPage.selectGender(genderCode);
    }

    @When("I click next to the birthdate section")
    public void iClickNextToTheBirthdateSection() {
        registerPage.clickNext();
    }

    @When("I enter birthdate day {string} month {string} year {string}")
    public void iEnterBirthdateDayMonthYear(String day, String month, String year) {
        registerPage.enterBirthdate(day, month, year);
    }

    @When("I click next to the confirmation section")
    public void iClickNextToTheConfirmationSection() {
        registerPage.clickNext();
    }

    @When("I confirm the patient registration")
    public void iConfirmThePatientRegistration() {
        registerPage.clickConfirm();
    }

    @Then("the patient should be registered successfully")
    public void thePatientShouldBeRegisteredSuccessfully() {
        registerPage.waitForRegistrationSuccess();
        assertFalse(driver.getCurrentUrl().contains("registerPatient.page"),
                "Expected to navigate away from registration wizard after submit");
    }

    // ── Search steps ─────────────────────────────────────────────────────────

    @When("I navigate to the find patient page")
    public void iNavigateToTheFindPatientPage() {
        searchPage.navigateTo();
    }

    @When("I search for the patient {string}")
    public void iSearchForThePatient(String name) {
        searchPage.searchFor(name);
    }

    @Then("at least one search result should be returned")
    public void atLeastOneSearchResultShouldBeReturned() {
        assertTrue(searchPage.getResultsCount() > 0,
                "Expected at least one result for the search query, got 0");
    }
}
