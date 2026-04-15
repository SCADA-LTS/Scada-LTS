package org.scada_lts.dao.watchlist;

import br.org.scadabr.vo.permission.WatchListAccess;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.WatchList;
import org.scada_lts.dao.GenericDaoCR;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.web.mvc.api.json.JsonDataPointOrder;

import java.util.List;

public interface IWatchListDAO extends GenericDaoCR<WatchList> {

    WatchList findByXId(String xid);

    List<ShareUser> getWatchListUsers(int watchListId);

    List<Integer> getPointsWatchList(int watchListId);

    void updateUsers(int userId, int watchListId);

    void update(WatchList watchList);

    void deleteWatchListPoints(int watchListId);

    void deleteWatchListUsers(int watchListId);

    void deleteWatchList(int watchListId);

    void addPointsForWatchList(WatchList watchList);

    void addWatchListUsers(WatchList watchList);

    void deleteUserFromWatchList(int watchListId, int userId);

    void deleteWatchListPoints(String dataPointIds);

    List<WatchList> selectWatchListsWithAccess(int userId, int profileId);

    List<ScadaObjectIdentifier> selectWatchListIdentifiersWithAccess(int userId, int profileId);

    List<WatchListAccess> selectWatchListPermissions(int userId);

    int[] insertPermissions(int userId, List<WatchListAccess> toInsert);

    int[] deletePermissions(int userId, List<WatchListAccess> toDelete);

    List<ShareUser> selectWatchListShareUsers(int watchListId);

    @Deprecated
    JsonDataPointOrder getDataPointOrder(Integer watchListId);

    @Deprecated
    void setDataPointOrder(JsonDataPointOrder pointOrder);

    List<ScadaObjectIdentifier> findIdentifiers();
}
