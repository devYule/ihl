package com.yule.open.database.impl.graph;

import com.yule.open.database.ConnectionFactory;
import com.yule.open.database.DatabaseAdapter;

import java.sql.SQLException;
import java.util.*;


public abstract class NodeDatabaseAdapter extends DatabaseAdapter {

    public NodeDatabaseAdapter() {
        super.allTables = new ArrayList<>();
        try {
            ConnectionFactory.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
