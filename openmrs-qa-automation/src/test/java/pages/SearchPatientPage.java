package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.ConfigLoader;

/**
 * Page Object for the "Find Patient Record" search page
 * (/openmrs/coreapps/findpatient/findPatient.page).
 *
 * The search box triggers an AJAX lookup (300-1000ms debounce, 2+ char minimum)
 * that renders results into a table inside #patient-search-results, or shows a
 * "No matching records found" message when there are zero matches. Locators were
 * verified against the live https://o2.openmrs.org server before writing tests.
 */
public class SearchPatientPage extends BasePage {

    private static final By SEARCH_BOX        = By.id("patient-search");
    private static final By RESULTS_CONTAINER = By.id("patient-search-results");
    private static final By RESULT_ROWS       = By.cssSelector("#patient-search-results table tbody tr");
    private static final By NO_RESULTS_TEXT   =
            By.xpath("//*[contains(text(),'No matching records found')]");

    public SearchPatientPage(WebDriver driver) {
        super(driver);
    }

    public SearchPatientPage navigateTo() {
        driver.get(ConfigLoader.getUiBaseUrl()
                + "/coreapps/findpatient/findPatient.page?app=coreapps.findPatient");
        waitForVisible(SEARCH_BOX); // wait for SPA to render before any interactions
        return this;
    }

    /**
     * Types the query then waits for the results panel or a "no results" message
     * to become visible. In a fresh CI session there are no recently-viewed patients,
     * so the results container is hidden on page load — we type first and let the
     * AJAX search make it appear.
     */
    public SearchPatientPage searchFor(String query) {
        var box = waitForVisible(SEARCH_BOX);
        box.clear();
        box.sendKeys(query);
        wait.until(ExpectedConditions.or(
            ExpectedConditions.visibilityOfElementLocated(RESULTS_CONTAINER),
            ExpectedConditions.visibilityOfElementLocated(NO_RESULTS_TEXT)
        ));
        return this;
    }

    public int getResultsCount() {
        return driver.findElements(RESULT_ROWS).size();
    }

    public boolean isNoResultsMessageDisplayed() {
        return !driver.findElements(NO_RESULTS_TEXT).isEmpty();
    }
}
