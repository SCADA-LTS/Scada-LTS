package org.scada_lts.web.mvc.api.dto.view.components;


import br.org.scadabr.view.component.AlarmListComponent;
import com.serotonin.mango.vo.User;

public class AlarmListComponentDTO extends GraphicalViewComponentDTO{
    private Integer minAlarmLevel;
    private Integer maxListSize;
    private Integer width;
    private Boolean hideIdColumn;
    private Boolean hideAlarmLevelColumn;
    private Boolean hideTimestampColumn;
    private Boolean hideInactivityColumn;
    private Boolean hideAckColumn;

    public AlarmListComponentDTO() {
    }

    public AlarmListComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, Integer minAlarmLevel, Integer maxListSize, Integer width, Boolean hideIdColumn, Boolean hideAlarmLevelColumn, Boolean hideTimestampColumn, Boolean hideInactivityColumn, Boolean hideAckColumn) {
        super(index, idSuffix, x, y, z, typeName);
        this.minAlarmLevel = minAlarmLevel;
        this.maxListSize = maxListSize;
        this.width = width;
        this.hideIdColumn = hideIdColumn;
        this.hideAlarmLevelColumn = hideAlarmLevelColumn;
        this.hideTimestampColumn = hideTimestampColumn;
        this.hideInactivityColumn = hideInactivityColumn;
        this.hideAckColumn = hideAckColumn;
    }

    public Integer getMinAlarmLevel() {
        return minAlarmLevel;
    }

    public void setMinAlarmLevel(Integer minAlarmLevel) {
        this.minAlarmLevel = minAlarmLevel;
    }

    public Integer getMaxListSize() {
        return maxListSize;
    }

    public void setMaxListSize(Integer maxListSize) {
        this.maxListSize = maxListSize;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Boolean getHideIdColumn() {
        return hideIdColumn;
    }

    public void setHideIdColumn(Boolean hideIdColumn) {
        this.hideIdColumn = hideIdColumn;
    }

    public Boolean getHideAlarmLevelColumn() {
        return hideAlarmLevelColumn;
    }

    public void setHideAlarmLevelColumn(Boolean hideAlarmLevelColumn) {
        this.hideAlarmLevelColumn = hideAlarmLevelColumn;
    }

    public Boolean getHideTimestampColumn() {
        return hideTimestampColumn;
    }

    public void setHideTimestampColumn(Boolean hideTimestampColumn) {
        this.hideTimestampColumn = hideTimestampColumn;
    }

    public Boolean getHideInactivityColumn() {
        return hideInactivityColumn;
    }

    public void setHideInactivityColumn(Boolean hideInactivityColumn) {
        this.hideInactivityColumn = hideInactivityColumn;
    }

    public Boolean getHideAckColumn() {
        return hideAckColumn;
    }

    public void setHideAckColumn(Boolean hideAckColumn) {
        this.hideAckColumn = hideAckColumn;
    }

    @Override
    public AlarmListComponent createFromBody(User user) {
        AlarmListComponent c = new AlarmListComponent();
        c.setMinAlarmLevel(minAlarmLevel);
        c.setMaxListSize(maxListSize);
        c.setWidth(width);
        c.setHideAckColumn(hideAckColumn);
        c.setHideAlarmLevelColumn(hideAlarmLevelColumn);
        c.setHideIdColumn(hideIdColumn);
        c.setHideInactivityColumn(hideInactivityColumn);
        c.setHideTimestampColumn(hideTimestampColumn);

        c.setIndex(getIndex());
        c.setIdSuffix(getIdSuffix());
        c.setX(getX());
        c.setY(getY());
        c.setZ(getZ());

        return c;
    }
}
