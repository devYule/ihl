package com.yule.open.utils;

import com.yule.open.database.enums.DatabaseKind;
import com.yule.open.properties.EnvironmentProperties;

import javax.annotation.processing.ProcessingEnvironment;

public class Validator {

    public static <T extends Comparable<T>> boolean isUnder(T num, T target) {
        return num.compareTo(target) > 0;
    }

    public static <T extends Comparable<T>> boolean isOver(T num, T target) {
        return num.compareTo(target) < 0;
    }

    public static <T extends Comparable<T>> boolean isEquals(T num, T target) {
        return num.equals(target);
    }

    public static <T> boolean isNull(T target) {
        return target == null;
    }

    public static <T> boolean isNotNull(T target) {
        return !isNull(target);
    }

    public static <T> boolean anyNull(T... target) {
        for (T t : target) {
            if (isNull(t)) return true;
        }
        return false;
    }

    public static <T> boolean anyNotNull(T... target) {
        for (T t : target) {
            if (isNotNull(t)) return true;
        }
        return false;
    }

}
