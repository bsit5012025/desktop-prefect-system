package org.rocs.osd.data.connection;

import org.rocs.osd.controller.sms.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;

public final class ConnectionHelper {

    /**
     * Default database URL, used only when nothing else is configured.
     * Points at a local Oracle instance -- never production.
     */
    private static final String DEFAULT_URL =
            "jdbc:oracle:thin:@localhost:1521/oracleDB";

    /**
     * Default database username, used only when nothing else is configured.
     */
    private static final String DEFAULT_USERNAME = "rcosd";

    /**
     * Default database password, used only when nothing else is configured.
     */
    private static final String DEFAULT_PASSWORD = "Changeme0";

    /**
     * Oracle JDBC driver.
     */
    public static final String ORACLE_DRIVER =
            "oracle.jdbc.driver.OracleDriver";

    private ConnectionHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Resolves a configuration value using the following precedence:
     * system property, environment variable, configuration, and default value.
     *
     * @param systemPropertyKey the system property key
     * @param envVarKey the environment variable key
     * @param configKey the configuration key
     * @param defaultValue the default value if no configured value is found
     * @return the resolved configuration value
     */
    private static String resolve(String systemPropertyKey, String envVarKey,
                                  String configKey, String defaultValue) {
        String value = System.getProperty(systemPropertyKey);
        if (value != null && !value.isBlank()) {
            return value;
        }
        value = System.getenv(envVarKey);
        if (value != null && !value.isBlank()) {
            return value;
        }
        value = ConfigLoader.get(configKey);
        if (value != null && !value.isBlank()) {
            return value;
        }
        return defaultValue;
    }

    /**
     * This method is used to get a database connection.
     *
     *  @return a Connection object representing the database connection.
     */
    public static Connection getConnection() {
        try {
            String url = resolve("db.url", "DB_URL", "db.url", DEFAULT_URL);
            String username = resolve("db.username", "DB_USERNAME",
                    "db.username", DEFAULT_USERNAME);
            String password = resolve("db.password", "DB_PASSWORD",
                    "db.password", DEFAULT_PASSWORD);

            Class.forName(ORACLE_DRIVER).getDeclaredConstructor().newInstance();

            return DriverManager.getConnection(url, username, password);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
