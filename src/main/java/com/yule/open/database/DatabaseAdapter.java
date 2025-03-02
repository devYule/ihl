package com.yule.open.database;

import com.yule.open.database.data.AnalyseResult;
import com.yule.open.database.enums.QueryKind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static com.yule.open.database.ConnectionFactory.getDatabaseKind;
import static com.yule.open.utils.Logger.error;

public abstract class DatabaseAdapter {


    protected List<String> allTables;

    public abstract List<String> findTables(String dbname);

    public abstract AnalyseResult analyseAllTablesAndBatchSources(String owner, List<String> tableName);

    public String getQueryToken(QueryKind kind) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        String path = kind.getPrefix() + "query-" + getDatabaseKind().getKind();

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


    public List<String> getAllTables() {
        return allTables;
    }
}
