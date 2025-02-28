package com.yule.open.utils;

import com.squareup.javapoet.*;
import com.yule.open.entity.EntityAdapter;
import com.yule.open.info.Column;
import com.yule.open.info.Constraint;
import com.yule.open.info.Table;
import com.yule.open.info.enums.ConstraintsType;
import com.yule.open.properties.Environment;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.yule.open.core.IHLCore.*;
import static com.yule.open.info.enums.ConstraintsType.*;
import static com.yule.open.properties.EnvironmentProperties.Required.PROJECT_ROOT;
import static com.yule.open.utils.JDatabaseConverter.typeConverter;
import static com.yule.open.utils.Logger.error;

public class JavaPoetBatchSourceGenerator<T extends TypeSpec, D extends Table> extends BatchSourceGenerator<T, D> {

    private final String jpaDependencyPath;
    private final String entityPath;
    private final List<ClassName> lombokAnnotationForAdd;

    public JavaPoetBatchSourceGenerator(EntityAdapter entityAdapter, String entityPath, ProcessingEnvironment processingEnv) {
        this.jpaDependencyPath = entityAdapter.getJPADependencyPath();
        this.entityPath = entityPath;

        /* Lombok */
        String lombokDependencyPath = "lombok";
        String getter = processingEnv.getOptions().get("need.getter");
        String setter = processingEnv.getOptions().get("need.setter");
        String noArgs = processingEnv.getOptions().get("need.noArgs");
        String allArgs = processingEnv.getOptions().get("need.allArgs");
        String blder = processingEnv.getOptions().get("need.builder");

        this.lombokAnnotationForAdd = new ArrayList<>();
        if (getter != null && !getter.isEmpty()) lombokAnnotationForAdd.add(ClassName.get(lombokDependencyPath, "Getter"));
        if (setter != null && !setter.isEmpty()) lombokAnnotationForAdd.add(ClassName.get(lombokDependencyPath, "Setter"));
        if (noArgs != null && !noArgs.isEmpty()) lombokAnnotationForAdd.add(ClassName.get(lombokDependencyPath, "NoArgsConstructor"));
        if (allArgs != null && !allArgs.isEmpty()) lombokAnnotationForAdd.add(ClassName.get(lombokDependencyPath, "AllArgsConstructor"));
        if (blder != null && !blder.isEmpty()) lombokAnnotationForAdd.add(ClassName.get(lombokDependencyPath, "Builder"));
    }

    @Override
    protected List<T> batch(Table[] tables) {
        List<TypeSpec> result = new ArrayList<>();

        for (Table table : tables) {
            result.add(genSource(table));
        }

        return (List<T>) result;

    }
//
//    @Override
//    public int generate(Table[] tables) {
//        batchTables = batch(tables);
//        int cnt = 0;
//
//        for (TypeSpec spec : batchTables) {
//            try {
//                JavaFile.builder(entityPath, spec)
//                        .build()
//                        .writeTo(filer);
//                cnt++;
//            } catch (IOException e) {
//                warn(WarningMessageProperties.CAN_NOT_GENERATE_SOURCE.getMessage(spec.name));
//                warn(e.getMessage());
//            }
//        }
//
//        return cnt;
//    }


