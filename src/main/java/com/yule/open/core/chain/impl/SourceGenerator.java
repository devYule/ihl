package com.yule.open.core.chain.impl;

import com.squareup.javapoet.TypeSpec;
import com.yule.open.core.chain.Chain;
import com.yule.open.database.data.AnalyseResult;
import com.yule.open.javapoet.source.JavapoetNodeBatchSourceGenerator;
import com.yule.open.properties.Environment;
import com.yule.open.properties.enums.EnvironmentProperties;

import java.util.List;

import static com.yule.open.core.IHLProcessor.context;
import static com.yule.open.utils.Logger.info;

public class SourceGenerator extends Chain {
    public SourceGenerator(int order) {
        super(order);
    }

    @Override
    public boolean execute() {
        AnalyseResult entityTables = context.getContext(AnalyseResult.class);

        info("Generate Source...");
        List<TypeSpec> generated = new JavapoetNodeBatchSourceGenerator<>().generate(entityTables);
        info(generated.size() + " Entity files generated in " + Environment.get(EnvironmentProperties.Required.ENTITY_PATH) + "...");
        info("Done...");
        info("Success to make Entity Sources!!!");

        return doNext();
    }
}
