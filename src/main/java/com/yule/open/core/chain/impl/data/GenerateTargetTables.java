package com.yule.open.core.chain.impl.data;

import java.util.List;

public class GenerateTargetTables {
    private final List<String> tables;

    public GenerateTargetTables(List<String> tables) {
        this.tables = tables;
    }

    public List<String> getTables() {
        return tables;
    }
}
