package org.scada_lts.archive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ArchiveConfig {

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private int batchSize;
    private String cron;
    private List<ArchiveTask> tasks;

    private String driverClassName;

    @JsonCreator
    public ArchiveConfig(@JsonProperty("dbUrl") String dbUrl,
                         @JsonProperty("dbUsername") String dbUsername,
                         @JsonProperty("dbPassword") String dbPassword,
                         @JsonProperty("batchSize") int batchSize,
                         @JsonProperty("cron") String cron,
                         @JsonProperty("tasks") List<ArchiveTask> tasks,
                         @JsonProperty("driverClassName") String driverClassName) {
        this.dbUrl = trimOrNull(dbUrl);
        this.dbUsername = trimOrNull(dbUsername);
        this.dbPassword = dbPassword;
        this.batchSize = batchSize;
        this.cron = trimOrNull(cron);
        this.tasks = tasks;
        this.driverClassName = trimOrNull(driverClassName);
    }

    public ArchiveConfig() {}

    public String getDbUrl() { return dbUrl; }
    public void setDbUrl(String dbUrl) { this.dbUrl = trimOrNull(dbUrl); }

    public String getDbUsername() { return dbUsername; }
    public void setDbUsername(String dbUsername) { this.dbUsername = trimOrNull(dbUsername); }

    public String getDbPassword() { return dbPassword; }
    public void setDbPassword(String dbPassword) { this.dbPassword = dbPassword; }

    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }

    public String getCron() { return cron; }
    public void setCron(String cron) { this.cron = trimOrNull(cron); }

    public List<ArchiveTask> getTasks() { return tasks; }
    public void setTasks(List<ArchiveTask> tasks) { this.tasks = tasks; }

    public String getDriverClassName() { return driverClassName; }
    public void setDriverClassName(String driverClassName) { this.driverClassName = trimOrNull(driverClassName); }

    @Override
    public String toString() {
        return "ArchiveConfig{" +
                "dbUrl='" + dbUrl + '\'' +
                ", dbUsername='" + dbUsername + '\'' +
                ", dbPassword='" + mask(dbPassword) + '\'' +
                ", batchSize=" + batchSize +
                ", cron='" + cron + '\'' +
                ", tasks=" + (tasks == null ? 0 : tasks.size()) +
                ", driverClassName='" + driverClassName + '\'' +
                '}';
    }

    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String mask(String s) {
        if (s == null) return null;
        if (s.length() <= 2) return "***";
        return s.charAt(0) + "***" + s.charAt(s.length() - 1);
    }
}
