package com.yule.open.core.chain.impl;

import com.yule.open.core.chain.Chain;
import com.yule.open.entity.EntityAdapter;
import com.yule.open.properties.Environment;
import com.yule.open.properties.enums.EnvironmentProperties;

import javax.annotation.processing.ProcessingEnvironment;

import static com.yule.open.core.IHLProcessor.context;
import static com.yule.open.properties.enums.ErrorMessageProperties.JPA_NOT_FOUND;
import static com.yule.open.properties.enums.ErrorMessageProperties.REQUIRED_ENVIRONMENT_NOT_PROVIDED;
import static com.yule.open.properties.enums.ProcessingMessageProperties.FIND_JPA_DEPENDENCY;
import static com.yule.open.properties.enums.ProcessingMessageProperties.VALIDATE_REQUIRED_ENVIRONMENTS;
import static com.yule.open.utils.Logger.error;
import static com.yule.open.utils.Logger.info;
import static com.yule.open.utils.Validator.*;

public class EnvironmentResolver extends Chain {
    public EnvironmentResolver(int order) {
        super(order);
    }

    @Override
    public boolean execute() {

        EntityAdapter entityAdapter = context.getContext(EntityAdapter.class);
        ProcessingEnvironment processingEnv = context.getContext(ProcessingEnvironment.class);

        info(FIND_JPA_DEPENDENCY.getProc());
        if (isEquals(-1, entityAdapter.resolveEntityPath())) error(JPA_NOT_FOUND.getMessage());
        info(FIND_JPA_DEPENDENCY.getSuccess());

        info(VALIDATE_REQUIRED_ENVIRONMENTS.getProc());
        String url = processingEnv.getOptions().get(EnvironmentProperties.Required.DB_URL.getEnv());
        String name = processingEnv.getOptions().get(EnvironmentProperties.Required.DB_USERNAME.getEnv());
        String pw = processingEnv.getOptions().get(EnvironmentProperties.Required.DB_PASSWORD.getEnv());
        String path = processingEnv.getOptions().get(EnvironmentProperties.Required.ENTITY_PATH.getEnv());
        String projectRoot = processingEnv.getOptions().get(EnvironmentProperties.Required.PROJECT_ROOT.getEnv());
        if (anyNull(url, name, pw, path, projectRoot)) error(REQUIRED_ENVIRONMENT_NOT_PROVIDED.getMessage());
        String entityNamePrefix = processingEnv.getOptions().get(EnvironmentProperties.Required.ENTITY_NAME_PREFIX.getEnv());
        String entityNameSuffix = processingEnv.getOptions().get(EnvironmentProperties.Required.ENTITY_NAME_SUFFIX.getEnv());

        String getter = processingEnv.getOptions().get(EnvironmentProperties.Optional.GETTER.env);
        String setter = processingEnv.getOptions().get(EnvironmentProperties.Optional.SETTER.env);
        String noArgs = processingEnv.getOptions().get(EnvironmentProperties.Optional.NOARGS.env);
        String allArgs = processingEnv.getOptions().get(EnvironmentProperties.Optional.ALLARGS.env);
        String builder = processingEnv.getOptions().get(EnvironmentProperties.Optional.BUILDER.env);

        // setRequiredEnvironments - EnvironmentResolver
        Environment.put(EnvironmentProperties.Required.DB_URL, url);
        Environment.put(EnvironmentProperties.Required.DB_USERNAME, name);
        Environment.put(EnvironmentProperties.Required.DB_PASSWORD, pw);
        Environment.put(EnvironmentProperties.Required.ENTITY_PATH, path);
        Environment.put(EnvironmentProperties.Required.PROJECT_ROOT, projectRoot);

        String schema = processingEnv.getOptions().get(EnvironmentProperties.Required.ORACLE_SCHEMA.getEnv());
        String dbname = processingEnv.getOptions().get(EnvironmentProperties.Required.MY_SQL_AND_MARIA_DB.getEnv());
        if (!anyNotNull(schema, dbname)) error(REQUIRED_ENVIRONMENT_NOT_PROVIDED.getMessage());
        Environment.put(EnvironmentProperties.Required.ORACLE_SCHEMA, schema);
        Environment.put(EnvironmentProperties.Required.MY_SQL_AND_MARIA_DB, dbname);

        Environment.put(EnvironmentProperties.Required.ENTITY_NAME_PREFIX, entityNamePrefix);
        Environment.put(EnvironmentProperties.Required.ENTITY_NAME_SUFFIX, entityNameSuffix);

        Environment.put(EnvironmentProperties.Optional.GETTER, getter);
        Environment.put(EnvironmentProperties.Optional.SETTER, setter);
        Environment.put(EnvironmentProperties.Optional.NOARGS, noArgs);
        Environment.put(EnvironmentProperties.Optional.ALLARGS, allArgs);
        Environment.put(EnvironmentProperties.Optional.BUILDER, builder);

        Environment.put(EnvironmentProperties.AnnotationProcessor.JPA_DEPENDENCY, entityAdapter.getJPADependencyPath());
        Environment.put(EnvironmentProperties.AnnotationProcessor.HIBERNATE_DEPENDENCY, "org.hibernate.annotations");
        Environment.put(EnvironmentProperties.AnnotationProcessor.JAVA_IO, "java.io");

        info(VALIDATE_REQUIRED_ENVIRONMENTS.getSuccess());

        return doNext();
    }
}
