package org.scada_lts.web.mvc.api.datasources.meta;

import com.serotonin.db.IntValuePair;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.vo.TimePeriodType;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import org.scada_lts.web.mvc.api.datasources.DataPointLocatorJson;

import java.util.ArrayList;
import java.util.List;

public class MetaPointLocatorJson extends DataPointLocatorJson {

    private List<IntValuePair> context = new ArrayList<>();
    @JsonRemoteProperty
    private String script;
    private int dataTypeId;
    @JsonRemoteProperty
    private boolean settable;
    private int updateEvent = MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_CHANGE;
    @JsonRemoteProperty
    private String updateCronPattern;
    @JsonRemoteProperty
    private int executionDelaySeconds;
    private TimePeriodType executionDelayPeriodType = TimePeriodType.SECONDS;

    public MetaPointLocatorJson() {}

    public MetaPointLocatorJson(MetaPointLocatorVO pointLocatorVO) {
        super(pointLocatorVO);
        this.context = new ArrayList<>(pointLocatorVO.getContext());
        this.script = pointLocatorVO.getScript();
        this.updateEvent = pointLocatorVO.getUpdateEvent();
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
    public int getDataTypeId() {
        return dataTypeId;
    }

    @Override
    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    @Override
    public boolean isSettable() {
        return settable;
    }

    @Override
    public void setSettable(boolean settable) {
        this.settable = settable;
    }

    public int getUpdateEvent() {
        return updateEvent;
    }

    public void setUpdateEvent(int updateEvent) {
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
        plVO.setUpdateEvent(this.getUpdateEvent());
        plVO.setUpdateCronPattern(this.getUpdateCronPattern());
        return plVO;
    }


}
