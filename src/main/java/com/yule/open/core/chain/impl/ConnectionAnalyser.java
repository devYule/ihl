package com.yule.open.core.chain.impl;

import com.yule.open.core.chain.Chain;
import com.yule.open.database.DatabaseAdapter;
import com.yule.open.database.enums.DatabaseKind;
import com.yule.open.database.impl.graph.NodeDatabaseAdapter;
import com.yule.open.database.impl.graph.OracleNodeDatabaseAdapter;
import com.yule.open.properties.Environment;
import com.yule.open.properties.enums.EnvironmentProperties;

import javax.annotation.processing.ProcessingEnvironment;

import static com.yule.open.core.IHLProcessor.context;
import static com.yule.open.database.ConnectionFactory.getDatabaseKind;
import static com.yule.open.properties.enums.ErrorMessageProperties.SCHEMA_OR_DATABASE_NAME_IS_NOT_PROVIDED;
import static com.yule.open.utils.Logger.error;
import static com.yule.open.utils.Logger.info;
import static com.yule.open.utils.Validator.isNull;

public class ConnectionAnalyser extends Chain {

    public ConnectionAnalyser(int order) {
        super(order);
    }

    @Override
    public boolean execute() {
        ProcessingEnvironment processingEnv = context.getContext(ProcessingEnvironment.class);
        info("Connect to database...");
        DatabaseAdapter databaseAdapter = new OracleNodeDatabaseAdapter();
        context.addContext(DatabaseAdapter.class, databaseAdapter);
        info("Check your database name...");
        DatabaseKind databaseKind = getDatabaseKind();
        String dbname = null;
        if (databaseKind == DatabaseKind.ORACLE) {
            info("Your database kind is ORACLE...");
            Environment.put(EnvironmentProperties.Required.ORACLE_SCHEMA, dbname = processingEnv.getOptions().get(EnvironmentProperties.Required.ORACLE_SCHEMA.getEnv()));
        } else if (databaseKind == DatabaseKind.MYSQL || databaseKind == DatabaseKind.MARIADB) {
            info("Your database kind is MYSQL(MARIADB)...");
            Environment.put(EnvironmentProperties.Required.MY_SQL_AND_MARIA_DB, dbname = processingEnv.getOptions().get(EnvironmentProperties.Required.MY_SQL_AND_MARIA_DB.getEnv()));
        }
        Environment.put(EnvironmentProperties.Required.DB_NAME, dbname);
        if (isNull(dbname)) error(SCHEMA_OR_DATABASE_NAME_IS_NOT_PROVIDED.getMessage());
        info("Database is found...");

        return doNext();
    }
}
