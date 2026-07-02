package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.ConfigLoader;

/**
 * Page Object for the OpenMRS 2.x login page (/openmrs/login.htm).
 *
 * Locators were verified manually against the live https://o2.openmrs.org server
 * before writing any test (per the project's locator-verification policy) — see
 * docs/Test_Cases.md TC-UI-001/002/003 for the corresponding test cases.
 *
 * Note: the login form requires a session location to be selected via JS before
 * the submit button will fire (sessionLocationInput must be non-empty), so
 * login() always selects "Outpatient Clinic" (value=7) before clicking Login.
 */
public class LoginPage extends BasePage {

    private static final By USERNAME_FIELD     = By.id("username");
    private static final By PASSWORD_FIELD     = By.id("password");
    private static final By LOCATION_OUTPATIENT = By.cssSelector("#sessionLocation li[value='7']");
    private static final By LOGIN_BUTTON       = By.id("loginButton");
    private static final By ERROR_MESSAGE      = By.id("error-message");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage navigateTo() {
        driver.get(ConfigLoader.getUiBaseUrl() + "/login.htm");
        return this;
    }

    public LoginPage enterUsername(String username) {
        WebElement field = waitForVisible(USERNAME_FIELD);
        field.clear();
        field.sendKeys(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        WebElement field = waitForVisible(PASSWORD_FIELD);
        field.clear();
        field.sendKeys(password);
        return this;
    }

    public LoginPage selectOutpatientClinicLocation() {
        waitForClickable(LOCATION_OUTPATIENT).click();
        return this;
    }

    public void clickLogin() {
        waitForClickable(LOGIN_BUTTON).click();
    }

    /** Waits until the post-login redirect lands on the dashboard. */
    public void waitForDashboard() {
        waitForUrlContains("home.page");
    }

    /** Full login flow: username, password, required session location, submit, wait for dashboard. */
    public void login(String username, String password) {
        navigateTo();
        enterUsername(username);
        enterPassword(password);
        selectOutpatientClinicLocation();
        clickLogin();
        waitForDashboard();
    }

    public String getErrorMessageText() {
        return waitForVisible(ERROR_MESSAGE).getText().trim();
    }

    public boolean isErrorMessageDisplayed() {
        return isVisible(ERROR_MESSAGE);
    }
}
