package base;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import utils.ApiClient;
import utils.ConfigLoader;

/**
 * All API test classes extend BaseTest.
 * Provides a ready-to-use {@code spec} field with Basic Auth and logging.
 */
public abstract class BaseTest {

    protected static RequestSpecification spec;

    @BeforeAll
    static void setUpRestAssured() {
        RestAssured.baseURI = ConfigLoader.getBaseUrl();
        spec = ApiClient.adminSpec();
    }
}
