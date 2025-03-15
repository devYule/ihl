//package com.yule.open.core;
//
//import com.squareup.javapoet.TypeSpec;
//import com.yule.open.annotations.EnableEntityGenerator;
//import com.yule.open.database.ConnectionFactory;
//import com.yule.open.database.DatabaseAdapter;
//import com.yule.open.database.data.AnalyseResult;
//import com.yule.open.database.enums.DatabaseKind;
//import com.yule.open.database.impl.graph.OracleNodeDatabaseAdapter;
//import com.yule.open.entity.EntityAdapter;
//import com.yule.open.entity.impl.DefaultEntityAdapter;
//import com.yule.open.javapoet.source.JavapoetNodeBatchSourceGenerator;
//import com.yule.open.mediator.EntityTableMediator;
//import com.yule.open.mediator.impl.DefaultEntityTableMediator;
//import com.yule.open.properties.Environment;
//import com.yule.open.utils.Logger;
//import com.yule.open.utils.NameGenerator;
//
//import javax.annotation.processing.AbstractProcessor;
//import javax.annotation.processing.RoundEnvironment;
//import javax.annotation.processing.SupportedAnnotationTypes;
//import javax.lang.model.SourceVersion;
//import javax.lang.model.element.Element;
//import javax.lang.model.element.ElementKind;
//import javax.lang.model.element.TypeElement;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import static com.yule.open.database.ConnectionFactory.getDatabaseKind;
//import static com.yule.open.properties.enums.EnvironmentProperties.*;
//import static com.yule.open.properties.enums.ErrorMessageProperties.*;
//import static com.yule.open.properties.enums.ProcessingMessageProperties.*;
//import static com.yule.open.utils.Logger.*;
//import static com.yule.open.utils.Validator.*;
//
////@AutoService(Processor.class)
//@SupportedAnnotationTypes("")
//public class v1_IHLProcessor extends AbstractProcessor {
//
//    public static NameGenerator nameGenerator;
//    public final static NameGenerator embeddedEntityNameGenerator;
//
//    static {
//        embeddedEntityNameGenerator = new NameGenerator("Embedded", "Id");
//    }
//
//    @Override
//    public SourceVersion getSupportedSourceVersion() {
//        return SourceVersion.RELEASE_8;
//    }
//
//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        Set<String> set = new HashSet<>();
//        set.add(EnableEntityGenerator.class.getName());
//        return set;
//    }
//
//    @Override
//    public Set<String> getSupportedOptions() {
//        Set<String> set = new HashSet<>();
//        set.add("entity.path");
//        set.add("need.setter");
//        set.add("db.password");
//        set.add("need.noArgs");
//        set.add("need.allArgs");
//        set.add("need.getter");
//        set.add("need.builder");
//        set.add("db.username");
//        set.add("db.url");
//        set.add("db.schema");
//        set.add("project.root");
//        return set;
//    }
//
//    @Override
//    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        // createLogger - Initializr
//        Logger.setMessager(processingEnv.getMessager());
//        // createDefaultEntityAdapter - Initializr
//        EntityAdapter entityAdapter = new DefaultEntityAdapter(processingEnv.getElementUtils());
//
//        // getAnnotationElements - ElementValidator
//        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(EnableEntityGenerator.class);
//
//        // checkAnnotationElements - ElementValidator
//        info(FIND_ANNOTATION.getProc());
//        if (elements == null || elements.isEmpty()) return true;
//        if (isOver(1, elements.size())) error(ANNOTATION_DUPLICATED.getMessage());
//        info(FIND_ANNOTATION.getSuccess());
//
//
//        // checkJPADependency - EnvironmentResolver
//        info(FIND_JPA_DEPENDENCY.getProc());
//        if (isEquals(-1, entityAdapter.resolveEntityPath())) error(JPA_NOT_FOUND.getMessage());
//        info(FIND_JPA_DEPENDENCY.getSuccess());
//
//        // validateRequiredEnvironments - EnvironmentResolver
//        info(VALIDATE_REQUIRED_ENVIRONMENTS.getProc());
//        String url = processingEnv.getOptions().get(Required.DB_URL.getEnv());
//        String name = processingEnv.getOptions().get(Required.DB_USERNAME.getEnv());
//        String pw = processingEnv.getOptions().get(Required.DB_PASSWORD.getEnv());
//        String path = processingEnv.getOptions().get(Required.ENTITY_PATH.getEnv());
//        String projectRoot = processingEnv.getOptions().get(Required.PROJECT_ROOT.getEnv());
//        if (anyNull(url, name, pw, path, projectRoot)) error(REQUIRED_ENVIRONMENT_NOT_PROVIDED.getMessage());
//        String entityNamePrefix = processingEnv.getOptions().get(Required.ENTITY_NAME_PREFIX.getEnv());
//        String entityNameSuffix = processingEnv.getOptions().get(Required.ENTITY_NAME_SUFFIX.getEnv());
//
//        String getter = processingEnv.getOptions().get("need.getter");
//        String setter = processingEnv.getOptions().get("need.setter");
//        String noArgs = processingEnv.getOptions().get("need.noArgs");
//        String allArgs = processingEnv.getOptions().get("need.allArgs");
//        String builder = processingEnv.getOptions().get("need.builder");
//
//        // setRequiredEnvironments - EnvironmentResolver
//        Environment.put(Required.DB_URL, url);
//        Environment.put(Required.DB_USERNAME, name);
//        Environment.put(Required.DB_PASSWORD, pw);
//        Environment.put(Required.ENTITY_PATH, path);
//        Environment.put(Required.PROJECT_ROOT, projectRoot);
//
//        // lazy
//        Environment.put(Required.ORACLE_SCHEMA, null);
//        // lazy
//        Environment.put(Required.MY_SQL_AND_MARIA_DB, null);
//
//        Environment.put(Required.ENTITY_NAME_PREFIX, entityNamePrefix);
//        Environment.put(Required.ENTITY_NAME_SUFFIX, entityNameSuffix);
//
//        Environment.put(Optional.GETTER, getter);
//        Environment.put(Optional.SETTER, setter);
//        Environment.put(Optional.NOARGS, noArgs);
//        Environment.put(Optional.ALLARGS, allArgs);
//        Environment.put(Optional.BUILDER, builder);
//
//        Environment.put(AnnotationProcessor.JPA_DEPENDENCY, entityAdapter.getJPADependencyPath());
//        Environment.put(AnnotationProcessor.HIBERNATE_DEPENDENCY, "org.hibernate.annotations");
//        Environment.put(AnnotationProcessor.JAVA_IO, "java.io");
//
//        info(VALIDATE_REQUIRED_ENVIRONMENTS.getSuccess());
//
//        // createNameGenerator - NameGeneratorGenerator
//        nameGenerator = new NameGenerator(Environment.get(Required.ENTITY_NAME_PREFIX),
//                Environment.get(Required.ENTITY_NAME_SUFFIX));
//
//
//        try {
//
//            // createDatabaseAdapterWithJoinConnection - ConnectionAnalyser
//            info("Connect to database...");
//            DatabaseAdapter databaseAdapter = new OracleNodeDatabaseAdapter();
//
//            // analyseConnectionInfo - ConnectionAnalyser
//            info("Check your database name...");
//            DatabaseKind databaseKind = getDatabaseKind();
//            String dbname = null;
//            if (databaseKind == DatabaseKind.ORACLE) {
//                info("Your database kind is ORACLE...");
//                Environment.put(Required.DB_NAME, dbname = processingEnv.getOptions().get(Required.ORACLE_SCHEMA.getEnv()));
//            } else if (databaseKind == DatabaseKind.MYSQL || databaseKind == DatabaseKind.MARIADB) {
//                info("Your database kind is MYSQL(MARIADB)...");
//                Environment.put(Required.DB_NAME, dbname = processingEnv.getOptions().get(Required.MY_SQL_AND_MARIA_DB.getEnv()));
//            }
//            if (isNull(dbname)) error(SCHEMA_OR_DATABASE_NAME_IS_NOT_PROVIDED.getMessage());
//            info("Database is found...");
//
//            // findAllTables - TableFinder
//            List<String> tables = databaseAdapter.findTables(dbname);
//            info("Connection Success...");
//            collectionBatchInfo("elements: ", tables);
//
//
//            // findAlreadyEntities - EntityFinder
//            info("Find exists Entities...");
//            Set<? extends Element> allElements = roundEnv.getRootElements();
//            int cnt = 0;
//            for (Element el : allElements) {
//                if (el.getKind() == ElementKind.CLASS && entityAdapter.hasEntityAnnotation(el)) cnt++;
//            }
//            info(cnt + " classes has @Entity...");
//
//            // entityTableFilter - EntityTableFilter
//            info("Comparing Table and Entity class...");
//            EntityTableMediator mediator = new DefaultEntityTableMediator(entityAdapter, databaseAdapter);
//            List<String> toEntityTables = mediator.compareToTables();
//            info("Find " + toEntityTables.size() + " Tables for Entity Mapping...");
//            collectionBatchInfo("elements: ", toEntityTables);
//
//            if (toEntityTables.isEmpty()) {
//                info("You already have all the entities...");
//                return true;
//            }
//
//            // analyseTable - EntityTableAnalyser
//            info("Analyse your tables with column and constraints...");
////                Table[] entityTables = databaseAdapter.analyseAllTablesAndBatchSources(dbname, toEntityTables);
//            AnalyseResult entityTables = databaseAdapter.analyseAllTablesAndBatchSources(dbname, toEntityTables);
//            info("All tables, columns and constraints READY...");
//
//            // generateSource - SourceGenerator
//            info("Generate Source...");
//            List<TypeSpec> generated = new JavapoetNodeBatchSourceGenerator<>().generate(entityTables);
//            info(generated.size() + " Entity files generated in " + Environment.get(Required.ENTITY_PATH) + "...");
//            info("Done...");
//            info("Success to make Entity Sources!!!");
//
//            return true;
//
//        } finally {
//            // closeConnection - ConnectionCloser
//            ConnectionFactory.close();
//        }
//    }
//}
