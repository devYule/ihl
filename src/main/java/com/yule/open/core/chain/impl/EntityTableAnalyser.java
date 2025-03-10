package com.yule.open.core.chain.impl;

import com.yule.open.core.chain.Chain;
import com.yule.open.core.chain.impl.data.GenerateTargetTables;
import com.yule.open.database.DatabaseAdapter;
import com.yule.open.database.data.AnalyseResult;
import com.yule.open.properties.Environment;
import com.yule.open.properties.enums.EnvironmentProperties;

import java.util.List;

import static com.yule.open.core.IHLProcessor.context;
import static com.yule.open.utils.Logger.info;

public class EntityTableAnalyser extends Chain {
    public EntityTableAnalyser(int order) {
        super(order);
    }

    @Override
    public boolean execute() {
        DatabaseAdapter databaseAdapter = context.getContext(DatabaseAdapter.class);
        String dbname = Environment.get(EnvironmentProperties.Required.DB_NAME);
        List<String> toEntityTables = context.getContext(GenerateTargetTables.class).getTables();

        info("Analyse your tables with column and constraints...");
        AnalyseResult entityTables = databaseAdapter.analyseAllTablesAndBatchSources(dbname, toEntityTables);
        context.addContext(AnalyseResult.class, entityTables);
        info("All tables, columns and constraints READY...");
        return doNext();
    }
}
