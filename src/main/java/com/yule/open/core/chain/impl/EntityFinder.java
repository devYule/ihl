package com.yule.open.core.chain.impl;

import com.yule.open.core.chain.Chain;
import com.yule.open.entity.EntityAdapter;
import com.yule.open.utils.NameGenerator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.util.Set;

import static com.yule.open.core.IHLProcessor.context;
import static com.yule.open.utils.Logger.info;

public class EntityFinder extends Chain {
    public EntityFinder(int order) {
        super(order);
    }

    @Override
    public boolean execute() {
        RoundEnvironment roundEnv = context.getContext(RoundEnvironment.class);
        EntityAdapter entityAdapter = context.getContext(EntityAdapter.class);
        NameGenerator nameGenerator = context.getContext(NameGenerator.class);

        info("Find exists Entities...");
        Set<? extends Element> allElements = roundEnv.getRootElements();
        int cnt = 0;
        for (Element el : allElements) {
            if (el.getKind() == ElementKind.CLASS && entityAdapter.hasEntityAnnotation(el, nameGenerator)) cnt++;
        }
        info(cnt + " classes has @Entity...");

        return doNext();
    }
}
