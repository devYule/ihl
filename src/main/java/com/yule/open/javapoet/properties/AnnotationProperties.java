package com.yule.open.javapoet.properties;

public interface AnnotationProperties {
    enum ManyToOne {
        FETCH("fetch", "$T.LAZY"),
        TARGET_ENTITY("targetEntity", "$T.class");

        private final String name;
        private final String format;

        ManyToOne(String name, String format) {
            this.name = name;
            this.format = format;
        }

        public String getName() {
            return name;
        }

        public String getFormat() {
            return format;
        }
    }

    enum Check {
        CONSTRAINTS("constraints", "$S");
        private final String name;
        private final String format;

        Check(String name, String format) {
            this.name = name;
            this.format = format;
        }

        public String getName() {
            return name;
        }

        public String getFormat() {
            return format;
        }
    }

    enum Column {
        NULLABLE("nullable", "$L"),
        LENGTH("length", "$L"),
        UNIQUE("unique", "$L");
        private final String name;
        private final String format;

        Column(String name, String format) {
            this.name = name;
            this.format = format;
        }

        public String getName() {
            return name;
        }

        public String getFormat() {
            return format;
        }
    }

    enum GeneratedValue {
        STRATEGY("strategy", "$T." + StrategyFormat.AUTO);

        private final String name;
        private String format;

        GeneratedValue(String name, String format) {
            this.name = name;
            this.format = format;
        }

        public String setFormat(StrategyFormat format) {
            return this.format = "$T." + format.name();
        }

        public String getName() {
            return name;
        }

        public String getFormat() {
            return format;
        }

        public enum StrategyFormat {
            TABLE,
            SEQUENCE,
            IDENTITY,
            UUID,
            AUTO;
        }
    }

    enum MapsId {
        VALUE("value", "$S");
        private final String name;
        private final String format;

        MapsId(String name, String format) {
            this.name = name;
            this.format = format;
        }

        public String getName() {
            return name;
        }

        public String getFormat() {
            return format;
        }
    }

    enum Table {
        NAME("name", "$S"),
        SCHEMA("schema", "$S")
        ;
        private final String name;
        private final String format;

        Table(String name, String format) {
            this.name = name;
            this.format = format;
        }

        public String getName() {
            return name;
        }

        public String getFormat() {
            return format;
        }
    }

}
