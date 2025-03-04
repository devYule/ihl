package com.yule.open.javapoet.spec.generator;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.yule.open.database.data.Constraint;
import com.yule.open.database.data.enums.ConstraintsType;
import com.yule.open.properties.Environment;
import com.yule.open.properties.enums.EnvironmentProperties;
import com.yule.open.javapoet.properties.AnnotationProperties;
import com.yule.open.javapoet.spec.wrapper.impl.ConstraintsAnnotationSpecWrapper;
import com.yule.open.javapoet.spec.wrapper.impl.FieldSpecWrapper;
import com.yule.open.javapoet.spec.wrapper.impl.TypeSpecWrapper;
import com.yule.open.javapoet.spec.wrapper.wrapper.AnnotationSpecBuilderWrapper;

import java.util.Arrays;
import java.util.List;

import static com.yule.open.properties.enums.EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY;
import static com.yule.open.javapoet.spec.wrapper.impl.ConstraintsAnnotationSpecWrapper.AnnotationKindIndex.*;

public class ConstraintsAnnotationSpecGenerator {
    // 자신이 아닌, 부모 (parentId) 의 인덱스에 저장.
    // 하나의 제약조건으로 병합 (제약조건의 db 조회 결과가 1:N 임.)
    private final ConstraintsAnnotationSpecWrapper[] as;
    private final String DEPENDENCY_JPA;


    public ConstraintsAnnotationSpecGenerator(int wholeNodeSize) {

        this.as = new ConstraintsAnnotationSpecWrapper[wholeNodeSize];
        this.DEPENDENCY_JPA = Environment.get(JPA_DEPENDENCY);

    }

    public ConstraintsAnnotationSpecWrapper[] getAs() {
        return as;
    }

    public void generate(TypeSpecWrapper ts, FieldSpecWrapper fs, Constraint constraint, int parentIdx) {
        if (as[parentIdx] == null) {
            as[parentIdx] = new ConstraintsAnnotationSpecWrapper(parentIdx, new boolean[AnnotationProperties.Column.values().length]);
        }
        ConstraintsAnnotationSpecWrapper curObj = as[parentIdx];
        List<AnnotationSpecBuilderWrapper> builder = generateSpec(constraint, curObj);

        for (AnnotationSpecBuilderWrapper b : builder) {
            addMember(b.getBuilder(), b.getKind(), constraint, curObj.getPropIsVisited());
        }

        if (constraint.getConstraintType() == ConstraintsType.PRIMARY_KEY) {
            curObj.addPKCnt();
            fs.addPKCnt();
            ts.addPKCnt();
        }
        if (constraint.getConstraintType() == ConstraintsType.FOREIGN_KEY) {
            fs.setFK(true);
        }

    }

    private List<AnnotationSpecBuilderWrapper> generateSpec(Constraint constraint, ConstraintsAnnotationSpecWrapper spec) {
        return spec.addAndGetBuilder(constraint);
    }

    private void addMember(AnnotationSpec.Builder builder, ConstraintsAnnotationSpecWrapper.AnnotationKindIndex anno, Constraint constraint, boolean[] isVisitedProps) {
        if (anno == COLUMN || anno == JOIN_COLUMN) {
            if ("n".equalsIgnoreCase(constraint.getNullable())) {
                AnnotationProperties.Column nullableProps = AnnotationProperties.Column.NULLABLE;
                if (!isVisitedProps[nullableProps.ordinal()]) {
                    builder.addMember(nullableProps.getName(), nullableProps.getFormat(), false);
                    isVisitedProps[nullableProps.ordinal()] = true;
                }
            }
            // @JoinColumn 에는 length 가 없음 (ManyToOne 이 동반되므로, length 는 참조받는 클래스에 존재함.
            if (anno == COLUMN && constraint.getDataLenVarchar() != null) {
                AnnotationProperties.Column lengthProps = AnnotationProperties.Column.LENGTH;
                if (!isVisitedProps[lengthProps.ordinal()]) {
                    builder.addMember(lengthProps.getName(), lengthProps.getFormat(), (int) (double) constraint.getDataLenVarchar());
                    isVisitedProps[lengthProps.ordinal()] = true;
                }
            }
            if (constraint.getConstraintType() == ConstraintsType.UNIQUE) {
                AnnotationProperties.Column uniqueProps = AnnotationProperties.Column.UNIQUE;
                if (!isVisitedProps[uniqueProps.ordinal()]) {
                    builder.addMember(uniqueProps.getName(), uniqueProps.getFormat(), true);
                    isVisitedProps[uniqueProps.ordinal()] = true;
                }
            }
        }
        if (anno == MANY_TO_ONE) {
            AnnotationProperties.ManyToOne fetchProps = AnnotationProperties.ManyToOne.FETCH;
            AnnotationProperties.ManyToOne targetEntityProps = AnnotationProperties.ManyToOne.TARGET_ENTITY;
            builder.addMember(fetchProps.getName(), fetchProps.getFormat(), ClassName.get(DEPENDENCY_JPA, "FetchType"));
            builder.addMember(targetEntityProps.getName(), targetEntityProps.getFormat(),
                    ClassName.get(Environment.get(EnvironmentProperties.Required.ENTITY_PATH), constraint.getRefEntity()));
        }
        if (anno == CHECK) {
            AnnotationProperties.Check constraintsProps = AnnotationProperties.Check.CONSTRAINTS;
            builder.addMember(constraintsProps.getName(), constraintsProps.getFormat(),
                    constraint.getCheckString().replaceAll("\"", "")).build();
        }
    }


    public void build(FieldSpecWrapper[] fs) {
        Arrays.stream(this.as).forEach(a -> {
            if (a == null) return;
            fs[a.getParent()].getBuilder().addAnnotations(a.decideColumnAnnotation().getBuilderWithBuild());
        });
    }
}
