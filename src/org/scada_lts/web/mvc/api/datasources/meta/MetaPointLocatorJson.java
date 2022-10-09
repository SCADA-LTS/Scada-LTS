package org.scada_lts.web.mvc.api.datasources.meta;

import com.serotonin.db.IntValuePair;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.vo.TimePeriodType;
import com.serotonin.mango.vo.UpdateEventType;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import org.scada_lts.web.mvc.api.datasources.DataPointLocatorJson;

import java.util.ArrayList;
import java.util.List;

public class MetaPointLocatorJson extends DataPointLocatorJson {

    private List<IntValuePair> context = new ArrayList<>();
    @JsonRemoteProperty
    private String script;
    @JsonRemoteProperty
    private boolean settable;
    private UpdateEventType updateEvent = UpdateEventType.CONTEXT_CHANGE;
    @JsonRemoteProperty
    private String updateCronPattern;
    @JsonRemoteProperty
    private int executionDelaySeconds;
    private TimePeriodType executionDelayPeriodType = TimePeriodType.SECONDS;

    public MetaPointLocatorJson() {}

    public MetaPointLocatorJson(MetaPointLocatorVO pointLocatorVO, int dataSourceTypeId) {
        super(pointLocatorVO, dataSourceTypeId);
        this.context = new ArrayList<>(pointLocatorVO.getContext());
        this.script = pointLocatorVO.getScript();
        this.updateEvent = UpdateEventType.byCode(pointLocatorVO.getUpdateEvent());
        this.updateCronPattern = pointLocatorVO.getUpdateCronPattern();
        this.executionDelaySeconds = pointLocatorVO.getExecutionDelaySeconds();
        this.executionDelayPeriodType = pointLocatorVO.getExecutionDelayPeriodType();
    }

    public List<IntValuePair> getContext() {
        return new ArrayList<>(context);
    }

    public void setContext(List<IntValuePair> context) {
        this.context = new ArrayList<>(context);
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public boolean isSettable() {
        return settable;
    }

    @Override
    public void setSettable(boolean settable) {
        this.settable = settable;
    }

    public UpdateEventType getUpdateEvent() {
        return updateEvent;
    }

    public void setUpdateEvent(UpdateEventType updateEvent) {
        this.updateEvent = updateEvent;
    }

    public String getUpdateCronPattern() {
        return updateCronPattern;
    }

    public void setUpdateCronPattern(String updateCronPattern) {
        this.updateCronPattern = updateCronPattern;
    }

    public int getExecutionDelaySeconds() {
        return executionDelaySeconds;
    }

    public void setExecutionDelaySeconds(int executionDelaySeconds) {
        this.executionDelaySeconds = executionDelaySeconds;
    }

    public TimePeriodType getExecutionDelayPeriodType() {
        return executionDelayPeriodType;
    }

    public void setExecutionDelayPeriodType(TimePeriodType executionDelayPeriodType) {
        this.executionDelayPeriodType = executionDelayPeriodType;
    }

    @Override
    public MetaPointLocatorVO parsePointLocatorData() {
        MetaPointLocatorVO  plVO = new MetaPointLocatorVO();
        plVO.setSettable(this.isSettable());
        plVO.setDataTypeId(this.getDataTypeId());
        plVO.setContext(this.getContext());
        plVO.setScript(this.getScript());
        plVO.setExecutionDelayPeriodType(this.getExecutionDelayPeriodType());
        plVO.setExecutionDelaySeconds(this.getExecutionDelaySeconds());
        plVO.setUpdateEvent(this.getUpdateEvent().getCode());
        plVO.setUpdateCronPattern(this.getUpdateCronPattern());
        return plVO;
    }


}
