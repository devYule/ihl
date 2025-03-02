package com.yule.open.database.info;

import com.yule.open.core.IHLCore;

import java.util.ArrayList;
import java.util.List;

import static com.yule.open.utils.StringUtils.camelFromSnake;

public class Column implements Node {
    private String colNm;
    private String dataType;
    private Double dataLenNum;

    private boolean isFK;
    private String refEntity;


    private Constraint constraint;
    private final List<Constraint> constraints;

    {
        constraints = new ArrayList<>();
        isFK = false;
    }

    public String getRefEntity() {
        return refEntity;
    }

    public void setRefEntity(String refTb) {
        this.refEntity = IHLCore.nameGenerator.generateEntityName(refTb);
    }

    public Column() {
    }

    public Column(String colNm, String dataType, Double dataLenNum) {
        this.colNm = colNm;
        this.dataType = dataType;
        this.dataLenNum = dataLenNum;
    }

    public void setColNm(String colNm) {
        this.colNm = colNm;
    }

    public boolean isFK() {
        return isFK;
    }

    public void setFK(boolean FK) {
        isFK = FK;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setDataLenNum(Double dataLenNum) {
        this.dataLenNum = dataLenNum;
    }

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

    public boolean addConstraint(Constraint constraint) {
        return this.constraints.add(constraint);
    }

    public void addAllConstraints(List<Constraint> constraints) {
        this.constraints.addAll(constraints);
    }

    @Override
    public String getName() {
        return this.getColNm();
    }
}
