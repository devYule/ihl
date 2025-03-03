package com.yule.open.utils.javapoet.spec.wrapper.impl;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.yule.open.database.data.Constraint;
import com.yule.open.database.data.enums.ConstraintsType;
import com.yule.open.properties.Environment;
import com.yule.open.utils.javapoet.spec.wrapper.Spec;
import com.yule.open.utils.javapoet.spec.wrapper.wrapper.AnnotationSpecBuilderWrapper;

import java.util.ArrayList;
import java.util.List;

import static com.yule.open.properties.enums.EnvironmentProperties.AnnotationProcessor.HIBERNATE_DEPENDENCY;
import static com.yule.open.properties.enums.EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY;

public class AnnotationSpecWrapper extends Spec {

    private final int parent;
    private final AnnotationSpec.Builder[] builder;
    private final String DEPENDENCY_JPA;
    private final String DEPENDENCY_HIBERNATE;
    private final boolean[] propIsVisited;


    public AnnotationSpecWrapper(int parent, boolean[] propIsVisited) {
        int annoCnt = AnnotationKindIndex.values().length;
        this.parent = parent;
        this.builder = new AnnotationSpec.Builder[annoCnt];
        this.DEPENDENCY_JPA = Environment.get(JPA_DEPENDENCY);
        this.DEPENDENCY_HIBERNATE = Environment.get(HIBERNATE_DEPENDENCY);
        this.propIsVisited = propIsVisited;
    }

    public AnnotationSpecWrapper decideColumnAnnotation() {
        if (this.builder[AnnotationKindIndex.MANY_TO_ONE.getIdx()] == null) {
            this.builder[AnnotationKindIndex.JOIN_COLUMN.getIdx()] = null;
        } else {
            this.builder[AnnotationKindIndex.COLUMN.getIdx()] = null;
        }
        return this;
    }

    public List<AnnotationSpecBuilderWrapper> addAndGetBuilder(Constraint constraint) {
        List<AnnotationSpecBuilderWrapper> result = new ArrayList<>();
        AnnotationKindIndex kind = constraint.getConstraintType().getAnno();
        if (kind != null) {
            if (this.builder[kind.getIdx()] == null) {
                this.builder[kind.getIdx()] = AnnotationSpec.builder(
                        ClassName.get(kind == AnnotationKindIndex.CHECK ? DEPENDENCY_HIBERNATE : DEPENDENCY_JPA, kind.annoName)
                );
            }

            result.add(new AnnotationSpecBuilderWrapper(kind, this.builder[kind.getIdx()]));

            if (kind == AnnotationKindIndex.COLUMN || kind == AnnotationKindIndex.JOIN_COLUMN) {
                AnnotationKindIndex internal = kind == AnnotationKindIndex.COLUMN ?
                        AnnotationKindIndex.JOIN_COLUMN :
                        AnnotationKindIndex.COLUMN;

                this.builder[internal.getIdx()] = AnnotationSpec.builder(
                        ClassName.get(DEPENDENCY_JPA, internal.annoName)
                );
                result.add(new AnnotationSpecBuilderWrapper(internal, this.builder[internal.getIdx()]));
            }
        }
        if (constraint.getDataLenVarchar() != null || "n".equalsIgnoreCase(constraint.getNullable())) {
            Constraint virtualConstraints = new Constraint();
            virtualConstraints.setConstraintType(ConstraintsType.COLUMN);
            result.addAll(addAndGetBuilder(virtualConstraints));
        }

        return result;
    }

    public int getParent() {
        return parent;
    }


    public List<AnnotationSpec> getBuilderWithBuild() {
        List<AnnotationSpec> result = new ArrayList<>();
        for (int i = 0; i < builder.length; i++) {
            if (builder[i] == null) continue;
            result.add(builder[i].build());
            builder[i] = null;
        }
        return result;
    }


    public enum AnnotationKindIndex {
        COLUMN(0, "Column"), // nullable, length, unique,
        JOIN_COLUMN(1, "JoinColumn"), //nullable, unique
        MANY_TO_ONE(2, "ManyToOne"), // fetch, targetEntity
        CHECK(3, "Check"); // constraints

        private final int idx;
        private final String annoName;

        AnnotationKindIndex(int idx, String annoName) {
            this.idx = idx;
            this.annoName = annoName;
        }

        public int getIdx() {
            return idx;
        }
    }
    public boolean[] getPropIsVisited() {
        return propIsVisited;
    }
}
