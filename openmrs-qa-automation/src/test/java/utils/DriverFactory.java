package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Creates a WebDriver instance based on config.properties (browser, headless).
 * Both values can be overridden per-run, e.g. -Dheadless=false -Dbrowser=firefox,
 * since ConfigLoader.get() checks system properties before falling back to the file.
 */
public class DriverFactory {

    private DriverFactory() {}

    public static WebDriver createDriver() {
        boolean headless = ConfigLoader.isHeadless();
        return switch (ConfigLoader.getBrowser().toLowerCase()) {
            case "firefox" -> createFirefoxDriver(headless);
            default -> createChromeDriver(headless);
        };
    }

    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        // Required for CI/containerized environments (GitHub Actions, Docker, etc.)
        options.addArguments(
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--disable-notifications",
            "--remote-allow-origins=*"
        );
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return new FirefoxDriver(options);
    }
}
