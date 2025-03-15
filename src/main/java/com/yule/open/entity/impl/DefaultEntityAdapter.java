package com.yule.open.entity.impl;

import com.yule.open.entity.EntityAdapter;
import com.yule.open.utils.NameGenerator;

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
    private final String[] expectTablePath;
    private int validEntityPathIdx;

    private static List<String> alreadyEntityNames;

    public DefaultEntityAdapter(Elements el) {
        elementUtils = el;
        expectEntityPath = new String[]{"javax.persistence.Entity", "jakarta.persistence.Entity"};
        expectTablePath = new String[]{"javax.persistence.Table", "jakarta.persistence.Table"};
        validEntityPathIdx = -1;
        alreadyEntityNames = new ArrayList<>();
    }


    public int resolveEntityPath() {
        for (int i = 0; i < expectEntityPath.length; i++) {
            if (elementUtils.getTypeElement(expectEntityPath[i]) != null) {
                validEntityPathIdx = i;
                break;
            }
        }
        return validEntityPathIdx;
    }

    public boolean hasEntityAnnotation(Element el, NameGenerator nameGenerator) {
        boolean isEntity = false;
        for (AnnotationMirror mirror : el.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals(expectEntityPath[validEntityPathIdx])) {
                isEntity = true;
                break;
            }
        }

        if (!isEntity) return isEntity;

        for (AnnotationMirror mirror : el.getAnnotationMirrors()) {
            if (!mirror.getAnnotationType().toString().equals(expectTablePath[validEntityPathIdx])) continue;
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
                if ("name".equals(entry.getKey().getSimpleName().toString())) {
                    String val = entry.getValue().getValue().toString();
                    String target = "";
                    if (val != null && !val.isEmpty()) {
                        target = val.toLowerCase().replaceAll("_", "");
                    } else {
                        target = el.getSimpleName().toString().replaceAll("_", "");
                    }
                    alreadyEntityNames.add(nameGenerator.extractOriginalName(target));
                    return isEntity;
                }
            }
        }

        return isEntity;
    }

    public List<String> getAlreadyEntityNames() {
        return alreadyEntityNames;
    }

    public String getJPADependencyPath() {
        String entityPath = expectEntityPath[validEntityPathIdx];
        return expectEntityPath[validEntityPathIdx].substring(0, entityPath.lastIndexOf("."));
    }
}
