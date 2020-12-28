package com.serotonin.mango.rt.event.type;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import org.scada_lts.service.CommunicationChannel;

import java.util.Map;

@JsonRemoteEntity
public class ScheduledInactiveEventType extends EventType {

    private final EventType type;
    private final CommunicationChannel communicationChannel;

    public ScheduledInactiveEventType(EventType type, CommunicationChannel communicationChannel) {
        this.type = type;
        this.communicationChannel = communicationChannel;
    }

    public CommunicationChannel getCommunicationChannel() {
        return communicationChannel;
    }

    @Override
    public int getEventSourceId() {
        return type.getEventSourceId();
    }

    @Override
    public int getDataSourceId() {
        return type.getDataSourceId();
    }

    @Override
    public int getDataPointId() {
        return type.getDataPointId();
    }

    @Override
    public int getDuplicateHandling() {
        return type.getDuplicateHandling();
    }

    @Override
    public int getReferenceId1() {
        return type.getReferenceId1();
    }

    @Override
    public int getReferenceId2() {
        return type.getReferenceId2();
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return type.equals(obj);
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        type.jsonSerialize(map);
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        type.jsonDeserialize(reader, json);
    }

    @Override
    public boolean isSystemMessage() {
        return type.isSystemMessage();
    }

    @Override
    public int getScheduleId() {
        return type.getScheduleId();
    }

    @Override
    public int getCompoundEventDetectorId() {
        return type.getCompoundEventDetectorId();
    }

    @Override
    public int getPublisherId() {
        return type.getPublisherId();
    }

    @Override
    public boolean excludeUser(User user) {
        return type.excludeUser(user);
    }

    @Override
    public int getInt(JsonObject json, String name, ExportCodes codes) throws JsonException {
        return type.getInt(json, name, codes);
    }

    @Override
    public int getCompoundEventDetectorId(JsonObject json, String name) throws JsonException {
        return type.getCompoundEventDetectorId(json, name);
    }

    @Override
    public int getScheduledEventId(JsonObject json, String name) throws JsonException {
        return type.getScheduledEventId(json, name);
    }

    @Override
    public int getDataPointId(JsonObject json, String name) throws JsonException {
        return type.getDataPointId(json, name);
    }

    @Override
    public int getPointEventDetectorId(JsonObject json, String dpName, String pedName) throws JsonException {
        return type.getPointEventDetectorId(json, dpName, pedName);
    }

    @Override
    public int getPointEventDetectorId(JsonObject json, int dpId, String pedName) throws JsonException {
        return type.getPointEventDetectorId(json, dpId, pedName);
    }

    @Override
    public DataSourceVO<?> getDataSource(JsonObject json, String name) throws JsonException {
        return type.getDataSource(json, name);
    }

    @Override
    public PublisherVO<?> getPublisher(JsonObject json, String name) throws JsonException {
        return type.getPublisher(json, name);
    }

    @Override
    public int getMaintenanceEventId(JsonObject json, String name) throws JsonException {
        return type.getMaintenanceEventId(json, name);
    }

    @Override
    public String toString() {
        return "ScheduledInactiveEventType{" +
                "type=" + type +
                ", communicationChannel=" + communicationChannel +
                '}';
    }
}
