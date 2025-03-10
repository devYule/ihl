package com.yule.open.javapoet.spec;

import com.squareup.javapoet.TypeSpec;
import com.yule.open.database.data.Column;
import com.yule.open.database.data.Constraint;
import com.yule.open.database.data.Table;
import com.yule.open.javapoet.spec.generator.ConstraintsAnnotationSpecGenerator;
import com.yule.open.javapoet.spec.generator.FieldSpecGenerator;
import com.yule.open.javapoet.spec.generator.TypeSpecGenerator;
import com.yule.open.javapoet.spec.wrapper.impl.FieldSpecWrapper;

import java.util.*;

public class JavaPoetSpecGenerateCommander {

    private final TypeSpecGenerator typeSpecGenerator;
    private final FieldSpecGenerator fieldSpecGenerator;
    private final ConstraintsAnnotationSpecGenerator constraintsAnnotationSpecGenerator;

    public JavaPoetSpecGenerateCommander(int wholeNodeSize) {

        // init holders - Builder or Wrapper with a Builder & parentId.
        // batch processing without a build process.
        // If a 'spec' is built, it will be settled.

        // Generate generators
        this.typeSpecGenerator = new TypeSpecGenerator(wholeNodeSize);
        this.fieldSpecGenerator = new FieldSpecGenerator(wholeNodeSize);
        this.constraintsAnnotationSpecGenerator = new ConstraintsAnnotationSpecGenerator(wholeNodeSize);
    }

    // Overload
    // Command
    public void generate(Table table, int myIdx) {
        typeSpecGenerator.generate(table, myIdx);
    }

    // Overload
    // Command
    public void generate(Column column, int myIdx, int parentIdx) {
        fieldSpecGenerator.generate(column, myIdx, parentIdx);
    }

    // Overload
    // Command
    public void generate(Constraint constraint, int parentIdx) {

        FieldSpecWrapper fs = fieldSpecGenerator.getFs()[parentIdx];
        constraintsAnnotationSpecGenerator.generate(
                typeSpecGenerator.getTs()[fs.getParent()],
                fs,
                constraint,
                parentIdx
        );

    }

    /**
     * Template Method.
     * Mediator.
     */
    public List<TypeSpec> build() {
        constraintsAnnotationSpecGenerator.build(fieldSpecGenerator.getFs());
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
