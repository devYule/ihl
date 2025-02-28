package com.yule.open.entity;

import com.yule.open.info.Table;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultEntityAdapter implements EntityAdapter {
    private final Elements elementUtils;
    private final String[] expectEntityPath;
    private int validEntityPathIdx;

    private static List<String> alreadyEntityNames;

    public DefaultEntityAdapter(Elements el) {
        elementUtils = el;
        expectEntityPath = new String[]{"javax.persistence.Entity", "jakarta.persistence.Entity"};
        validEntityPathIdx = -1;
        alreadyEntityNames = new ArrayList<>();
    }


    public int validateJPADependency() {
        for (int i = 0; i < expectEntityPath.length; i++) {
            if (elementUtils.getTypeElement(expectEntityPath[i]) != null) {
                validEntityPathIdx = i;
                break;
            }
        }
        return validEntityPathIdx;
    }

    public boolean hasEntityAnnotation(Element el) {

        for (AnnotationMirror mirror : el.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals(expectEntityPath[validEntityPathIdx])) {
                boolean namedByAnnotationFlag = false;
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
                    if ("name".equals(entry.getKey().getSimpleName().toString())) {
                        String val = entry.getValue().getValue().toString();
                        if (val != null && !val.isEmpty()) {
                            alreadyEntityNames.add(val.toLowerCase().replaceAll("_", ""));
                            namedByAnnotationFlag = true;
                            break;
                        }
                    }
                }
                if (!namedByAnnotationFlag) {
                    alreadyEntityNames.add(el.getSimpleName().toString().toLowerCase().replaceAll("_", ""));
                }
                return true;
            }
        }
        return false;
    }

    public List<String> getAlreadyEntityNames() {
        return alreadyEntityNames;
    }
    public String getJPADependencyPath() {
        String entityPath = expectEntityPath[validEntityPathIdx];
        return expectEntityPath[validEntityPathIdx].substring(0, entityPath.lastIndexOf("."));
    }
}
