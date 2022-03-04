package com.serotonin.mango.vo.report;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.serotonin.json.*;
import com.serotonin.mango.Common;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.serorepl.utils.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;

@JsonRemoteEntity
public class ReportPointVO implements Serializable, JsonSerializable {

    private int pointId;
    private String pointXid;
    @JsonRemoteProperty
    private String colour;
    @JsonRemoteProperty
    private boolean consolidatedChart;

    public static ReportPointVO newInstance(HashMap<String, Object> dp) {
        ReportPointVO reportPointVO = new ReportPointVO();
        reportPointVO.setPointId((Integer) dp.get("pointId"));
        reportPointVO.setColour((String) dp.get("colour"));
        reportPointVO.setConsolidatedChart((Boolean) dp.get("consolidatedChart"));
        return reportPointVO;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public boolean isConsolidatedChart() {
        return consolidatedChart;
    }

    public void setConsolidatedChart(boolean consolidatedChart) {
        this.consolidatedChart = consolidatedChart;
    }

    public String getPointXid() {
        return pointXid;
    }

    public void setPointXid(String pointXid) {
        this.pointXid = pointXid;
    }

    //
    //
    // Serialization
    //
    private static final long serialVersionUID = -1;
    private static final int version = 3;

    private static final Log LOG = LogFactory.getLog(ReportPointVO.class);

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        out.writeInt(pointId);
        SerializationHelper.writeSafeUTF(out, colour);
        out.writeBoolean(consolidatedChart);
        SerializationHelper.writeSafeUTF(out, pointXid);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            pointId = in.readInt();
            colour = SerializationHelper.readSafeUTF(in);
            consolidatedChart = true;
        }
        else if (ver == 2) {
            pointId = in.readInt();
            colour = SerializationHelper.readSafeUTF(in);
            consolidatedChart = in.readBoolean();
        }
        else if (ver == 3) {
            pointId = in.readInt();
            colour = SerializationHelper.readSafeUTF(in);
            consolidatedChart = in.readBoolean();
            pointXid = SerializationHelper.readSafeUTF(in);
        }
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("pointId", pointId);
        map.put("pointXid", pointXid);
    }

    @Override
    public void jsonDeserialize(JsonReader jsonReader, JsonObject jsonObject) throws JsonException {
        DataPointService dataPointService = new DataPointService();
        String xid = jsonObject.getString("pointXid");
        if (!StringUtils.isEmpty(xid)) {
            try {
                DataPointVO dataPoint = dataPointService.getDataPoint(xid);
                if(dataPoint == null)
                    throw new LocalizableJsonException(new LocalizableMessage("emport.error.missingPoint", xid));
                this.pointId = dataPoint.getId();
                this.pointXid = dataPoint.getXid();
            } catch (LocalizableJsonException ex) {
                LOG.warn(ex.getMessage(), ex);
                throw ex;
            } catch (Exception ex) {
                LOG.warn(ex.getMessage(), ex);
                throw new LocalizableJsonException(new LocalizableMessage("emport.error.invalid", "xid", xid, "not null"));
            }
        } else {
            setId(jsonObject, dataPointService);
        }
    }

    private void setId(JsonObject jsonObject, DataPointService dataPointService) throws JsonException {
        Integer id = jsonObject.getInt("pointId");
        try {
            DataPointVO dataPoint = dataPointService.getDataPoint(id);
            this.pointId = dataPoint.getId();
            this.pointXid = dataPoint.getXid();
        } catch (EmptyResultDataAccessException ex) {
            LOG.warn(ex.getMessage(), ex);
            throw new LocalizableJsonException(new LocalizableMessage("emport.error.missingObject",
                    LocalizableMessage.getMessage(Common.getBundle(), "emport.dataPoint.prefix", "id", id)));
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            throw new LocalizableJsonException(new LocalizableMessage("emport.error.invalid", "id", id, "not null"));
        }
    }
}
