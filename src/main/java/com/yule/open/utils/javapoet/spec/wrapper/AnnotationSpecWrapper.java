package com.yule.open.utils.javapoet.spec.wrapper;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.yule.open.properties.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yule.open.properties.enums.EnvironmentProperties.AnnotationProcessor.HIBERNATE_DEPENDENCY;
import static com.yule.open.properties.enums.EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY;

public class AnnotationSpecWrapper {

    private final int parent;
    private final AnnotationSpec.Builder[] builder;
    private final String DEPENDENCY_JPA;
    private final String DEPENDENCY_HIBERNATE;


    public AnnotationSpecWrapper(int parent) {
        int annoCnt = AnnotationKind.values().length;
        this.parent = parent;
        this.builder = new AnnotationSpec.Builder[annoCnt];
        this.DEPENDENCY_JPA = Environment.get(JPA_DEPENDENCY);
        this.DEPENDENCY_HIBERNATE = Environment.get(HIBERNATE_DEPENDENCY);

    }

    public void decideColumn() {
        if (this.builder[AnnotationKind.MANY_TO_ONE.getIdx()] == null) {
            this.builder[AnnotationKind.JOIN_COLUMN.getIdx()] = null;
        } else {
            this.builder[AnnotationKind.COLUMN.getIdx()] = null;
        }
    }

    public List<AnnotationSpec.Builder> addAndGetBuilder(AnnotationKind kind) {
        if (kind == null) return new ArrayList<>();
        if (this.builder[kind.getIdx()] == null) {
            this.builder[kind.getIdx()] = AnnotationSpec.builder(
                    ClassName.get(kind == AnnotationKind.CHECK ? DEPENDENCY_HIBERNATE : DEPENDENCY_JPA, kind.annoName)
            );
        }

        List<AnnotationSpec.Builder> result = new ArrayList<>();

        result.add(this.builder[kind.getIdx()]);
        if (kind == AnnotationKind.COLUMN || kind == AnnotationKind.JOIN_COLUMN) {
            AnnotationKind internal = kind == AnnotationKind.COLUMN ? AnnotationKind.JOIN_COLUMN : AnnotationKind.COLUMN;
            this.builder[internal.getIdx()] = AnnotationSpec.builder(
                    ClassName.get(DEPENDENCY_JPA, internal.annoName)
            );
            result.add(this.builder[internal.getIdx()]);
        }
        return result;
    }


    public int getParent() {
        return parent;
    }


    public List<AnnotationSpec> getBuilderWithBuild() {
        return Arrays.stream(this.builder).filter(Objects::nonNull).map(AnnotationSpec.Builder::build).collect(Collectors.toList());
    }

    public enum AnnotationKind {
        COLUMN(0, "Column"), // nullable, length, unique,
        JOIN_COLUMN(1, "JoinColumn"), //
        MANY_TO_ONE(2, "ManyToOne"), // fetch, targetEntity
        CHECK(3, "Check"); // constraints

        private final int idx;
        private final String annoName;

        AnnotationKind(int idx, String annoName) {
            this.idx = idx;
            this.annoName = annoName;
        }

        public int getIdx() {
            return idx;
        }
    }
}
