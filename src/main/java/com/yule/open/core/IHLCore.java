package com.yule.open.core;

import com.google.auto.service.AutoService;
import com.yule.open.annotations.EnableEntityGenerator;
import com.yule.open.database.ConnectionFactory;
import com.yule.open.database.DatabaseAdapter;

import com.yule.open.database.data.AnalyseResult;
import com.yule.open.database.enums.DatabaseKind;
import com.yule.open.database.graph.NodeDatabaseAdapter;
import com.yule.open.entity.DefaultEntityAdapter;
import com.yule.open.entity.EntityAdapter;
import com.yule.open.mediator.DefaultEntityTableMediator;
import com.yule.open.mediator.EntityTableMediator;
import com.yule.open.properties.Environment;
import com.yule.open.properties.EnvironmentProperties;

import com.yule.open.utils.Logger;
import com.yule.open.utils.NameGenerator;
import com.yule.open.utils.JavapoetNodeBatchSourceGenerator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.*;

import static com.yule.open.database.ConnectionFactory.getDatabaseKind;
import static com.yule.open.utils.Validator.*;
import static com.yule.open.utils.Logger.*;
import static com.yule.open.properties.ErrorMessageProperties.*;
import static com.yule.open.properties.ProcessingMessageProperties.*;
import static com.yule.open.properties.EnvironmentProperties.Required;
import static com.yule.open.properties.EnvironmentProperties.Optional;
import static com.yule.open.properties.EnvironmentProperties.AnnotationProcessor;

@AutoService(Processor.class)
@SupportedAnnotationTypes("")
public class IHLCore extends AbstractProcessor {

    public static NameGenerator nameGenerator;
    public final static NameGenerator embeddedEntityNameGenerator;

    static {
        embeddedEntityNameGenerator = new NameGenerator("Embedded", "Id");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(EnableEntityGenerator.class.getName());
        return set;
    }

