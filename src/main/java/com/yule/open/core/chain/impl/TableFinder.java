package com.yule.open.core.chain.impl;

import com.yule.open.core.chain.Chain;
import com.yule.open.database.DatabaseAdapter;
import com.yule.open.properties.Environment;
import com.yule.open.properties.enums.EnvironmentProperties;

import java.util.List;

import static com.yule.open.core.IHLProcessor.context;
import static com.yule.open.utils.Logger.collectionBatchInfo;
import static com.yule.open.utils.Logger.info;

public class TableFinder extends Chain {
    public TableFinder(int order) {
        super(order);
    }

    @Override
    public boolean execute() {
        DatabaseAdapter databaseAdapter = context.getContext(DatabaseAdapter.class);
        List<String> tables = databaseAdapter.findTables(Environment.get(EnvironmentProperties.Required.DB_NAME));
        info("Connection Success...");
        collectionBatchInfo("elements: ", tables);

        return doNext();
    }
}
