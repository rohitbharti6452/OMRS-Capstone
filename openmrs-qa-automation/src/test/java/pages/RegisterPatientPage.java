package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import utils.ConfigLoader;

/**
 * Page Object for the "Register a patient" wizard
 * (/openmrs/registrationapp/registerPatient.page).
 *
 * Demographics and the confirmation step render on a single page (no separate
 * "Next" step) — fields are matched by their stable `name` attribute since
 * OpenMRS auto-generates non-semantic `id` values (e.g. id="fr3042-field")
 * for form-builder fields. Verified against the live https://o2.openmrs.org server.
 */
public class RegisterPatientPage extends BasePage {

    private static final By GIVEN_NAME_FIELD  = By.cssSelector("input[name='givenName']");
    private static final By FAMILY_NAME_FIELD = By.cssSelector("input[name='familyName']");
    private static final By GENDER_SELECT     = By.id("gender-field");
    private static final By BIRTH_DAY_FIELD   = By.cssSelector("input[name='birthdateDay']");
    private static final By BIRTH_MONTH_SELECT = By.id("birthdateMonth-field");
    private static final By BIRTH_YEAR_FIELD  = By.cssSelector("input[name='birthdateYear']");
    private static final By NEXT_BUTTON       = By.id("next-button");
    private static final By CONFIRM_BUTTON    = By.id("submit");
    private static final By REQUIRED_FIELD_ERROR =
            By.xpath("//span[contains(@class,'field-error') and contains(text(),'Required')]");

    public RegisterPatientPage(WebDriver driver) {
        super(driver);
    }

    public RegisterPatientPage navigateTo() {
        driver.get(ConfigLoader.getUiBaseUrl()
                + "/registrationapp/registerPatient.page?appId=registrationapp.basicRegisterPatient");
        return this;
    }

    /**
     * Typing a name triggers a jQuery UI autocomplete dropdown that visually
     * overlays the field(s) below it (e.g. the gender selector) and intercepts
     * clicks there. Pressing Escape closes the suggestion list before moving on.
     */
    public RegisterPatientPage enterGivenName(String givenName) {
        WebElement field = waitForVisible(GIVEN_NAME_FIELD);
        field.sendKeys(givenName);
        field.sendKeys(Keys.ESCAPE);
        return this;
    }

    public RegisterPatientPage enterFamilyName(String familyName) {
        WebElement field = waitForVisible(FAMILY_NAME_FIELD);
        field.sendKeys(familyName);
        field.sendKeys(Keys.ESCAPE);
        return this;
    }

    public RegisterPatientPage selectGender(String genderCode) {
        new Select(waitForVisible(GENDER_SELECT)).selectByValue(genderCode);
        return this;
    }

    public RegisterPatientPage enterBirthdate(String day, String month, String year) {
        waitForVisible(BIRTH_DAY_FIELD).sendKeys(day);
        new Select(waitForVisible(BIRTH_MONTH_SELECT)).selectByValue(month);
        waitForVisible(BIRTH_YEAR_FIELD).sendKeys(year);
        return this;
    }

    /**
     * The wizard is a single-page-app paginated by a left-hand navigator
     * (Demographics &gt; Name / Gender / Birthdate &gt; Confirm) — only one
     * sub-section is rendered visible at a time. This green chevron button
     * advances to the next sub-section.
     */
    public RegisterPatientPage clickNext() {
        waitForClickable(NEXT_BUTTON).click();
        return this;
    }

    public void clickConfirm() {
        waitForClickable(CONFIRM_BUTTON).click();
    }

    /** True if at least one "Required" inline field-error is visible. */
    public boolean isValidationErrorDisplayed() {
        return isVisible(REQUIRED_FIELD_ERROR);
    }

    /**
     * Waits until the post-submit redirect navigates away from the
     * registration wizard, confirming the form was actually submitted
     * (the demo server's configured successUrl lands on /openmrs/index.htm
     * rather than a patientId-bearing URL, so that string can't be used).
     */
    public void waitForRegistrationSuccess() {
        wait.until(d -> !d.getCurrentUrl().contains("registerPatient.page"));
    }

    /** Fills the full demographics form with the given Faker-style data and submits. */
    public void registerPatient(String givenName, String familyName, String genderCode,
                                 String day, String month, String year) {
        navigateTo();
        enterGivenName(givenName);
        enterFamilyName(familyName);
        clickNext();
        selectGender(genderCode);
        clickNext();
        enterBirthdate(day, month, year);
        clickNext();
        clickConfirm();
        waitForRegistrationSuccess();
    }
}
