package com.yule.open.javapoet.spec.generator;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import com.yule.open.core.IHLProcessor;
import com.yule.open.database.data.Table;
import com.yule.open.database.enums.DatabaseKind;
import com.yule.open.javapoet.properties.AnnotationProperties;
import com.yule.open.properties.Environment;
import com.yule.open.utils.LombokAnnotationGenerator;
import com.yule.open.javapoet.spec.wrapper.impl.TypeSpecWrapper;
import com.yule.open.utils.NameGenerator;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.yule.open.core.IHLProcessor.context;
import static com.yule.open.database.ConnectionFactory.*;
import static com.yule.open.database.enums.DatabaseKind.MYSQL;
import static com.yule.open.database.enums.DatabaseKind.ORACLE;
import static com.yule.open.properties.enums.EnvironmentProperties.*;

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
        return TypeSpec.classBuilder(context.getContext(NameGenerator.class).generateEntityName(table.getTbNm()))
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
            List<TypeSpec> additionalBuild = t.getAdditionalBuild(lomboks);
            result.addAll(additionalBuild);

            AnnotationProperties.Table namePropsInTableAnnotation = AnnotationProperties.Table.NAME;
            AnnotationProperties.Table schemaPropsInTableAnnotation = AnnotationProperties.Table.SCHEMA;
            DatabaseKind dbKind = getDatabaseKind();
            result.add(t.getBuilder()
                    .addAnnotations(lomboks)
                    .addAnnotation(ClassName.get(Environment.get(AnnotationProcessor.JPA_DEPENDENCY), "Entity"))
                    .addAnnotation(AnnotationSpec.builder(ClassName.get(Environment.get(AnnotationProcessor.JPA_DEPENDENCY), "Table"))
                            .addMember(namePropsInTableAnnotation.getName(), namePropsInTableAnnotation.getFormat(), t.getTbNm())
                            .addMember(schemaPropsInTableAnnotation.getName(), schemaPropsInTableAnnotation.getFormat(),
                                    Environment.get(dbKind == ORACLE ? Required.ORACLE_SCHEMA : dbKind == MYSQL ? Required.MY_SQL_AND_MARIA_DB : null))
                            .build())
                    .build());
        });
        return result;
    }

}
