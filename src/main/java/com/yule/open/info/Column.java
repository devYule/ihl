package com.yule.open.info;

import static com.yule.open.utils.StringUtils.camelFromSnake;

public class Column {
    private final String colNm;
    private final String dataType;
    private final Double dataLenNum;

    private Constraint constraint;

    public Column(String colNm, String dataType, Double dataLenNum, Constraint constraint) {
        this.colNm = camelFromSnake(colNm.toLowerCase());
        this.dataType = dataType;
        this.dataLenNum = dataLenNum;
        this.constraint = constraint;
    }

    public String getColNm() {
        return colNm;
    }

    public String getDataType() {
        return dataType;
    }

    public Double getDataLenNum() {
        return dataLenNum;
    }

    public Constraint getConstraint() {
        return constraint;
    }

}
