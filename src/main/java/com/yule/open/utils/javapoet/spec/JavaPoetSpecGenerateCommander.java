package com.yule.open.utils.javapoet.spec;

import com.squareup.javapoet.TypeSpec;
import com.yule.open.database.data.Column;
import com.yule.open.database.data.Constraint;
import com.yule.open.database.data.Table;
import com.yule.open.utils.javapoet.annotations.AnnotationSpecGenerator;
import com.yule.open.utils.javapoet.spec.generator.FieldSpecGenerator;
import com.yule.open.utils.javapoet.spec.generator.TypeSpecGenerator;
import com.yule.open.utils.javapoet.spec.wrapper.impl.FieldSpecWrapper;

import java.util.*;

public class JavaPoetSpecGenerateCommander {

    private final TypeSpecGenerator typeSpecGenerator;
    private final FieldSpecGenerator fieldSpecGenerator;
    private final AnnotationSpecGenerator annotationSpecGenerator;

    public JavaPoetSpecGenerateCommander(int wholeNodeSize) {

        // init holders - Builder or Wrapper with a Builder & parentId.
        // batch processing without a build process.
        // If a 'spec' is built, it will be settled.

        // Generate generators
        this.typeSpecGenerator = new TypeSpecGenerator(wholeNodeSize);
        this.fieldSpecGenerator = new FieldSpecGenerator(wholeNodeSize);
        this.annotationSpecGenerator = new AnnotationSpecGenerator(wholeNodeSize);
    }

    // Overload
    // Command
    public void generate(Table table, int myIdx) {
        typeSpecGenerator.generate(table, myIdx);
    }

    // Overload
    // Command
    public void generate(Column column, int myIdx, int parentIdx) {
        fieldSpecGenerator.generate(typeSpecGenerator.getTs()[parentIdx], column, myIdx, parentIdx);
    }

    // Overload
    // Command
    public void generate(Constraint constraint, int myIdx, int parentIdx) {

        FieldSpecWrapper fs = fieldSpecGenerator.getFs()[parentIdx];
        annotationSpecGenerator.generate(
                typeSpecGenerator.getTs()[fs.getParent()],
                fs,
                constraint,
                myIdx,
                parentIdx
        );

    }

    /**
     * Template Method.
     * Mediator.
     */
    public List<TypeSpec> build() {
        annotationSpecGenerator.build(fieldSpecGenerator.getFs());
        fieldSpecGenerator.build(typeSpecGenerator.getTs());
        return typeSpecGenerator.build();
    }

    public enum AdditionalTypeKind {
        EMBEDDABLE(0, "Embedded_", "");

        private final int idx;
        private final String additionalTypePrefix;
        private final String additionalTypeSuffix;

        AdditionalTypeKind(int idx, String additionalTypePrefix, String additionalTypeSuffix) {
            this.idx = idx;
            this.additionalTypePrefix = additionalTypePrefix;
            this.additionalTypeSuffix = additionalTypeSuffix;
        }

        public String getAdditionalTypePrefix() {
            return additionalTypePrefix;
        }

        public String getAdditionalTypeSuffix() {
            return additionalTypeSuffix;
        }

        public int getIdx() {
            return idx;
        }
    }


}
