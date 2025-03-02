package com.yule.open.database;

import com.yule.open.database.enums.DatabaseKind;
import com.yule.open.properties.Environment;
import com.yule.open.properties.EnvironmentProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.yule.open.utils.Logger.error;

public abstract class ConnectionFactory {

    private static Connection conn;
    private static DatabaseKind databaseKind;
    private static final Map<String, String[]> DRIVER_MAP;

    static {
        DRIVER_MAP = new HashMap<>();
        // MySQL
        DRIVER_MAP.put("jdbc:mysql:", new String[]{"com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver"});
        // Oracle
        DRIVER_MAP.put("jdbc:oracle:", new String[]{"oracle.jdbc.OracleDriver", "oracle.jdbc.driver.OracleDriver"});
        // MariaDB
        DRIVER_MAP.put("jdbc:mariadb:", new String[]{"org.mariadb.jdbc.Driver"});
    }

    public static Connection getConnection() throws SQLException {
        String url = Environment.get(EnvironmentProperties.Required.DB_URL);
        String username = Environment.get(EnvironmentProperties.Required.DB_USERNAME);
        String password = Environment.get(EnvironmentProperties.Required.DB_PASSWORD);

        if (conn == null) {
            ClassNotFoundException err = new ClassNotFoundException();
            for (Map.Entry<String, String[]> entry : DRIVER_MAP.entrySet()) {
                if (url.startsWith(entry.getKey())) {
                    for (String driver : entry.getValue()) {
                        try {
                            Class.forName(driver);
                            databaseKind = DatabaseKind.getByValue(entry.getKey());
                            conn = DriverManager.getConnection(url, username, password);
                        } catch (ClassNotFoundException e) {
                            err.addSuppressed(e);
                        }
                    }
                    break;
                }
            }
        }
        if (conn == null) error("Connection to Database fail!");
        return conn;
    }

    public static DatabaseKind getDatabaseKind() {
        return databaseKind;
    }

    public static void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}