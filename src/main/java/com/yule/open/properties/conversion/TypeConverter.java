package com.yule.open.properties.conversion;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.yule.open.properties.Environment;
import com.yule.open.properties.enums.EnvironmentProperties;
import com.yule.open.properties.enums.PrimitiveTypeDatabaseToJava;

import java.util.ArrayList;
import java.util.Arrays;

import static com.yule.open.utils.Logger.error;
import static com.yule.open.properties.enums.ErrorMessageProperties.INVALID_TYPE;

public abstract class TypeConverter {

    private static final ArrayList<PrimitiveTypeDatabaseToJava>[] orderedTypes;

    static {
        orderedTypes = new ArrayList[26];
        for (int i = 0; i < orderedTypes.length; i++) {
            orderedTypes[i] = new ArrayList<>();
        }
        Arrays.stream(PrimitiveTypeDatabaseToJava.values()).forEach(e -> {
            orderedTypes[e.name().toLowerCase().charAt(0) - 'a'].add(e);
        });
    }


    public static Class convert(String type, Double numDataLen) {

        int idx = type.toLowerCase().charAt(0) - 'a';
        ArrayList<PrimitiveTypeDatabaseToJava> scope = orderedTypes[idx];
        for (int i = 0; i < scope.size(); i++) {
            PrimitiveTypeDatabaseToJava item = scope.get(i);
            String t = item.name().toLowerCase();
            if (t.replaceAll("_", "").contains(type.replaceAll("_", "").toLowerCase()) &&
                item.getMax() <= numDataLen) {
                return scope.get(i).getType();
            }
        }
        error(INVALID_TYPE.getMessage());
        throw new RuntimeException();
    }

    public static TypeName convert(String refEntity) {
        return ClassName.get(Environment.get(EnvironmentProperties.Required.ENTITY_PATH), refEntity);
    }


}
