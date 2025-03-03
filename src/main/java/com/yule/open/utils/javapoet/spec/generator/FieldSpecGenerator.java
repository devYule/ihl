package com.yule.open.utils.javapoet.spec.generator;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import com.yule.open.core.IHLProcessor;
import com.yule.open.database.data.Column;
import com.yule.open.properties.Environment;
import com.yule.open.properties.conversion.TypeConverter;
import com.yule.open.properties.enums.EnvironmentProperties;
import com.yule.open.utils.javapoet.spec.JavaPoetSpecGenerateCommander;
import com.yule.open.utils.javapoet.spec.wrapper.FieldSpecWrapper;
import com.yule.open.utils.javapoet.spec.wrapper.TypeSpecWrapper;

import javax.lang.model.element.Modifier;
import java.util.Arrays;

import static com.yule.open.utils.StringUtils.camelFromSnake;

public class FieldSpecGenerator {
    private final FieldSpecWrapper[] fs;

    public FieldSpecGenerator(int wholeNodeSize) {
        this.fs = new FieldSpecWrapper[wholeNodeSize];
    }

    public void generate(TypeSpecWrapper ts, Column column, int myIdx, int parentIdx) {
        if (fs[myIdx] == null) {
            fs[myIdx] = new FieldSpecWrapper(parentIdx, generateSpec(column, column.getRefEntity()), camelFromSnake(column.getColNm()));
        }
        if (fs[myIdx].getPkCnt() == 1) ts.addPKCnt();



    }


    private FieldSpec.Builder generateSpec(Column column, String refEntity) {
        /* generate process */
        String fieldName = camelFromSnake(column.getColNm(), false);

        if (column.isFK()) {
            return FieldSpec.builder(
                    TypeConverter.convert(refEntity),
                    fieldName,
                    Modifier.PRIVATE
            );
        }

        return FieldSpec.builder(
                TypeConverter.convert(column.getDataType(), column.getDataLenNum()),
                fieldName,
                Modifier.PRIVATE
        );
    }

    public FieldSpecWrapper[] getFs() {
        return fs;
    }

    public void build(TypeSpecWrapper[] ts) {
        Arrays.stream(this.fs).forEach(f -> {
            if (f == null) return;
            TypeSpec.Builder b = null;

            if (ts[f.getParent()].getPkCnt() > 1) {
                JavaPoetSpecGenerateCommander.AdditionalTypeKind embeddable = JavaPoetSpecGenerateCommander.AdditionalTypeKind.EMBEDDABLE;
                int additionalKindIdx = embeddable.getIdx();
                TypeSpecWrapper defaultType = ts[f.getParent()];

                // Add embeddable at type
                // Add field tpye generated, <Embeddable>
                // 1회만 수행.
                if (!defaultType.isAddedEmbeddableAnnotation()) {

                    // 추가타입 Embeddable 생성.
                    defaultType.addAdditionalBuilder(embeddable);
                    // Embeddable, Serializable 어노테이션 추가.
                    defaultType.addAnnotationAtAdditionalType(embeddable);

                    // embeddedId 필드 추가.
                    // add embeddedId at field
                    // embeddable 을 '사용할' 클래스의 embeddable 을 사용하는 필드에 EmbeddedId 어노테이션 추가.
                    FieldSpec embeddedId =
                            FieldSpec.builder(TypeConverter.convert(IHLProcessor.nameGenerator.generateEntityName(defaultType.getAdditionalTypeName(embeddable))),
                                            "embeddedId", Modifier.PRIVATE)
                                    .addAnnotation(ClassName.get(Environment.get(EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY),
                                            "EmbeddedId"))
                                    .build();

                    // 만약 f가,
                    // @Id 면 embeddable 클래스에 추가해야함.
                    // 그 외는 그냥 추가함. (embeddable 만 있으면 됨.)

                    defaultType.getBuilder().addField(embeddedId);
                }

                b = ts[f.getParent()].getBuilder();

                if (f.getPkCnt() == 1) {
                    // pk 필드면 (@Id 면)
                    // pk 면 embeddable 에 추가해야함.
                    // 그 외는 그냥 원본 클래스에 추가해야함.

                    if (f.isFK()) {
                        // 다만, 외래키일 경우, 즉 복합키 이면서 외래키일 경우에는 타입이 애초에 객체로 들어옴.
                        // 따라서 객체는 원본에 그대로 저장시키면 되지만,
                        // pk 를 embeddable 에 추가해야함.

                        TypeSpec.Builder embeddableTypeBuilder = defaultType.getAdditionalBuilder(embeddable);
                        embeddableTypeBuilder.addField(Long.class, f.getFieldNm(), Modifier.PRIVATE);


                        // 복합키 + 외래키면 @MapsId 도 달아줘야 함.
                        AnnotationSpec mapsId = AnnotationSpec.builder(
                                ClassName.get(Environment.get(EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY), "MapsId")
                        ).addMember("value", "$S", f.getFieldNm()).build();
                        f.getBuilder().addAnnotation(mapsId);
                    } else {
                        // 외래키가 아닌 일반 PK 라면 embeddable 에 필드를 추가하면 됨.
                        b = defaultType.getAdditionalBuilder()[additionalKindIdx];
                    }
                }

                // 추가적인 additional 이 필요하면 여기 else if 로 추가.
            } else {
                // @Id 달기
                if (f.getPkCnt() == 1) {
                    f.getBuilder().addAnnotation(ClassName.get(Environment.get(EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY),
                            "Id"));
                    AnnotationSpec annoBuilder = AnnotationSpec.builder(ClassName.get(Environment.get(EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY),
                            "GeneratedValue")).addMember("strategy", "$T.AUTO",
                            ClassName.get(Environment.get(EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY),
                                    "GenerationType")).build();
                    f.getBuilder().addAnnotation(annoBuilder);
                }

                b = ts[f.getParent()].getBuilder();

            }
            // pk > 1 이면 추가된 embedded 타입에, 아니면 기본 타입에 필드 추가.
            b.addField(f.getBuilder().build());
        });

    }
}
