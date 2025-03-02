package com.yule.open.utils.javapoet;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.yule.open.database.info.Constraint;
import com.yule.open.database.info.enums.ConstraintsType;
import com.yule.open.properties.Environment;
import com.yule.open.properties.EnvironmentProperties;
import com.yule.open.utils.javapoet.wrapper.AnnotationSpecWrapper;
import com.yule.open.utils.javapoet.wrapper.FieldSpecWrapper;
import com.yule.open.utils.javapoet.wrapper.TypeSpecWrapper;

import java.util.Arrays;
import java.util.List;

import static com.yule.open.properties.EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY;
import static com.yule.open.utils.javapoet.wrapper.AnnotationSpecWrapper.AnnotationKind.*;


public class AnnotationSpecGenerator {
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

        System.out.println("------------");
        System.out.println("ts.getTbNm() = " + ts.getTbNm());
        System.out.println("fs.getFieldNm() = " + fs.getFieldNm());
        System.out.println("constraint = " + constraint.getConstraintType().getToken());
        System.out.println("------------");

        if (as[myIdx] == null) {
            as[myIdx] = new AnnotationSpecWrapper(parentIdx);
        }

        List<AnnotationSpec.Builder> builder = generateSpec(constraint.getConstraintType().getAnno(), as[myIdx]);

        for (AnnotationSpec.Builder b : builder) {
            addMember(b, constraint.getConstraintType().getAnno(), constraint);
        }

        if (constraint.getConstraintType() == ConstraintsType.PRIMARY_KEY) {
            fs.addPKCnt();
            ts.addPKCnt();
        }
        if (constraint.getConstraintType() == ConstraintsType.FOREIGN_KEY) {
            fs.setFK(true);
        }

    }

    private List<AnnotationSpec.Builder> generateSpec(AnnotationSpecWrapper.AnnotationKind kind, AnnotationSpecWrapper spec) {
        return spec.addAndGetBuilder(kind);
    }

    private void addMember(AnnotationSpec.Builder builder, AnnotationSpecWrapper.AnnotationKind anno, Constraint constraint) {
        if (anno == COLUMN || anno == JOIN_COLUMN) {
            if ("n".equalsIgnoreCase(constraint.getNullable())) {
                builder.addMember("nullable", "$L", false);
            }
            if (constraint.getDataLenVarchar() != null) {
                builder.addMember("length", "$L", (int) (double) constraint.getDataLenVarchar());
            }
            if (constraint.getConstraintType() == ConstraintsType.UNIQUE) {
                builder.addMember("unique", "$L", true);
            }
        }
        if (anno == MANY_TO_ONE) {
            builder.addMember("fetch", "$T.LAZY", ClassName.get(DEPENDENCY_JPA, "FetchType"));
            builder.addMember("targetEntity", "$T.class",
                    ClassName.get(Environment.get(EnvironmentProperties.Required.ENTITY_PATH), constraint.getRefEntity()));
        }
        if (anno == CHECK) {
            builder.addMember("constraints", "$S", constraint.getCheckString().replaceAll("\"", "")).build();
        }
    }


    public void build(FieldSpecWrapper[] fs) {
        Arrays.stream(this.as).forEach(a -> {
            if (a == null) return;
            a.decideColumn();
            fs[a.getParent()].getBuilder().addAnnotations(a.getBuilderWithBuild()).build();
        });
    }
}
