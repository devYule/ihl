package com.yule.open.core.chain.impl;

import com.yule.open.core.chain.Chain;
import com.yule.open.entity.EntityAdapter;
import com.yule.open.entity.impl.DefaultEntityAdapter;
import com.yule.open.utils.Logger;

import javax.annotation.processing.ProcessingEnvironment;

import static com.yule.open.core.IHLProcessor.context;

public class Initializr extends Chain {

    public Initializr(int order) {
        super(order);
    }

    @Override
    public boolean execute() {
        Logger.setMessager(context.getContext(ProcessingEnvironment.class).getMessager());
        EntityAdapter entityAdapter = new DefaultEntityAdapter(context.getContext(ProcessingEnvironment.class).getElementUtils());
        context.addContext(EntityAdapter.class, entityAdapter);

        return doNext();
    }

}
