package com.serotonin.mango.util;

import com.serotonin.mango.vo.dataSource.sql.SqlDataSourceVO;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public final class SqlDataSourceUtils {

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
                                            boolean rowBasedQuery, boolean jndiResource, String jndiResourceName) {
        SqlDataSourceVO sqlDataSourceVO = new SqlDataSourceVO();
        sqlDataSourceVO.setDriverClassname(driverClassname);
        sqlDataSourceVO.setConnectionUrl(connectionUrl);
        sqlDataSourceVO.setUsername(username);
        sqlDataSourceVO.setPassword(password);
        sqlDataSourceVO.setSelectStatement(selectStatement);
        sqlDataSourceVO.setRowBasedQuery(rowBasedQuery);
        sqlDataSourceVO.setJndiResource(jndiResource);
        sqlDataSourceVO.setJndiResourceName(jndiResourceName);
        return sqlDataSourceVO;
    }
}
