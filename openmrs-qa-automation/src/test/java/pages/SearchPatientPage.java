package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
        return this;
    }

    /**
     * Types the query and waits for the results panel to actually update.
     * The page pre-populates #patient-search-results with "recently viewed"
     * rows on load, so simply waiting for "any row to exist" would pass
     * immediately on stale data — instead this snapshots the panel's text
     * before typing and waits for it to change (or for the explicit
     * "No matching records found" message), which only happens once the
     * debounced AJAX search has actually completed.
     */
    public SearchPatientPage searchFor(String query) {
        String beforeText = waitForVisible(RESULTS_CONTAINER).getText();
        var box = waitForVisible(SEARCH_BOX);
        box.clear();
        box.sendKeys(query);

        wait.until(d -> !d.findElements(NO_RESULTS_TEXT).isEmpty()
                || !d.findElement(RESULTS_CONTAINER).getText().equals(beforeText));
        return this;
    }

    public int getResultsCount() {
        return driver.findElements(RESULT_ROWS).size();
    }

    public boolean isNoResultsMessageDisplayed() {
        return !driver.findElements(NO_RESULTS_TEXT).isEmpty();
    }
}