    @Override
    public Set<String> getSupportedOptions() {
        Set<String> set = new HashSet<>();
        set.add("entity.path");
        set.add("need.setter");
        set.add("db.password");
        set.add("need.noArgs");
        set.add("need.allArgs");
        set.add("need.getter");
        set.add("need.builder");
        set.add("db.username");
        set.add("db.url");
        set.add("db.schema");
        set.add("project.root");
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Logger.setMessager(processingEnv.getMessager());

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(EnableEntityGenerator.class);

        if (elements == null || elements.isEmpty()) return true;

        EntityAdapter entityAdapter = new DefaultEntityAdapter(processingEnv.getElementUtils());

        info(FIND_ANNOTATION.getProc());
        if (isOver(1, elements.size())) error(ANNOTATION_DUPLICATED.getMessage());
        info(FIND_ANNOTATION.getSuccess());

        info(FIND_JPA_DEPENDENCY.getProc());
        if (isEquals(-1, entityAdapter.validateJPADependency())) error(JPA_NOT_FOUND.getMessage());
        info(FIND_JPA_DEPENDENCY.getSuccess());

        info(VALIDATE_REQUIRED_ENVIRONMENTS.getProc());
        String url = processingEnv.getOptions().get(Required.DB_URL.getEnv());
        String name = processingEnv.getOptions().get(Required.DB_USERNAME.getEnv());
        String pw = processingEnv.getOptions().get(Required.DB_PASSWORD.getEnv());
        String path = processingEnv.getOptions().get(Required.ENTITY_PATH.getEnv());
        String projectRoot = processingEnv.getOptions().get(Required.PROJECT_ROOT.getEnv());
        if (anyNull(url, name, pw, path, projectRoot)) error(REQUIRED_ENVIRONMENT_NOT_PROVIDED.getMessage());
        String entityNamePrefix = processingEnv.getOptions().get(Required.ENTITY_NAME_PREFIX.getEnv());
        String entityNameSuffix = processingEnv.getOptions().get(Required.ENTITY_NAME_SUFFIX.getEnv());

        String getter = processingEnv.getOptions().get("need.getter");
        String setter = processingEnv.getOptions().get("need.setter");
        String noArgs = processingEnv.getOptions().get("need.noArgs");
        String allArgs = processingEnv.getOptions().get("need.allArgs");
        String builder = processingEnv.getOptions().get("need.builder");

        Environment.put(Required.DB_URL, url);
        Environment.put(Required.DB_USERNAME, name);
        Environment.put(Required.DB_PASSWORD, pw);
        Environment.put(Required.ENTITY_PATH, path);
        Environment.put(Required.PROJECT_ROOT, projectRoot);

        Environment.put(Required.ENTITY_NAME_PREFIX, entityNamePrefix);
        Environment.put(Required.ENTITY_NAME_SUFFIX, entityNameSuffix);

        Environment.put(Optional.GETTER, getter);
        Environment.put(Optional.SETTER, setter);
        Environment.put(Optional.NOARGS, noArgs);
        Environment.put(Optional.ALLARGS, allArgs);
        Environment.put(Optional.BUILDER, builder);

        Environment.put(AnnotationProcessor.JPA_DEPENDENCY, entityAdapter.getJPADependencyPath());
        Environment.put(AnnotationProcessor.HIBERNATE_DEPENDENCY, "org.hibernate.annotations");
        Environment.put(AnnotationProcessor.JAVA_IO, "java.io");


        nameGenerator = new NameGenerator(entityNamePrefix, entityNameSuffix);
        info(VALIDATE_REQUIRED_ENVIRONMENTS.getSuccess());

        info("Connect to database...");
        try {
            DatabaseAdapter databaseAdapter = new NodeDatabaseAdapter();

            info("Check your database name...");
            DatabaseKind databaseKind = getDatabaseKind();
            String dbname = null;
            if (databaseKind == DatabaseKind.ORACLE) {
                info("Your database kind is ORACLE...");
                dbname = processingEnv.getOptions().get(EnvironmentProperties.Required.ORACLE_SCHEMA.getEnv());
            } else if (databaseKind == DatabaseKind.MYSQL || databaseKind == DatabaseKind.MARIADB) {
                info("Your database kind is MYSQL(MARIADB)...");
                dbname = processingEnv.getOptions().get(EnvironmentProperties.Required.MY_SQL_AND_MARIA_DB.getEnv());
            }
            if (isNull(dbname)) error(SCHEMA_OR_DATABASE_NAME_IS_NOT_PROVIDED.getMessage());
            info("Database is found...");

            List<String> tables = databaseAdapter.findTables(dbname);
            info("Connection Success...");
            collectionBatchInfo("elements: ", tables);


            info("Find exists Entities...");
            Set<? extends Element> allElements = roundEnv.getRootElements();
            int cnt = 0;
            for (Element el : allElements) {
                if (el.getKind() == ElementKind.CLASS && entityAdapter.hasEntityAnnotation(el)) cnt++;
            }
            info(cnt + " classes has @Entity...");

            info("Comparing Table and Entity class...");
            EntityTableMediator mediator = new DefaultEntityTableMediator(entityAdapter, databaseAdapter);
            List<String> toEntityTables = mediator.compareToTables();
            info("Find " + toEntityTables.size() + " Tables for Entity Mapping...");
            collectionBatchInfo("elements: ", toEntityTables);

            if (toEntityTables.isEmpty()) {
                info("You already have all the entities...");
            } else {

                info("Analyse your tables with column and constraints...");
//                Table[] entityTables = databaseAdapter.analyseAllTablesAndBatchSources(dbname, toEntityTables);
                AnalyseResult entityTables = databaseAdapter.analyseAllTablesAndBatchSources(dbname, toEntityTables);
                info("All tables, columns and constraints READY...");

                info("Generate Source...");
                new JavapoetNodeBatchSourceGenerator<>().generate(entityTables);
            }
            info("Done...");
            info("Success to make Entity Sources!!!");

            return true;

        } finally {
            ConnectionFactory.close();
        }
    }
}
