package com.yule.open.database.data;

import com.yule.open.core.IHLProcessor;
import com.yule.open.database.data.enums.ConstraintsType;
import com.yule.open.utils.NameGenerator;

import static com.yule.open.core.IHLProcessor.context;
import static com.yule.open.utils.StringUtils.camelFromSnake;

public class Constraint implements Node {
    // constraint
    private String nullable;
    private ConstraintsType constraintType; // P: pk, R: fk, U: unique
    private Double dataLenVarchar;
    // fk
    private String refTb; //  R: fk 일 경우만 존재
    private String refCol; //  R: fk 일 경우만 존재

    private String refEntity;

    private String checkString;

    public Constraint() {
    }

    public Constraint(String nullable, ConstraintsType constraintType, String refTb, String refCol, Double dataLenVarchar,
                      String checkString) {
        this.nullable = nullable;
        this.constraintType = constraintType;
        this.refTb = refTb != null ? camelFromSnake(refTb.toLowerCase(), true) : null;
        this.refCol = refCol;
        this.dataLenVarchar = dataLenVarchar;
        this.refEntity = refTb != null && refCol != null ?
                context.getContext(NameGenerator.class).generateEntityName(refTb) :
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

    public Constraint setAll(String nullable, ConstraintsType constraintType, String refTb, String refCol, Double dataLenVarchar,
                             String checkString) {
        this.nullable = nullable;
        this.constraintType = constraintType;
        this.refTb = refTb != null ? camelFromSnake(refTb.toLowerCase(), true) : null;
        this.refCol = refCol;
        this.dataLenVarchar = dataLenVarchar;
        this.refEntity = refTb != null && refCol != null ?
                context.getContext(NameGenerator.class).generateEntityName(refTb) :
                null;
        this.checkString = checkString;
        return this;
    }

    @Override
    public String getName() {
        return this.getConstraintType().getToken();
    }
}
