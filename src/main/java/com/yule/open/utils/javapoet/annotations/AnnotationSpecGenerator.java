package com.yule.open.utils.javapoet.annotations;

import com.squareup.javapoet.ClassName;
import com.yule.open.database.data.Constraint;
import com.yule.open.database.data.enums.ConstraintsType;
import com.yule.open.properties.Environment;
import com.yule.open.properties.enums.EnvironmentProperties;
import com.yule.open.utils.javapoet.annotations.properties.AnnotationProperties;
import com.yule.open.utils.javapoet.spec.wrapper.impl.AnnotationSpecWrapper;
import com.yule.open.utils.javapoet.spec.wrapper.impl.FieldSpecWrapper;
import com.yule.open.utils.javapoet.spec.wrapper.impl.TypeSpecWrapper;
import com.yule.open.utils.javapoet.spec.wrapper.wrapper.AnnotationSpecBuilderWrapper;

import java.util.Arrays;
import java.util.List;

import static com.yule.open.properties.enums.EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY;
import static com.yule.open.utils.javapoet.spec.wrapper.impl.AnnotationSpecWrapper.AnnotationKindIndex.*;

public class AnnotationSpecGenerator {
    // 자신이 아닌, 부모 (parentId) 의 인덱스에 저장.
    private final AnnotationSpecWrapper[] as;
    private final String DEPENDENCY_JPA;


    public AnnotationSpecGenerator(int wholeNodeSize) {

        this.as = new AnnotationSpecWrapper[wholeNodeSize];
        this.DEPENDENCY_JPA = Environment.get(JPA_DEPENDENCY);

    }

    public AnnotationSpecWrapper[] getAs() {
        return as;
    }

    public void generate(TypeSpecWrapper ts, FieldSpecWrapper fs, Constraint constraint, int myIdx, int parentIdx) {
        if (as[myIdx] == null) {
            as[myIdx] = new AnnotationSpecWrapper(parentIdx);
        }

        List<AnnotationSpecBuilderWrapper> builder = generateSpec(constraint, as[myIdx]);

        for (AnnotationSpecBuilderWrapper b : builder) {
            addMember(b.getBuilder(), b.getKind(), constraint);
        }

        if (constraint.getConstraintType() == ConstraintsType.PRIMARY_KEY) {
            as[myIdx].addPKCnt();
            fs.addPKCnt();
            ts.addPKCnt();
        }
        if (constraint.getConstraintType() == ConstraintsType.FOREIGN_KEY) {
            fs.setFK(true);
        }

    }

    private List<AnnotationSpecBuilderWrapper> generateSpec(Constraint constraint, AnnotationSpecWrapper spec) {
        return spec.addAndGetBuilder(constraint);
    }

    private void addMember(com.squareup.javapoet.AnnotationSpec.Builder builder, AnnotationSpecWrapper.AnnotationKindIndex anno, Constraint constraint) {
        if (anno == COLUMN || anno == JOIN_COLUMN) {
            if ("n".equalsIgnoreCase(constraint.getNullable())) {
                AnnotationProperties.Column nullableProps = AnnotationProperties.Column.NULLABLE;
                builder.addMember(nullableProps.getName(), nullableProps.getFormat(), false);
            }
            // @JoinColumn 에는 length 가 없음 (ManyToOne 이 동반되므로, length 는 참조받는 클래스에 존재함.
            if (anno == COLUMN && constraint.getDataLenVarchar() != null) {
                AnnotationProperties.Column lengthProps = AnnotationProperties.Column.LENGTH;
                builder.addMember(lengthProps.getName(), lengthProps.getFormat(), (int) (double) constraint.getDataLenVarchar());
            }
            if (constraint.getConstraintType() == ConstraintsType.UNIQUE) {
                AnnotationProperties.Column uniqueProps = AnnotationProperties.Column.UNIQUE;
                builder.addMember(uniqueProps.getName(), uniqueProps.getFormat(), true);
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