    @Override
    public int generate(D[] tables) {
        batchTables = batch(tables);
        String projectRoot = Environment.get(PROJECT_ROOT);
        System.out.println("projectRoot = " + projectRoot);
        String outputDir = Paths.get(projectRoot, "src/main/java").toString();
        System.out.println("outputDir = " + outputDir);
        File dir = new File(outputDir);
        Path path = dir.toPath();
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                error("Generate directories error!");
                throw new RuntimeException(e);
            }
        }
        for (TypeSpec batchTable : batchTables) {
            try {
                JavaFile.builder(entityPath, batchTable)
                        .build()
                        .writeTo(new File(outputDir));
            } catch (IOException e) {
                error("Write file error!");
                throw new RuntimeException(e);
            }
        }
        return batchTables.size();
    }

    private TypeSpec genSource(Table table) {

//        String tbNm = camelFromSnake(table.getTbNm().toLowerCase(), true) + "Entity";
        String tbNm = nameGenerator.generateEntityName(table.getTbNm().toLowerCase());
        TypeSpec.Builder builder = TypeSpec.classBuilder(tbNm)
                .addModifiers(Modifier.PUBLIC);
        FieldSpecWrapper fieldSpecWrapper = genSource(table.getColumns(), table.getTbNm());
        builder.addFields(fieldSpecWrapper.fieldSpecs);

        if (fieldSpecWrapper.isEmbeddable) {
            builder.addAnnotation(ClassName.get(jpaDependencyPath, "Embeddable"));
            builder.addSuperinterface(ClassName.get("java.io", "Serializable"));
        } else builder.addAnnotation(ClassName.get(jpaDependencyPath, "Entity"));


        for (ClassName className : lombokAnnotationForAdd) {
            builder.addAnnotation(className);
        }
        return builder.build();
    }

    private FieldSpecWrapper genSource(List<Column> columns, String tableName) {
        Map<String, FieldSpec.Builder> resultMap = new LinkedHashMap<>();
        boolean isEmbeddable = false;
        boolean alreadyAddEmbeddedIdField = false;
        boolean isFK = false;
        for (Column column : columns) {
            if (column.getConstraint().getConstraintType() == FOREIGN_KEY) {
                isFK = true;
                break;
            }
        }
        for (Column column : columns) {
            if (!isEmbeddable && column.getConstraint().getConstraintType() == EMBEDDABLE) {
                isEmbeddable = true;
            }
            if (column.getConstraint().getConstraintType() == EMBEDDED_ID) {
                if (alreadyAddEmbeddedIdField) continue;

                FieldSpec.Builder embeddedIdBuilder = FieldSpec.builder(ClassName.get(entityPath,
                                        embeddedEntityNameGenerator.generateEntityName(tableName.toLowerCase())),
                                "embeddedId")
                        .addModifiers(Modifier.PRIVATE)
                        .addAnnotations(genSource(column.getConstraint(), true, isFK));

                resultMap.put(column.getColNm(), embeddedIdBuilder);
                alreadyAddEmbeddedIdField = true;
            }

//            if (!columnNames.isEmpty() && columnNames.contains(column.getColNm())) continue;

            if (resultMap.get(column.getColNm()) != null) {
                // 어노테이션만 추가.
                FieldSpec.Builder fieldSpec = resultMap.get(column.getColNm());
                fieldSpec.addAnnotations(genSource(column.getConstraint(), false, isFK));
            } else {
                FieldSpec.Builder builder = null;
                String entityName = nameGenerator.generateEntityName(column.getColNm());
                if (column.getConstraint().getRefEntity() == null) {
                    builder = FieldSpec.builder(typeConverter(column.getDataType(), column.getDataLenNum()), entityName);
                } else {
                    builder = FieldSpec.builder(typeConverter(column.getConstraint().getRefEntity()), entityName);
                }
                builder.addModifiers(Modifier.PRIVATE)
                        .addAnnotations(genSource(column.getConstraint(), true, isFK));
                resultMap.put(column.getColNm(), builder);
            }

        }
        return new FieldSpecWrapper(resultMap.values().stream().map(FieldSpec.Builder::build).collect(Collectors.toList()), isEmbeddable);
    }

    private List<AnnotationSpec> genSource(Constraint constraint, boolean isFirst, boolean isFK) {
        List<AnnotationSpec> result = new ArrayList<>();
        String nullable = constraint.getNullable(); // N -> not null
        Double dataLenVarchar = constraint.getDataLenVarchar();
        ConstraintsType constraintType = constraint.getConstraintType(); // fk or pk or unique or nothing

        if (isFirst) {
            boolean hasAnyOption = false;
            AnnotationSpec.Builder columnAnno = isFK ?
                    AnnotationSpec.builder(ClassName.get(jpaDependencyPath, "JoinColumn")) :
                    AnnotationSpec.builder(ClassName.get(jpaDependencyPath, "Column"));

            if ("n".equalsIgnoreCase(nullable)) {
                // @Column(nullable = false) (default == true)
                columnAnno.addMember("nullable", "$L", false);
                hasAnyOption = true;
            }
            if (!isFK) {

                // if not null, type = varchar or char and need to set it by annotation
                if (dataLenVarchar != null) {
                    // @Column(length = x) (int only, max 255)
                    columnAnno.addMember("length", "$L", (int) (double) dataLenVarchar);
                    hasAnyOption = true;
                }
                if (UNIQUE == constraintType) {
                    // @Column(unique = true)
                    columnAnno.addMember("unique", "$L", true);
                    hasAnyOption = true;
                }
            }
            if (hasAnyOption) result.add(columnAnno.build());
        }
        if (constraintType != NONE) {
            if (FOREIGN_KEY == constraintType) {
                String refTb = constraint.getRefTb();
                String refCol = constraint.getRefCol();
            /*
        	    Since it is impossible to determine whether it is @OneToOne or @ManyToOne,
        	    this processor always uses @ManyToOne.
             */
                // @ManyToOne(fetch = FetchType.LAZY, targetEntity = SomeClass.class)
                AnnotationSpec.Builder manyToOneAnno = AnnotationSpec.builder(ClassName.get(jpaDependencyPath, "ManyToOne"));
                manyToOneAnno.addMember("fetch", "$T.LAZY", ClassName.get(jpaDependencyPath, "FetchType"));

//                String baseEntityName = camelFromSnake(refTb);
                System.out.println("log here");
                System.out.println("refTb = " + refTb);
                ClassName refClassName = ClassName.get(entityPath, nameGenerator.generateEntityName(refTb));
//                for (String suffix : possibleEntitySuffix) {
//                    String expectClassName = baseEntityName + suffix;
//                    if (processingEnv.getElementUtils().getTypeElement(expectClassName) == null) continue;
//                    refClassName = ClassName.get(entityPath, expectClassName);
//                }

                manyToOneAnno.addMember("targetEntity", "$T.class", refClassName);
                AnnotationSpec anno = manyToOneAnno.build();
                result.add(anno);
            }
            if (PRIMARY_KEY == constraintType) {
                result.add(AnnotationSpec.builder(ClassName.get(jpaDependencyPath, "Id")).build());
                // pk annotation (@Id)
            }

            if (EMBEDDED_ID == constraintType && isFirst) {
                result.add(AnnotationSpec.builder(ClassName.get(jpaDependencyPath, "EmbeddedId")).build());
            }
            if (CHECK == constraintType) {
                result.add(AnnotationSpec.builder(ClassName.get(hibernateDependencyPath, "Check"))
                        .addMember("constraints", "$S", constraint.getCheckString().replaceAll("\"", "")).build());
            }
        }

        return result;
    }

    private static class FieldSpecWrapper {
        private List<FieldSpec> fieldSpecs;
        private boolean isEmbeddable;

        public FieldSpecWrapper(List<FieldSpec> fieldSpecs, boolean isEmbeddable) {
            this.fieldSpecs = fieldSpecs;
            this.isEmbeddable = isEmbeddable;
        }
    }
}
