package com.yule.open.database;

import com.yule.open.database.enums.DatabaseKind;
import com.yule.open.database.enums.QueryKind;
import com.yule.open.info.Table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.yule.open.utils.Logger.error;

public abstract class DatabaseAdapter {

    protected DatabaseKind databaseKind;
    protected Connection conn;
    protected List<String> allTables;

    public abstract List<String> findTables(String dbname);

    public abstract Table[] analyseAllTablesAndBatchSources(String owner, List<String> tableName);

    protected interface ConnectionFactory {
        Connection getConnection(String url, String username, String password) throws SQLException, ClassNotFoundException;
    }


    public String getQueryToken(QueryKind kind) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        String path = kind.getPrefix() + "query-" + databaseKind.getKind();

        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(
                             Objects.requireNonNull(classLoader.getResourceAsStream(path)),
                             StandardCharsets.UTF_8))) {

            String line;
            StringBuilder textBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) textBuilder.append(line);

            return textBuilder.toString();

        } catch (IOException e) {
            error(e.getMessage());
        }
        error("no query!");
        throw new RuntimeException();
    }

    public DatabaseKind getDatabaseKind() {
        return databaseKind;
    }

    public Connection getConn() {
        return conn;
    }

    public List<String> getAllTables() {
        return allTables;
    }
}
