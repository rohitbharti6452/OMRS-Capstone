package utils;

import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Factory for pre-configured REST Assured RequestSpecifications.
 * Call ApiClient.adminSpec() to get a spec with Basic Auth, base URI,
 * JSON content type, and request/response logging already wired in.
 */
public class ApiClient {

    private static RequestSpecification adminSpec;

    private ApiClient() {}

    /** Returns a singleton RequestSpecification authenticated as admin. */
    public static synchronized RequestSpecification adminSpec() {
        if (adminSpec == null) {
            PreemptiveBasicAuthScheme auth = new PreemptiveBasicAuthScheme();
            auth.setUserName(ConfigLoader.getAdminUsername());
            auth.setPassword(ConfigLoader.getAdminPassword());

            adminSpec = new RequestSpecBuilder()
                    .setBaseUri(ConfigLoader.getBaseUrl())
                    .setContentType(ContentType.JSON)
                    .setAuth(auth)
                    .addFilter(new RequestLoggingFilter())
                    .addFilter(new ResponseLoggingFilter())
                    .build();
        }
        return adminSpec;
    }
}
