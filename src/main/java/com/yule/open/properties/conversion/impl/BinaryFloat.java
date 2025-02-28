package com.yule.open.properties.conversion.impl;

import com.yule.open.properties.conversion.ConvertType;

public class BinaryFloat {
    private static String fToken;
    private static String sToken;
    private static Class<?> type;

    static {
        fToken = "binary";
        sToken = "float";
        type = Float.class;
    }


    public boolean check(String type, Double numDataLen, Double varcharLen) {
        String t = type.toLowerCase();
        return stageOne(t) && stageTwo(t);
    }

    public boolean stageOne(String type) {
        return type.contains(fToken);
    }

    public boolean stageTwo(String type) {
        return type.contains(sToken);
    }


}
