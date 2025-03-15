package com.yule.open.core.chain.impl;

import com.yule.open.core.chain.Chain;
import com.yule.open.database.DatabaseAdapter;
import com.yule.open.database.enums.DatabaseKind;
import com.yule.open.database.impl.graph.MySQLNodeDatabaseAdapter;
import com.yule.open.database.impl.graph.OracleNodeDatabaseAdapter;
import com.yule.open.properties.Environment;
import com.yule.open.properties.enums.EnvironmentProperties;
import com.yule.open.utils.Validator;


import static com.yule.open.core.IHLProcessor.context;
import static com.yule.open.database.ConnectionFactory.getDatabaseKind;
import static com.yule.open.utils.Logger.error;
import static com.yule.open.utils.Logger.info;

public class ConnectionAnalyser extends Chain {

    public ConnectionAnalyser(int order) {
        super(order);
    }

    @Override
    public boolean execute() {
        info("Connect to database...");
        info("Check your database name...");
        DatabaseKind databaseKind = getDatabaseKind();
        String dbname = null;
        DatabaseAdapter databaseAdapter = null;
        if (databaseKind == DatabaseKind.ORACLE) {
            info("Your database kind is ORACLE...");
            dbname = Environment.get(EnvironmentProperties.Required.ORACLE_SCHEMA);
            databaseAdapter = new OracleNodeDatabaseAdapter();
        } else if (databaseKind == DatabaseKind.MYSQL || databaseKind == DatabaseKind.MARIADB) {
            info("Your database kind is MYSQL(MARIADB)...");
            dbname = Environment.get(EnvironmentProperties.Required.MY_SQL_AND_MARIA_DB);
            /* TODO 2025-03-13 목 21:4
                MYSQL & MARIA DB 구현체 완성시 객체 생성 & 할당
                --by Hyunmin
            */
            databaseAdapter = new MySQLNodeDatabaseAdapter(); // empty object
        }

        if (Validator.isNull(databaseAdapter)) error("Can not find your Database kind!");

        context.addContext(DatabaseAdapter.class, databaseAdapter);
        Environment.put(EnvironmentProperties.Required.DB_NAME, dbname);
        info("Database is found...");

        return doNext();
    }
}
