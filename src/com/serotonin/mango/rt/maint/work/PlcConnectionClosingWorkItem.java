package com.serotonin.mango.rt.maint.work;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.spi.connection.DefaultNettyPlcConnection;

import java.util.concurrent.ExecutionException;

public class PlcConnectionClosingWorkItem extends AbstractBeforeAfterWorkItem {

    private final PlcConnection plcConnection;

    public PlcConnectionClosingWorkItem(PlcConnection plcConnection) {
        this.plcConnection = plcConnection;
    }

    @Override
    public void work() {
        try {
            plcConnection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WorkItemPriority getPriorityType() {
        return WorkItemPriority.LOW;
    }

    @Override
    public String getDetails() {
        return "[OPC UA] Closed connection";
    }
}
