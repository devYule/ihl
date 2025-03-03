package com.yule.open.utils.javapoet.spec.generator;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import com.yule.open.core.IHLProcessor;
import com.yule.open.database.data.Table;
import com.yule.open.properties.Environment;
import com.yule.open.properties.enums.EnvironmentProperties;
import com.yule.open.utils.LombokAnnotationGenerator;
import com.yule.open.utils.javapoet.spec.wrapper.impl.TypeSpecWrapper;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeSpecGenerator {
    private final TypeSpecWrapper[] ts;


    public TypeSpecGenerator(int wholeNodeSize) {
        this.ts = new TypeSpecWrapper[wholeNodeSize];
    }

    public void generate(Table table, int myIdx) {
        if (ts[myIdx] == null) ts[myIdx] = new TypeSpecWrapper(generateSpec(table), table.getTbNm());
    }

    private TypeSpec.Builder generateSpec(Table table) {

        /* generate process */
        return TypeSpec.classBuilder(IHLProcessor.nameGenerator.generateEntityName(table.getTbNm()))
                .addModifiers(Modifier.PUBLIC);

    }

    public TypeSpecWrapper[] getTs() {
        return ts;
    }

    public List<TypeSpec> build() {
        List<AnnotationSpec> lomboks = new LombokAnnotationGenerator().get();
        List<TypeSpec> result = new ArrayList<>();
        Arrays.stream(this.ts).forEach(t -> {
            if (t == null) return;
            System.out.println("t.getPkCnt() = " + t.getPkCnt());
            List<TypeSpec> additionalBuild = t.getAdditionalBuild(lomboks);
            result.addAll(additionalBuild);
            result.add(t.getBuilder()
                    .addAnnotation(ClassName.get(Environment.get(EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY), "Entity"))
                    .addAnnotations(lomboks)
                    .build());

        });
        return result;
    }

}
