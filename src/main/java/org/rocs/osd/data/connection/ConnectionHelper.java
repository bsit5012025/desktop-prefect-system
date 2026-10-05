package org.rocs.osd.data.connection;

import org.rocs.osd.controller.sms.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * This class is used create a connection to the database.
 *
 * The connection details are resolved in this order, so the same build
 * can run against a developer's local Oracle instance or the production
 * database on the VPS without a code change:
 *   1. JVM system properties: -Ddb.url=... -Ddb.username=... -Ddb.password=...
 *   2. Environment variables: DB_URL, DB_USERNAME, DB_PASSWORD
 *   3. config.properties keys: db.url, db.username, db.password
 *   4. A localhost default, for local development only.
 */
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
