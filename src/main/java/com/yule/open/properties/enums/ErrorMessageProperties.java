package com.yule.open.properties.enums;

public enum ErrorMessageProperties {
    ANNOTATION_DUPLICATED("Annotation is duplicated!"),
    JPA_NOT_FOUND("JPA not found!"),
    REQUIRED_ENVIRONMENT_NOT_PROVIDED("Required environment not provided!"),
    CAN_NOT_FIND_DATABASE_DRIVER("Can not find database driver!"),
    SCHEMA_OR_DATABASE_NAME_IS_NOT_PROVIDED("Required environment `SCHEMA` or `DATABASE NAME` not provided"),
    INVALID_TYPE("Invalid type error!")
    ;

    private final String message;

    ErrorMessageProperties(String m) {
        this.message = m;
    }

    public String getMessage() {
        return message;
    }
}
