package com.serotonin.mango.rt.maint.work;


import org.scada_lts.quartz.ReadItemsPerSecond;

import java.util.List;

public interface ReadWorkItems {
    List<WorkItemExecute> get();
    ReadItemsPerSecond getItemsPerSecond();
}