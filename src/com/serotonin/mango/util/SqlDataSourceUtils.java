package com.serotonin.mango.util;

import com.serotonin.mango.vo.dataSource.sql.SqlDataSourceVO;
import org.apache.commons.dbcp.BasicDataSource;
import org.scada_lts.serorepl.utils.StringUtils;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public final class SqlDataSourceUtils {

    private static final String SELECT_WITH_LIMIT_LOWER_CASE = "select(?:[^;']|(?:'[^']+'))+ limit +\\d+;?\\s*$";

    private SqlDataSourceUtils() {}

    public static JdbcOperations createJdbcOperations(SqlDataSourceVO vo) throws NamingException {
        if(vo.isJndiResource()) {
            DataSource dataSource = (DataSource) new InitialContext().lookup("java:comp/env/" + vo.getJndiResourceName());
            return new JdbcTemplate(dataSource);
        } else {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName(vo.getDriverClassname());
            dataSource.setUrl(vo.getConnectionUrl());
            dataSource.setUsername(vo.getUsername());
            dataSource.setPassword(vo.getPassword());
            return new JdbcTemplate(dataSource);
        }
    }

    public static SqlDataSourceVO createSqlDataSourceVO(String driverClassname, String connectionUrl,
                                                        String username, String password, String selectStatement,
                                                        boolean rowBasedQuery, boolean jndiResource, String jndiResourceName,
                                                        int statementLimit) {
        SqlDataSourceVO sqlDataSourceVO = new SqlDataSourceVO();
        sqlDataSourceVO.setDriverClassname(driverClassname);
        sqlDataSourceVO.setConnectionUrl(connectionUrl);
        sqlDataSourceVO.setUsername(username);
        sqlDataSourceVO.setPassword(password);
        sqlDataSourceVO.setSelectStatement(selectStatement);
        sqlDataSourceVO.setRowBasedQuery(rowBasedQuery);
        sqlDataSourceVO.setJndiResource(jndiResource);
        sqlDataSourceVO.setJndiResourceName(jndiResourceName);
        sqlDataSourceVO.setStatementLimit(statementLimit);
        return sqlDataSourceVO;
    }

    public static String addLimitIfWithout(String query, int defaultLimit) {
        if(StringUtils.isEmpty(query) || isInvalidValue(query))
            throw new IllegalArgumentException("Select statement cannot be empty!");
        if(isSetLimitSelect(query, defaultLimit))
            return reduce(query) + " LIMIT " + defaultLimit;
        return query;
    }

    public static boolean isSetLimitSelect(String query, int defaultLimit) {
        String queryLowerCase = query.toLowerCase();
        return !queryLowerCase.matches(SELECT_WITH_LIMIT_LOWER_CASE) && defaultLimit != 0;
    }

    public static String selectWithLimitLowerCaseEscape() {
        return SELECT_WITH_LIMIT_LOWER_CASE.replace("\\", "\\\\");
    }

    public static boolean isInvalidValue(String query) {
        return ";".equals(query);
    }

    private static String reduce(String sql) {
        return sql.endsWith(";") ? sql.substring(0, sql.length() - 1) : sql;
    }
}
