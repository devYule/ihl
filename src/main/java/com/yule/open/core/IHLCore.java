package com.yule.open.core;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeSpec;
import com.yule.open.annotations.EnableEntityGenerator;
import com.yule.open.database.DatabaseAdapter;
import com.yule.open.database.DefaultDatabaseAdapter;
import com.yule.open.database.enums.DatabaseKind;
import com.yule.open.entity.DefaultEntityAdapter;
import com.yule.open.entity.EntityAdapter;
import com.yule.open.info.Table;
import com.yule.open.mediator.DefaultEntityTableMediator;
import com.yule.open.mediator.EntityTableMediator;
import com.yule.open.properties.Environment;
import com.yule.open.properties.EnvironmentProperties;
import com.yule.open.utils.BatchSourceGenerator;
import com.yule.open.utils.JavaPoetBatchSourceGenerator;
import com.yule.open.utils.Logger;
import com.yule.open.utils.NameGenerator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.sql.SQLException;
import java.util.*;

import static com.yule.open.utils.Validator.*;
import static com.yule.open.utils.Logger.*;
import static com.yule.open.properties.ErrorMessageProperties.*;
import static com.yule.open.properties.ProcessingMessageProperties.*;
import static com.yule.open.properties.EnvironmentProperties.*;

@SupportedOptions({
        "entity.path",
        "need.setter",
        "need.getter",
        "need.noArgs",
        "need.allArgs",
        "need.builder",
        "db.password",
        "db.username",
        "db.url",
        "db.schema"
})
@AutoService(Processor.class)
@SupportedAnnotationTypes("")
public class IHLCore extends AbstractProcessor {

    public static NameGenerator nameGenerator;
    public final static String hibernateDependencyPath;
    public final static NameGenerator embeddedEntityNameGenerator;

    static {
        hibernateDependencyPath = "org.hibernate.annotations";
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

        Environment.put(Required.DB_URL, url);
        Environment.put(Required.DB_USERNAME, name);
        Environment.put(Required.DB_PASSWORD, pw);
        Environment.put(Required.ENTITY_PATH, path);
        Environment.put(Required.PROJECT_ROOT, projectRoot);

        Environment.put(Required.ENTITY_NAME_PREFIX, entityNamePrefix);
        Environment.put(Required.ENTITY_NAME_SUFFIX, entityNameSuffix);

        nameGenerator = new NameGenerator(entityNamePrefix, entityNameSuffix);
        info(VALIDATE_REQUIRED_ENVIRONMENTS.getSuccess());

        info("Connect to database...");
        DatabaseAdapter databaseAdapter = null;
        try {
            try {
                databaseAdapter = new DefaultDatabaseAdapter(url, name, pw, processingEnv.getFiler());
            } catch (SQLException | ClassNotFoundException e) {
                error(CAN_NOT_FIND_DATABASE_DRIVER.getMessage(), e);
            }
            assert (databaseAdapter != null);

            info("Check your database name...");
            DatabaseKind databaseKind = databaseAdapter.getDatabaseKind();
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
                Table[] entityTables = databaseAdapter.analyseAllTablesAndBatchSources(dbname, toEntityTables);
                info("All tables, columns and constraints READY...");

                info("Generate Source...");
                BatchSourceGenerator<TypeSpec, Table> generator = new JavaPoetBatchSourceGenerator<>(entityAdapter, path, processingEnv);
                generator.generate(entityTables);
            }
            info("Done...");
            info("Success to make Entity Sources!!!");

            return true;

        } finally {
            if (databaseAdapter != null) {
                try {
                    databaseAdapter.getConn().close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
