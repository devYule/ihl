package com.yule.open.database.impl.graph;

import com.yule.open.database.DatabaseAdapter;
import com.yule.open.database.data.AnalyseResult;

import java.util.Collections;
import java.util.List;

public class MySQLNodeDatabaseAdapter extends DatabaseAdapter {
    @Override
    public List<String> findTables(String dbname) {
        return Collections.emptyList();
    }

    @Override
    public AnalyseResult analyseAllTablesAndBatchSources(String owner, List<String> tableName) {
        return null;
    }
}
