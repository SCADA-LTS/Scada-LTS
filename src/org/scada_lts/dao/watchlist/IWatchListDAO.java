package org.scada_lts.dao.watchlist;

import br.org.scadabr.vo.permission.WatchListAccess;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.WatchList;
import org.scada_lts.dao.GenericDaoCR;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.web.mvc.api.json.JsonDataPointOrder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface IWatchListDAO extends GenericDaoCR<WatchList> {
    @Override
    List<WatchList> findAll();

    @Override
    WatchList findById(Object[] pk);

    WatchList findByXId(String xid);

    @Override
    List<WatchList> filtered(String filter, Object[] argsFilter, long limit);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    Object[] create(WatchList entity);

    List<ShareUser> getWatchListUsers(int watchListId);

    List<Integer> getPointsWatchList(int watchListId);

    void updateUsers(int userId, int watchListId);

    void update(WatchList watchList);

    //TODO rewrite because update is not delete All and add all.
    void deleteWatchListPoints(int watchListId);

    //TODO rewrite
    void deleteWatchListUsers(int watchListId);

    void deleteWatchList(int watchListId);

    //TODO rewrite
    void addPointsForWatchList(WatchList watchList);

    //TODO rewrite
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
