package com.yule.open.properties;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Period;

public enum PrimitiveTypeDatabaseToJava {

    BINARY_FLOAT(0, Float.class),
    BINARY_DOUBLE(0, Double.class),
    BLOB(0, byte[].class),
    CHAR(0, String.class),
    CLOB(0, String.class),
    DATE(0, LocalDateTime.class),
    FLOAT(0, Double.class),
    INTERVAL_YEAR_TO_MONTH(0, Period.class),
    INTERVAL_DAY_TO_SECOND(0, Duration.class),
    NUMBER_OVER_10(10, Long.class),
    NUMBER_OVER_5(5, Integer.class),
    NUMBER(0, Long.class),
    RAW(0, byte[].class),
    TIMESTAMP(0, LocalDateTime.class),
    TIMESTAMP_WITH_TIME_ZONE(0, OffsetDateTime.class),
    VARCHAR2(0, String.class),
    ;

    private final int max;
    private final Class<?> type;

    PrimitiveTypeDatabaseToJava(int max, Class<?> type) {
        this.max = max;
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    public int getMax() {
        return max;
    }
}
