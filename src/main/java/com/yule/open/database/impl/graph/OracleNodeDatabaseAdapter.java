package com.yule.open.database.impl.graph;

import com.yule.open.database.data.*;
import com.yule.open.database.data.enums.ConstraintsType;
import com.yule.open.database.enums.QueryKind;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.yule.open.database.ConnectionFactory.getConnection;


public class OracleNodeDatabaseAdapter extends NodeDatabaseAdapter {

    @Override
    public List<String> findTables(String dbname) {
        String query = getQueryToken(QueryKind.GET_TABLE);
        try (PreparedStatement ps = getConnection().prepareStatement(query)) {
            ps.setString(1, dbname);
            ps.setString(2, dbname);
            ps.setString(3, dbname);
            ps.setString(4, dbname);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    allTables.add(resultSet.getString(1));
                }
                return allTables;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AnalyseResult analyseAllTablesAndBatchSources(String owner, List<String> tableName) {

        StringBuilder sb = new StringBuilder();
        String queryWithPreparedStatement = getQueryToken(QueryKind.ALL);
        int idx = 1;
        sb.append("(");
        for (String t : tableName) {
            if (t == null) continue;
            if (idx == 1) {
                sb.append("?");
            } else if ((idx / 100) >= 1 && idx % 100 == 0) {
                sb.append(") or cols.TABLE_NAME in (?");
            } else sb.append(", ?");
            idx++;
        }
        sb.append(")");
        sb.append(" ORDER BY\n" +
                  "         cols.OWNER, cols.TABLE_NAME, cols.COLUMN_ID");
        queryWithPreparedStatement += sb;


        ResultSet rs = null;
        try (PreparedStatement ps = getConnection().prepareStatement(queryWithPreparedStatement)) {

            ps.setString(1, owner);

            idx = 0;
            for (String t : tableName) {
                if (tableName.get(idx) == null) continue;
                ps.setString(idx + 2, t);
                idx++;
            }

            rs = ps.executeQuery();
            List<Node> node = new ArrayList<>();
            List<List<Integer>> graph = new ArrayList<>();
            int rootCursor = 0, tableCursor = 0, columnCursor = 0, constraintCursor = 0;

            node.add(new RootNode());
            graph.add(new ArrayList<>());

            while (rs.next()) {

                // table
                String tbNm = rs.getString(2);

                // col
                String colNm = rs.getString(3);
                String dataType = rs.getString(4);
                Double dataLenVarchar = rs.getDouble(5);
                Double dataLenNum = rs.getDouble(6);

                // constraint
                String nullable = rs.getString(8);
                String constraintType = rs.getString(9); // P: pk, R: fk, U: unique
                // fk
                String refTb = rs.getString(12); //  R: fk 일 경우만 존재
                String refCol = rs.getString(13); //  R: fk 일 경우만 존재
                String checkString = rs.getString(14); // check 문장




                /*
                 * List 의 size 를 커서로 두고, List 에 요소를 추가하면, 자동으로 List 의 마지막 요소가 cursor 가 된다.
                 * */

                if (!node.get(tableCursor).getName().equals(tbNm)) {
                    tableCursor = getNewTableCursor(node, tbNm, graph, rootCursor);
                    // 테이블이 바뀌면 컬럼도 변경해야함.
                    // 만약 우연히 테이블 명이 변경됐지만, 동일한 컬럼명을 사용한다면,
                    // 새로운 테이블의 새로운 컬럼에 연관관계가 지어져야 하는 제약조건이,
                    // 이전 테이블의 컬럼에 추가될 수 있음. (컬럼 cursor 는 변하지 않으므로,
                    //   변하지 않은 컬럼 커서는 이전 테이블과 연관관계를 가지고 있음.)
                    // 따라서 해당 컬럼은 새로 변경된 테이블과 연관관계를 짓지 않게 됨.
                    columnCursor = getNewColumnCursor(node, colNm, dataType, dataLenNum, graph, tableCursor);
                    // 아래의 if 절 내에 or (||) 연산자로 추가하면,
                    // 이미 해당 if 절에서 테이블 명이 정상적으로 변경됐기 때문에,
                    // 아래 if 절 내의 !node.get(tableCursor).getName().equals(tbNm) 는,
                    // 언제나 false 가 된다.
                    // 따라서 테이블 명이 변경 될 경우에 컬럼을 새로 만들지 않게 되는 문제가 발생한다.
                }
                if (!node.get(columnCursor).getName().equals(colNm)) {
                    columnCursor = getNewColumnCursor(node, colNm, dataType, dataLenNum, graph, tableCursor);
                }
                if (constraintType != null && !constraintType.isEmpty()) {
                    constraintCursor = getNewConstraintCursor(node, nullable, constraintType, refTb, refCol, dataLenVarchar, checkString, graph, columnCursor);

                    if ("R".equalsIgnoreCase(constraintType)) {
                        ((Column) node.get(columnCursor)).setFK(true);
                        if (refTb != null && refCol != null) ((Column) node.get(columnCursor)).setRefTb(refTb);
                    }
                }


            }

            return new AnalyseResult(node, graph);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private int getNewConstraintCursor(List<Node> node, String nullable, String constraintType, String refTb, String refCol, Double dataLenVarchar, String checkString, List<List<Integer>> graph, int columnCursor) {
        int constraintCursor = node.size();
        node.add(new Constraint(nullable, ConstraintsType.getByValue(constraintType),
                refTb, refCol, dataLenVarchar, checkString));
        graph.add(new ArrayList<>());
        graph.get(columnCursor).add(constraintCursor);
        return constraintCursor;
    }

    private int getNewTableCursor(List<Node> node, String tbNm, List<List<Integer>> graph, int rootCursor) {
        int tableCursor = node.size();
        node.add(new Table(tbNm));
        graph.add(new ArrayList<>());
        graph.get(rootCursor).add(tableCursor);
        return tableCursor;
    }

    private int getNewColumnCursor(List<Node> node, String colNm, String dataType, Double dataLenNum, List<List<Integer>> graph, int tableCursor) {
        int columnCursor = node.size();
        node.add(new Column(colNm, dataType, dataLenNum));
        graph.add(new ArrayList<>());
        graph.get(tableCursor).add(columnCursor);
        return columnCursor;
    }

}
