package ui.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import utils.DriverFactory;

/**
 * All UI test classes extend BaseUiTest. A fresh WebDriver is created before
 * each test and quit afterward, so tests never bleed session state into each
 * other (each test logs in independently).
 */
public abstract class BaseUiTest {

    protected WebDriver driver;

    @BeforeEach
    void setUpDriver() {
        driver = DriverFactory.createDriver();
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
