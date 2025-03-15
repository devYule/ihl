package com.yule.open.mediator.impl;

import com.yule.open.database.DatabaseAdapter;
import com.yule.open.entity.EntityAdapter;
import com.yule.open.mediator.EntityTableMediator;

import java.util.ArrayList;
import java.util.List;

public class DefaultEntityTableMediator extends EntityTableMediator {

    private List<String> toEntityTables;

    public DefaultEntityTableMediator(EntityAdapter entityAdapter, DatabaseAdapter databaseAdapter) {
        super(entityAdapter, databaseAdapter);
    }

    @Override
    public List<String> compareToTables() {
        // table - already entity
        // N^2 -> 어떻게 시간복잡도 줄일까?
        // 순서 없음.
        List<String> allTables = databaseAdapter.getAllTables();
        List<String> alreadyEntityNamesCopy = new ArrayList<>(entityAdapter.getAlreadyEntityNames());

        int[] forRemoveArr = new int[allTables.size()];

        for (int i = 0; i < allTables.size(); i++) {
            String cur = allTables.get(i).replaceAll("_", "");
            for (int j = 0; j < alreadyEntityNamesCopy.size(); j++) {
                String e = alreadyEntityNamesCopy.get(j);
                if (e == null) continue;
                if (cur.equalsIgnoreCase(e)) {
                    alreadyEntityNamesCopy.set(j, null);
                    forRemoveArr[i] = -1;
                    break;
                }
            }
        }
        toEntityTables = new ArrayList<>();
        for (int i = 0; i < allTables.size(); i++) {
            if (forRemoveArr[i] == -1) continue;
            toEntityTables.add(allTables.get(i));
        }

        return toEntityTables;
    }
}
