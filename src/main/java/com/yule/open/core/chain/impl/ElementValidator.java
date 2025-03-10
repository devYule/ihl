package com.yule.open.core.chain.impl;

import com.yule.open.core.chain.Chain;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.Set;

import static com.yule.open.core.IHLProcessor.context;
import static com.yule.open.properties.enums.ErrorMessageProperties.ANNOTATION_DUPLICATED;
import static com.yule.open.properties.enums.ProcessingMessageProperties.FIND_ANNOTATION;
import static com.yule.open.utils.Logger.error;
import static com.yule.open.utils.Logger.info;
import static com.yule.open.utils.Validator.isOver;

public class ElementValidator extends Chain {

    private final Class<? extends Annotation> annotationType;

    public ElementValidator(int order, Class<? extends Annotation> annotationType) {
        super(order);
        this.annotationType = annotationType;
    }

    @Override
    public boolean execute() {

        Set<? extends Element> elements = context.getContext(RoundEnvironment.class).getElementsAnnotatedWith(annotationType);
        context.addContext(Element.class, elements);

        if (!validate(elements)) return true;

        return doNext();
    }

    private static boolean validate(Set<? extends Element> elements) {
        info(FIND_ANNOTATION.getProc());
        if (elements == null || elements.isEmpty()) return false;
        if (isOver(1, elements.size())) error(ANNOTATION_DUPLICATED.getMessage());
        info(FIND_ANNOTATION.getSuccess());
        return true;
    }
}
