package com.yule.open.mediator;

import com.yule.open.database.DatabaseAdapter;
import com.yule.open.entity.EntityAdapter;

import java.util.List;

public abstract class EntityTableMediator {
    protected final EntityAdapter entityAdapter;
    protected final DatabaseAdapter databaseAdapter;

    public EntityTableMediator(EntityAdapter entityAdapter, DatabaseAdapter databaseAdapter) {
        this.entityAdapter = entityAdapter;
        this.databaseAdapter = databaseAdapter;
    }


    abstract public List<String> compareToTables();
}
