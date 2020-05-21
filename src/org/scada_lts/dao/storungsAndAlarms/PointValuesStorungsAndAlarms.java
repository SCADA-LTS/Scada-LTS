package org.scada_lts.dao.storungsAndAlarms;
/*
 * (c) 2020 hyski.mateusz@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */
class PointValuesStorungsAndAlarms {

    private int id;

    private String
            pointId,
            pointXid,
            pointType,
            pointName,
            triggerName,
            inactiveTime,
            acknowledgeTime,
            lastpointValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public String getPointXid() {
        return pointXid;
    }

    public void setPointXid(String pointXid) {
        this.pointXid = pointXid;
    }

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getInactiveTime() {
        return inactiveTime;
    }

    public void setInactiveTime(String inactiveTime) {
        this.inactiveTime = inactiveTime;
    }

    public String getAcknowledgeTime() {
        return acknowledgeTime;
    }

    public void setAcknowledgeTime(String acknowledgeTime) {
        this.acknowledgeTime = acknowledgeTime;
    }

    public String getLastpointValue() {
        return lastpointValue;
    }

    public void setLastpointValue(String lastpointValue) {
        this.lastpointValue = lastpointValue;
    }

    @Override
    public String toString() {
        return "PointValuesStorungsAndAlarms{" +
                "id=" + id +
                ", pointId='" + pointId + '\'' +
                ", pointXid='" + pointXid + '\'' +
                ", pointType='" + pointType + '\'' +
                ", pointName='" + pointName + '\'' +
                ", triggerName='" + triggerName + '\'' +
                ", inactiveTime='" + inactiveTime + '\'' +
                ", acknowledgeTime='" + acknowledgeTime + '\'' +
                ", lastpointValue='" + lastpointValue + '\'' +
                '}';
    }
    public JSONObject toJSONObject() {
        try {
            return
                    new JSONObject()
                    .put("id",getId())
                    .put("pointId",getPointId())
                    .put("pointXid",getPointXid())
                    .put("pointType",getPointType())
                    .put("pointName",getPointName())
                    .put("triggerName",getTriggerName())
                    .put("inactiveTime",getInactiveTime())
                    .put("acknowledgeTime",getAcknowledgeTime())
                    .put("lastpointValue",getLastpointValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
