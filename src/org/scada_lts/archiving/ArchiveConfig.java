package org.scada_lts.archiving;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ArchiveConfig {

    private boolean enabled;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private int batchSize;
    private String cron;
    private List<ArchiveTask> tasks;

    @JsonCreator
    public ArchiveConfig(@JsonProperty("enabled") boolean enabled,
                         @JsonProperty("dbUrl") String dbUrl,
                         @JsonProperty("dbUsername") String dbUsername,
                         @JsonProperty("dbPassword") String dbPassword,
                         @JsonProperty("batchSize") int batchSize,
                         @JsonProperty("cron") String cron,
                         @JsonProperty("tasks") List<ArchiveTask> tasks

    ) {
        this.enabled = enabled;
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.batchSize = batchSize;
        this.cron = cron;
        this.tasks = tasks;
    }

    public ArchiveConfig() {}

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getDbUrl() { return dbUrl; }
    public void setDbUrl(String dbUrl) { this.dbUrl = dbUrl; }

    public String getDbUsername() { return dbUsername; }
    public void setDbUsername(String dbUsername) { this.dbUsername = dbUsername; }

    public String getDbPassword() { return dbPassword; }
    public void setDbPassword(String dbPassword) { this.dbPassword = dbPassword; }

    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }

    public String getCron() { return cron; }
    public void setCron(String cron) { this.cron = cron; }

    public List<ArchiveTask> getTasks() { return tasks; }
    public void setTasks(List<ArchiveTask> tasks) { this.tasks = tasks; }
}
