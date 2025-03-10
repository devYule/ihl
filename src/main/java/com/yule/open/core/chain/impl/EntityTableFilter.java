package com.yule.open.core.chain.impl;

import com.yule.open.core.chain.Chain;
import com.yule.open.core.chain.impl.data.GenerateTargetTables;
import com.yule.open.database.DatabaseAdapter;
import com.yule.open.entity.EntityAdapter;
import com.yule.open.mediator.EntityTableMediator;
import com.yule.open.mediator.impl.DefaultEntityTableMediator;

import java.util.List;

import static com.yule.open.core.IHLProcessor.context;
import static com.yule.open.utils.Logger.collectionBatchInfo;
import static com.yule.open.utils.Logger.info;

public class EntityTableFilter extends Chain {


    public EntityTableFilter(int order) {
        super(order);
    }

    @Override
    public boolean execute() {

        EntityAdapter entityAdapter = context.getContext(EntityAdapter.class);
        DatabaseAdapter databaseAdapter = context.getContext(DatabaseAdapter.class);

        info("Comparing Table and Entity class...");
        EntityTableMediator mediator = new DefaultEntityTableMediator(entityAdapter, databaseAdapter);
        List<String> toEntityTables = mediator.compareToTables();

        GenerateTargetTables generateTargetTables = new GenerateTargetTables(toEntityTables);
        context.addContext(GenerateTargetTables.class, generateTargetTables);

        info("Find " + toEntityTables.size() + " Tables for Entity Mapping...");
        collectionBatchInfo("elements: ", toEntityTables);

        if (toEntityTables.isEmpty()) {
            info("You already have all the entities...");
            return false;
        }

        return doNext();
    }
}
