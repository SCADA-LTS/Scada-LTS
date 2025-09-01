package org.scada_lts.archive;

import com.serotonin.mango.db.DatabaseAccess;
import com.serotonin.mango.db.MySQLAccess;
import com.serotonin.mango.db.PostgreSQLAccess;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class ArchiveQueryProviderFactory {

    private final DatabaseAccess databaseAccess;

    public ArchiveQueryProviderFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public static IArchiveQueryProvider newInstance(JdbcTemplate targetJdbc) {
        return forTarget(targetJdbc, targetJdbc);
    }

    public IArchiveQueryProvider get() {
        if (databaseAccess instanceof PostgreSQLAccess) {
            return new PostgresArchiveQueryProvider();
        } else if (databaseAccess instanceof MySQLAccess) {
            return new MySqlArchiveQueryProvider();
        }
        throw new IllegalStateException("Unsupported DB: " + databaseAccess.getClass().getName());
    }

    public static IArchiveQueryProvider forTarget(JdbcTemplate targetJdbc, JdbcTemplate sourceMetaJdbc) {
        String product = null;
        String url = null;
        try (Connection c = targetJdbc.getDataSource().getConnection()) {
            DatabaseMetaData md = c.getMetaData();
            product = md.getDatabaseProductName();
            url = md.getURL();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to detect DB vendor for archiving provider", e);
        }
        String p = safe(product);
        String u = safe(url);

        IArchiveQueryProvider provider;
        if (p.contains("mysql") || p.contains("mariadb") || u.startsWith("jdbc:mysql:") || u.startsWith("jdbc:mariadb:")) {
            provider = new MySqlArchiveQueryProvider();
        } else if (p.contains("postgresql") || u.startsWith("jdbc:postgresql:")) {
            PostgresArchiveQueryProvider pg = new PostgresArchiveQueryProvider();
            if (sourceMetaJdbc != null) {
                pg.setJdbcTemplate(sourceMetaJdbc);
            }
            provider = pg;
        } else {
            throw new UnsupportedOperationException("Unsupported DB for archiving provider: product=" + product + ", url=" + url);
        }
        return provider;
    }

    private static String safe(String s) { return s == null ? "" : s.toLowerCase(); }
}
