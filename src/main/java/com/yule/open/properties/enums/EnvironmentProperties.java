package com.yule.open.properties.enums;

public interface EnvironmentProperties {

    enum Required implements EnvironmentProperties {
        DB_URL("db.url"),
        DB_USERNAME("db.username"),
        DB_PASSWORD("db.password"),
        ENTITY_PATH("entity.path"),
        ENTITY_NAME_PREFIX("entity.name.prefix"),
        ENTITY_NAME_SUFFIX("entity.name.suffix"),
        ORACLE_SCHEMA("db.schema"),
        MY_SQL_AND_MARIA_DB("db.database-name"),
        PROJECT_ROOT("project.root");

        private final String env;

        Required(String env) {
            this.env = env;
        }

        public String getEnv() {
            return env;
        }
    }

    enum Optional implements EnvironmentProperties {
        GETTER("need.getter"),
        SETTER("need.setter"),
        NOARGS("need.noArgs"),
        ALLARGS("need.allArgs"),
        BUILDER("need.builder"),
        ;
        public final String env;

        Optional(String env) {
            this.env = env;
        }
    }


    enum AnnotationProcessor implements EnvironmentProperties {
        JPA_DEPENDENCY("processor.jpaDependency"),
        HIBERNATE_DEPENDENCY("processor.hibernate"),
        JAVA_IO("processor.serializable")
        ;

        private final String env;

        AnnotationProcessor(String env) {
            this.env = env;
        }
    }

}
