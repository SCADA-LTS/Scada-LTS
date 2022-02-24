package org.scada_lts.utils;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.mango.service.WatchListService;
import org.scada_lts.web.mvc.api.json.JsonDataPointOrder;
import org.scada_lts.web.mvc.api.json.JsonWatchList;

import java.util.*;
import java.util.stream.Collectors;

import static org.scada_lts.permissions.service.GetDataPointsWithAccess.filteringByAccess;
import static org.scada_lts.permissions.service.GetDataPointsWithAccess.filteringByNoAccess;

public final class WatchListApiUtils {

    private WatchListApiUtils() {}

    public static Map<Integer, Integer> getPointIds(User user, WatchList watchList) {
        List<DataPointVO> dataPoints = filteringByAccess(user, watchList.getPointList());
        Map<Integer, Integer> result = new HashMap<>();
        for(int i=0; i < dataPoints.size(); i++) {
            result.put(dataPoints.get(i).getId(), i);
        }
        return result;
    }

    public static JsonWatchList toJsonWatchList(User user, WatchList watchList) {
        List<ScadaObjectIdentifier> pointList = filteringByAccess(user, watchList.getPointList())
                .stream()
                .map(DataPointVO::toIdentifier)
                .collect(Collectors.toList());
        return new JsonWatchList(
                watchList.getId(),
                watchList.getXid(),
                watchList.getName(),
                watchList.getUserId(),
                pointList,
                watchList.getWatchListUsers()
        );
    }

    public static List<ScadaObjectIdentifier> getWatchListIdentifiers(User user, WatchListService watchListService) {
        if (user.isAdmin()) {
            return watchListService.getWatchLists()
                    .stream()
                    .map(WatchList::toIdentifier)
                    .collect(Collectors.toList());
        }
        return watchListService.getWatchListIdentifiersWithAccess(user);
    }

    public static List<DataPointVO> toDataPointsOrdered(JsonDataPointOrder orderData, WatchList watchList) {
        Map<Integer, DataPointVO> dataPoints = toMap(watchList);
        List<DataPointVO> dataPointsOrdered = orderData.getPointIds().entrySet().stream()
                .filter(order -> dataPoints.get(order.getKey()) != null)
                .map(order -> dataPoints.remove(order.getKey()))
                .collect(Collectors.toCollection(ArrayList::new));
        dataPointsOrdered.addAll(dataPoints.values());
        return dataPointsOrdered;
    }

    public static Map<Integer, DataPointVO> toMap(WatchList watchList) {
        return watchList.getPointList().stream()
                .collect(Collectors.toMap(DataPointVO::getId, point -> point, (a, b) -> a));
    }

    public static WatchList getWatchListToSave(JsonWatchList fromUi, User user, WatchList fromBase) {
        WatchList watchListToSave = fromUi.createWatchList();
        watchListToSave.setWatchListUsers(fromBase.getWatchListUsers());
        List<DataPointVO> notAccessForUser = filteringByNoAccess(user, fromBase.getPointList());
        List<DataPointVO> dataPoints = new ArrayList<>(watchListToSave.getPointList());
        dataPoints.addAll(notAccessForUser);
        watchListToSave.getPointList().clear();
        watchListToSave.setPointList(dataPoints);
        return watchListToSave;
    }

    public static WatchList getWatchListToRead(WatchList watchListToSave, User loggedUser) {
        WatchList watchListToRead = watchListToSave.copy();
        watchListToRead.setPointList(filteringByAccess(loggedUser, watchListToSave.getPointList()));
        watchListToRead.setWatchListUsers(watchListToSave.getWatchListUsers().stream()
                .filter(share -> share.getUserId() == loggedUser.getId())
                .collect(Collectors.toList()));
        return watchListToRead;
    }
}
