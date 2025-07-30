package org.scada_lts.archiving;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ArchivalConfig {

    private List<ArchivalTask> tasks;

    @JsonCreator
    public ArchivalConfig(@JsonProperty("tasks") List<ArchivalTask> tasks) {
        this.tasks = tasks;
    }

    public ArchivalConfig() {}

    public List<ArchivalTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<ArchivalTask> tasks) {
        this.tasks = tasks;
    }
}
