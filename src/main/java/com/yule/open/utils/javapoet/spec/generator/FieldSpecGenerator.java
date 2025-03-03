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
import com.yule.open.utils.javapoet.annotations.properties.AnnotationProperties;
import com.yule.open.utils.javapoet.annotations.properties.AnnotationName;
import com.yule.open.utils.javapoet.spec.JavaPoetSpecGenerateCommander;
import com.yule.open.utils.javapoet.spec.wrapper.impl.FieldSpecWrapper;
import com.yule.open.utils.javapoet.spec.wrapper.impl.TypeSpecWrapper;

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
            TypeSpecWrapper parentTypeSpec = ts[f.getParent()];
            TypeSpec.Builder b = parentTypeSpec.getBuilder();

            // 복합키일 경우,
            if (parentTypeSpec.getPkCnt() > 1) {
                JavaPoetSpecGenerateCommander.AdditionalTypeKind embeddable = JavaPoetSpecGenerateCommander.AdditionalTypeKind.EMBEDDABLE;
                int additionalKindIdx = embeddable.getIdx();

                // 복합키일 경우,
                // Embeddable 클래스 생성, embeddedId 어노테이션을 프로퍼티에 추가.
                // 1회만 수행.
                if (!parentTypeSpec.isAddedEmbeddableAnnotation()) {
                    // 추가타입 Embeddable 생성.
                    parentTypeSpec.addAdditionalBuilder(embeddable);
                    // Embeddable, Serializable 어노테이션 추가.
                    parentTypeSpec.addAnnotationAtAdditionalType(embeddable);

                    // embeddedId 필드 추가.
                    // add embeddedId at field
                    // embeddable 을 '사용할' 클래스의 embeddable 을 사용하는 필드에 EmbeddedId 어노테이션 추가.
                    FieldSpec embeddedId =
                            FieldSpec.builder(TypeConverter.convert(IHLProcessor.nameGenerator.generateEntityName(parentTypeSpec.getAdditionalTypeName(embeddable))),
                                            "embeddedId", Modifier.PRIVATE)
                                    .addAnnotation(ClassName.get(Environment.get(EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY),
                                            "EmbeddedId"))
                                    .build();

                    // 만약 f가,
                    // @Id 면 필드를 embeddable 클래스에 추가해야함.
                    // 그 외는 그냥 원래 클래스에 추가함. (embeddable 필드만 있으면 됨.)

                    parentTypeSpec.getBuilder().addField(embeddedId);
                }

                // 해당 클래스가 복합키 이면서, 현재 순회중인 필드가 PK 필드라면,
                if (f.getPkCnt() == 1) {

                    // 해당 클래스가 복합키 이면서, 현재 순회중인 필드가 PK 이고, 외래키 라면,
                    // pk 프로퍼티를 embeddable 클래스 에 추가해야함.
                    // 그 외는 그냥 원본 클래스에 추가해야함.
                    if (f.isFK()) {
                        // 다만, 외래키일 경우, 즉 복합키 이면서 외래키일 경우에는 타입이 애초에 객체로 들어옴.
                        // 따라서 객체는 원본에 그대로 저장시키면 되지만,
                        // pk 를 embeddable 에 추가해야함.

                        // 즉, f 는 원래 필드에 추가 (타입은 ManyToOne 과 동일, 다만 MapsId 추가적으로 부착.) 하지만,
                        // 복합키 + 외래키는 Embeddable 클래스에도 Primitive 타입으로 id 프로퍼티를 추가해 주고,
                        // MapsId 에는 해당 프로퍼티의 값을 기재해 주어야 함.

                        TypeSpec.Builder embeddableTypeBuilder = parentTypeSpec.getAdditionalBuilder(embeddable);
                        embeddableTypeBuilder.addField(Long.class, f.getFieldNm(), Modifier.PRIVATE);

                        // 복합키 + 외래키면 @MapsId 도 달아줘야 함.
                        AnnotationProperties.MapsId mapsIdProps = AnnotationProperties.MapsId.VALUE;
                        AnnotationSpec mapsId = AnnotationSpec.builder(
                                ClassName.get(Environment.get(EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY),
                                        AnnotationName.MapsId.name())
                        ).addMember(mapsIdProps.getName(), mapsIdProps.getFormat(), f.getFieldNm()).build();

                        // 현재 속성에 mapsId 추가.
                        f.getBuilder().addAnnotation(mapsId);
                    } else {
                        // 해당 클래스가 복합키 이지만, 현재 순회중인 필드가 PK 이지만, 외래키는 아닐 경우,
                        // 외래키가 아닌 일반 PK 라면 embeddable 에 필드를 추가하면 됨.
                        b = parentTypeSpec.getAdditionalBuilder()[additionalKindIdx];
                    }
                }


                // 추가적인 additional 이 필요하면 여기 else if 로 추가.
            } else {
                // 해당 클래스가 복합키가 아닐 경우,
                // @Id 달기
                AnnotationProperties.GeneratedValue strategyProps = AnnotationProperties.GeneratedValue.STRATEGY;

                // 해당 클래스가 복합키가 아니지만, 현재 순회중인 필드는 PK 일 경우,
                if (f.getPkCnt() == 1) {
                    // @Id 와 @GeneratedValue 는 여느 어노테이션과 달리, 필드순회시 부착함.
                    // ㄴ> @Embeddable 을 부착해야 하는 경우, 타입까지 제어해야 하기 때문에,
                    // Annotation Generator 레벨에서 다루기보다는 Type Generator 에서 직접 Annotation 과 타입을 모두 생성.
                    f.getBuilder().addAnnotation(ClassName.get(Environment.get(EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY),
                            AnnotationName.Id.name()));

                    // @GeneratedValue
                    AnnotationSpec generatedValueAnno = AnnotationSpec.builder(
                            ClassName.get(Environment.get(EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY), AnnotationName.GeneratedValue.name())
                    ).addMember(
                            strategyProps.getName(), strategyProps.getFormat(), ClassName.get(Environment.get(EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY), "GenerationType")
                    ).build();
                    f.getBuilder().addAnnotation(generatedValueAnno);
                }

            }
            // pk > 1 이면 추가된 embedded 타입에, 아니면 기본 타입에 필드 추가.
            b.addField(f.getBuilder().build());
        });

    }
}
