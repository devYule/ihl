package com.yule.open.database;

import com.yule.open.database.enums.DatabaseKind;
import com.yule.open.database.enums.QueryKind;
import com.yule.open.info.Column;
import com.yule.open.info.Constraint;
import com.yule.open.info.Table;
import com.yule.open.info.enums.ConstraintsType;

import javax.annotation.processing.Filer;
import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

import static com.yule.open.core.IHLCore.embeddedEntityNameGenerator;
import static com.yule.open.info.enums.ConstraintsType.PRIMARY_KEY;

public class DefaultDatabaseAdapter extends DatabaseAdapter {

    public DefaultDatabaseAdapter(String url, String username, String pw, Filer filer) throws SQLException,
            ClassNotFoundException {
        this.conn = new ConnectionFactory().getConnection(url, username, pw);
        super.allTables = new ArrayList<>();
    }

    @Override
    public List<String> findTables(String dbname) {
        String query = getQueryToken(QueryKind.GET_TABLE);
        try (PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, dbname);
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

    public Table[] analyseAllTablesAndBatchSources(String owner, List<String> tableName) {
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
        try (PreparedStatement ps = conn.prepareStatement(queryWithPreparedStatement)) {

            ps.setString(1, owner);

            idx = 0;
            for (String t : tableName) {
                if (tableName.get(idx) == null) continue;
                ps.setString(idx + 2, t);
                idx++;
            }

            Table[] graph = new Table[tableName.size()]; // 전체 노드의 인접 그래프.
            List<Table> embeddedGraph = new ArrayList<>();
            List<Column> multiplyPkColumn = new ArrayList<>();
            int virtualTableIdx = 0;

            Map<String, List<Column>> columnBatch = new LinkedHashMap<>();
            rs = ps.executeQuery();
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

                Constraint constraint = new Constraint(nullable, ConstraintsType.getByValue(constraintType), refTb, refCol,
                        dataLenVarchar, checkString);
                Column column = new Column(colNm, dataType, dataLenNum, constraint);

                Table table = null;
                if (graph[virtualTableIdx] != null && !tableName.get(virtualTableIdx).equals(tbNm)) {
                    // 테이블이 변경되는 시점.
                    // 여기서 P 제약조건에 대한 처리를 해주어야 함.
                    // 만약 1개면 그냥 컬럼으로 추가해주면 끝.
                    // 만약 2개 이상이면 새로운 Table 을 만들어야 함. (EmbeddedXxxId)
                    // 이 테이블은 P 제약조건을 가진 2개 이상의 컬럼을 대신 가지고 있음.
                    // 또한 해당 Embedded 테이블을 현재 테이블의 컬럼으로 (Type 주의) 추가하고, 제약조건에 EI 를 기재해야 함.

                    boolean compositeFlag = multiplyPkColumn.size() > 1;
                    if (compositeFlag) {
                        Table t = new Table(embeddedEntityNameGenerator.generateDatabaseName(graph[virtualTableIdx].getTbNm()));

                        for (Column col : multiplyPkColumn) {
                            t.addColumn(col);

                            for (Column c : columnBatch.get(col.getColNm())) {
                                c.getConstraint().setConstraintType(ConstraintsType.EMBEDDED_ID);
                            }
                            col.getConstraint().setConstraintType(ConstraintsType.EMBEDDABLE);
                            embeddedGraph.add(t);
                        }
                    }


                    columnBatch.clear();
                    multiplyPkColumn.clear();

                    virtualTableIdx++;
                }

                if (graph[virtualTableIdx] == null) {
                    table = new Table(tbNm);
                    graph[virtualTableIdx] = table;
                }

                if (PRIMARY_KEY.getToken().equalsIgnoreCase(constraintType)) {
                    column.getConstraint().setConstraintType(PRIMARY_KEY);
                    multiplyPkColumn.add(column);
                }
                columnBatch.computeIfAbsent(column.getColNm(), k -> new ArrayList<>());
                List<Column> columns = columnBatch.get(column.getColNm());
                columns.add(column);
                columnBatch.put(column.getColNm(), columns);
                graph[virtualTableIdx].addColumn(column);
            }
            // graph 와 embeddedGraph 합쳐서 반환.
            return Stream.concat(Arrays.stream(graph), embeddedGraph.stream()).toArray(Table[]::new);

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


    private class ConnectionFactory implements DatabaseAdapter.ConnectionFactory {

        private final Map<String, String[]> DRIVER_MAP = new HashMap<>();

        {
            // MySQL
            DRIVER_MAP.put("jdbc:mysql:", new String[]{"com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver"});
            // Oracle
            DRIVER_MAP.put("jdbc:oracle:", new String[]{"oracle.jdbc.OracleDriver", "oracle.jdbc.driver.OracleDriver"});
            // MariaDB
            DRIVER_MAP.put("jdbc:mariadb:", new String[]{"org.mariadb.jdbc.Driver"});
        }

        public Connection getConnection(String url, String username, String password)
                throws SQLException,
                ClassNotFoundException {
            ClassNotFoundException err = new ClassNotFoundException();
            for (Map.Entry<String, String[]> entry : DRIVER_MAP.entrySet()) {
                if (url.startsWith(entry.getKey())) {
                    for (String driver : entry.getValue()) {
                        try {
                            Class.forName(driver);
                            DefaultDatabaseAdapter.this.databaseKind =
                                    DatabaseKind.getByValue(entry.getKey());
                            return DriverManager.getConnection(url, username, password);
                        } catch (ClassNotFoundException e) {
                            err.addSuppressed(e);
                        }
                    }
                    break;
                }
            }
            throw err;
        }


    }


}
