package com.yule.open.utils.javapoet;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.yule.open.properties.Environment;
import com.yule.open.properties.EnvironmentProperties;

import java.util.ArrayList;
import java.util.List;

public class LombokAnnotationGenerator {
    private final List<ClassName> lombokAnnotationForAdd;

    public LombokAnnotationGenerator() {
        /* Lombok */
        String lombokDependencyPath = "lombok";
        String getter = Environment.get(EnvironmentProperties.Optional.GETTER);
        String setter = Environment.get(EnvironmentProperties.Optional.SETTER);
        String noArgs = Environment.get(EnvironmentProperties.Optional.NOARGS);
        String allArgs = Environment.get(EnvironmentProperties.Optional.ALLARGS);
        String blder = Environment.get(EnvironmentProperties.Optional.BUILDER);

        this.lombokAnnotationForAdd = new ArrayList<>();
        if (getter != null && !getter.isEmpty())
            lombokAnnotationForAdd.add(ClassName.get(lombokDependencyPath, "Getter"));
        if (setter != null && !setter.isEmpty())
            lombokAnnotationForAdd.add(ClassName.get(lombokDependencyPath, "Setter"));
        if (noArgs != null && !noArgs.isEmpty())
            lombokAnnotationForAdd.add(ClassName.get(lombokDependencyPath, "NoArgsConstructor"));
        if (allArgs != null && !allArgs.isEmpty())
            lombokAnnotationForAdd.add(ClassName.get(lombokDependencyPath, "AllArgsConstructor"));
        if (blder != null && !blder.isEmpty()) lombokAnnotationForAdd.add(ClassName.get(lombokDependencyPath, "Builder"));
    }

    public List<AnnotationSpec> get() {
        List<AnnotationSpec> result = new ArrayList<>();
        for (ClassName className : lombokAnnotationForAdd) {
            result.add(AnnotationSpec.builder(className).build());
        }
        return result;
    }
}
