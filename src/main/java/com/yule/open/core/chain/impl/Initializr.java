package com.yule.open.core.chain.impl;

import com.yule.open.annotations.EnableEntityGenerator;
import com.yule.open.core.chain.Chain;
import com.yule.open.entity.EntityAdapter;
import com.yule.open.entity.impl.DefaultEntityAdapter;
import com.yule.open.utils.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

import java.lang.annotation.Annotation;
import java.util.Set;

import static com.yule.open.core.IHLProcessor.context;

public class Initializr extends Chain {
    private final Class<? extends Annotation> annotationType;

    public Initializr(int order, Class<? extends Annotation> requiredAnnotationType) {
        super(order);
        this.annotationType = requiredAnnotationType;
    }

    @Override
    public boolean execute() {
        Logger.setMessager(context.getContext(ProcessingEnvironment.class).getMessager());
        EntityAdapter entityAdapter = new DefaultEntityAdapter(context.getContext(ProcessingEnvironment.class).getElementUtils());
        context.addContext(EntityAdapter.class, entityAdapter);

        Set<? extends Element> elements = context.getContext(RoundEnvironment.class).getElementsAnnotatedWith(annotationType);
        context.addContext(Set.class, elements);

        return doNext();
    }

}
