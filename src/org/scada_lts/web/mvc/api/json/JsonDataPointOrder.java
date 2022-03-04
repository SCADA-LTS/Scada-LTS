package org.scada_lts.web.mvc.api.json;

import java.util.HashMap;
import java.util.Map;

public class JsonDataPointOrder {

    private int watchListId;
    private Map<Integer, Integer> pointIds;

    public JsonDataPointOrder() {
        this.watchListId = -1;
        this.pointIds = new HashMap<>();
    }

    public int getWatchListId() {
        return watchListId;
    }

    public void setWatchListId(int watchListId) {
        this.watchListId = watchListId;
    }

    public Map<Integer, Integer> getPointIds() {
        return pointIds;
    }

    public void setPointIds(Map<Integer, Integer> pointIds) {
        this.pointIds = pointIds;
    }

    public void addPointOrder(Integer pointId, Integer order) {
        this.pointIds.put(pointId, order);
    }
}
