package com.yule.open.javapoet.spec.wrapper.impl;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import com.yule.open.properties.Environment;
import com.yule.open.properties.enums.EnvironmentProperties;
import com.yule.open.javapoet.spec.JavaPoetSpecGenerateCommander;
import com.yule.open.javapoet.spec.wrapper.SpecWrapper;

import java.util.ArrayList;
import java.util.List;

import static com.yule.open.core.IHLProcessor.nameGenerator;

public class TypeSpecWrapper extends SpecWrapper {
    private final TypeSpec.Builder builder;
    private final TypeSpec.Builder[] additionalBuilder;
    private final String tbNm;
    private boolean isAddedEmbeddableAnnotation;
    private final String[] additionalTypeNames;


    public TypeSpecWrapper(TypeSpec.Builder builder, String tbNm) {
        this.builder = builder;
        this.additionalBuilder = new TypeSpec.Builder[JavaPoetSpecGenerateCommander.AdditionalTypeKind.values().length];
        this.additionalTypeNames = new String[JavaPoetSpecGenerateCommander.AdditionalTypeKind.values().length];
        this.isAddedEmbeddableAnnotation = false;
        this.tbNm = tbNm;
    }

    public boolean addAnnotationAtAdditionalType(JavaPoetSpecGenerateCommander.AdditionalTypeKind kind) {
        if (kind == JavaPoetSpecGenerateCommander.AdditionalTypeKind.EMBEDDABLE) {
            this.additionalBuilder[kind.getIdx()]
                    .addAnnotation(ClassName.get(Environment.get(EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY), "Embeddable"))
                    .addSuperinterface(ClassName.get(Environment.get(EnvironmentProperties.AnnotationProcessor.JAVA_IO), "Serializable"));
            return this.isAddedEmbeddableAnnotation = true;
        }
        return false;
    }

    public boolean isAddedEmbeddableAnnotation() {
        return isAddedEmbeddableAnnotation;
    }

    public TypeSpec.Builder getBuilder() {
        return builder;
    }

    public TypeSpec.Builder getAdditionalBuilder(JavaPoetSpecGenerateCommander.AdditionalTypeKind kind) {
        return additionalBuilder[kind.getIdx()];
    }

    public TypeSpec.Builder[] getAdditionalBuilder() {
        return additionalBuilder;
    }

    public String getTbNm() {
        return tbNm;
    }

    public int addAdditionalBuilder(JavaPoetSpecGenerateCommander.AdditionalTypeKind kind) {
        if (this.additionalBuilder[kind.getIdx()] == null) {
            if (this.additionalTypeNames[kind.getIdx()] == null) {
                this.additionalTypeNames[kind.getIdx()] = kind.getAdditionalTypePrefix() + tbNm + kind.getAdditionalTypeSuffix();
            }

            this.additionalBuilder[kind.getIdx()] = TypeSpec.classBuilder(nameGenerator.generateEntityName(additionalTypeNames[kind.getIdx()]));
        }

        return kind.getIdx();
    }

    public String getAdditionalTypeName(JavaPoetSpecGenerateCommander.AdditionalTypeKind kind) {
        return this.additionalTypeNames[kind.getIdx()];
    }

    public List<TypeSpec> getAdditionalBuild(List<AnnotationSpec> lomboks) {
        List<TypeSpec> result = new ArrayList<>();

        for (int i = 0; i < this.additionalBuilder.length; i++) {
            if (additionalBuilder[i] == null) continue;
            result.add(additionalBuilder[i].addAnnotations(lomboks).build());

        }

        return result;
    }

}
