package com.yule.open.database.info;

import java.util.ArrayList;
import java.util.List;

public class Table implements Node {

    private  String tbNm;
    private final List<Column> columns;

    {
        this.columns = new ArrayList<>();
    }

    public Table() {

    }

    public void setTbNm(String tbNm) {
        this.tbNm = tbNm;
    }

    public Table(String tbNm, Column column) {
        this.tbNm = tbNm;
        this.columns.add(column);
    }

    public Table(String tbNm) {
        this.tbNm = tbNm;
    }

    public String getTbNm() {
        return tbNm;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void addColumn(Column column) {
        this.columns.add(column);
    }
    public void addColumn(List<Column> column) {
        this.columns.addAll(column);
    }


    @Override
    public String getName() {
        return this.getTbNm();
    }
}
