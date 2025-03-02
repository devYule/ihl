package com.yule.open.utils;

import com.squareup.javapoet.TypeName;
import com.yule.open.properties.conversion.ConvertType;

public abstract class JDatabaseConverter {
    public static Class convert(String type, Double numDataLen) {
        return findClass(type, numDataLen);
    }

    public static TypeName convert(String refEntity) {
        return findClass(refEntity);
    }


    private static Class findClass(String type, Double numDataLen) {
        return ConvertType.check(type, numDataLen);
    }
    private static TypeName findClass(String refEntity) {
        return ConvertType.check(refEntity);
    }
}
