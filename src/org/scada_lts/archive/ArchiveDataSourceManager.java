package org.scada_lts.archive;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


public class ArchiveDataSourceManager {

    private final ConcurrentHashMap<Key, JdbcTemplate> cache = new ConcurrentHashMap<>();

    public JdbcTemplate getOrCreate(String driverClassName, String url, String username, String password) {
        Key k = new Key(url, username);
        return cache.computeIfAbsent(k, kk -> {
            DriverManagerDataSource ds = new DriverManagerDataSource();
            if (driverClassName != null && !driverClassName.isEmpty()) {
                ds.setDriverClassName(driverClassName);
            }
            ds.setUrl(url);
            ds.setUsername(username);
            ds.setPassword(password);
            return new JdbcTemplate(ds);
        });
    }

    public void clear() {
        cache.clear();
    }

    public DataSource getDataSource(String url, String username) {
        JdbcTemplate jt = cache.get(new Key(url, username));
        return jt != null ? jt.getDataSource() : null;
    }

    private static final class Key {
        final String url;
        final String user;

        Key(String url, String user) {
            this.url = Objects.requireNonNull(url, "url");
            this.user = Objects.requireNonNull(user, "user");
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            Key key = (Key) o;
            return url.equals(key.url) && user.equals(key.user);
        }
        @Override public int hashCode() { return Objects.hash(url, user); }
    }
}
