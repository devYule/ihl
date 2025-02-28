package com.yule.open.info;

import com.yule.open.core.IHLCore;
import com.yule.open.info.enums.ConstraintsType;

import static com.yule.open.utils.StringUtils.camelFromSnake;

public class Constraint {
    // constraint
    private final String nullable;
    private ConstraintsType constraintType; // P: pk, R: fk, U: unique
    private final Double dataLenVarchar;
    // fk
    private final String refTb; //  R: fk 일 경우만 존재
    private final String refCol; //  R: fk 일 경우만 존재

    private final String refEntity;

    private final String checkString;


    public Constraint(String nullable, ConstraintsType constraintType, String refTb, String refCol, Double dataLenVarchar,
                      String checkString) {
        this.nullable = nullable;
        this.constraintType = constraintType;
        this.refTb = refTb != null ? camelFromSnake(refTb.toLowerCase(), true) : null;
        this.refCol = refCol;
        this.dataLenVarchar = dataLenVarchar;
        this.refEntity = refTb != null && refCol != null ?
                IHLCore.nameGenerator.generateEntityName(refTb) :
                null;
        this.checkString = checkString;
    }

    public void setConstraintType(ConstraintsType constraintType) {
        this.constraintType = constraintType;
    }

    public String getNullable() {
        return nullable;
    }

    public ConstraintsType getConstraintType() {
        return constraintType;
    }

    public String getRefTb() {
        return refTb;
    }

    public String getRefCol() {
        return refCol;
    }

    public Double getDataLenVarchar() {
        return dataLenVarchar;
    }

    public String getRefEntity() {
        return refEntity;
    }

    public String getCheckString() {
        return checkString;
    }
}
