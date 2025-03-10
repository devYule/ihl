package com.yule.open.core.chain.impl;

import com.yule.open.core.chain.Chain;
import com.yule.open.properties.Environment;
import com.yule.open.properties.enums.EnvironmentProperties;
import com.yule.open.utils.NameGenerator;

import static com.yule.open.core.IHLProcessor.context;

public class NameGeneratorGenerator extends Chain {
    public NameGeneratorGenerator(int order) {
        super(order);
    }

    @Override
    public boolean execute() {
        NameGenerator nameGenerator = new NameGenerator(Environment.get(EnvironmentProperties.Required.ENTITY_NAME_PREFIX),
                Environment.get(EnvironmentProperties.Required.ENTITY_NAME_SUFFIX));
        context.addContext(NameGenerator.class, nameGenerator);

        return doNext();
    }
}
