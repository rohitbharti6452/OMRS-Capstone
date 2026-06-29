package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads config.properties once at class-initialization time.
 * Any key can be overridden at runtime via -Dkey=value (system property wins).
 */
public class ConfigLoader {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on classpath");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
    }

    private ConfigLoader() {}

    /** Returns the value for {@code key}, with system property taking priority. */
    public static String get(String key) {
        String value = System.getProperty(key, PROPS.getProperty(key));
        if (value == null) {
            throw new IllegalStateException("Missing required config key: " + key);
        }
        return value.trim();
    }

    public static String getBaseUrl()             { return get("base.url"); }
    public static String getUiBaseUrl()           { return get("ui.base.url"); }
    public static String getAdminUsername()       { return get("admin.username"); }
    public static String getAdminPassword()       { return get("admin.password"); }
    public static String getIdentifierTypeUuid()  { return get("identifier.type.uuid"); }
    public static String getLocationUuid()        { return get("location.uuid"); }
    public static String getBrowser()             { return get("browser"); }
    public static boolean isHeadless()            { return Boolean.parseBoolean(get("headless")); }
    public static int getExplicitWait()           { return Integer.parseInt(get("explicit.wait")); }
}
