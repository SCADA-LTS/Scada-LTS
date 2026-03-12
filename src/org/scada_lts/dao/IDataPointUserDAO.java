package org.scada_lts.dao;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.util.Tuple;

import java.util.List;

public interface IDataPointUserDAO {

    List<Tuple<Integer, Integer>> getDataPointUsers(int dataPointId);

    void insert(List<Tuple<Integer, Integer>> users, int dataPointId);

    void insertPermissions(User user);

    void delete(int userId);

    void deleteWhereDataPointId(int dataPointId);

    List<DataPointAccess> selectDataPointPermissions(int userId);

    int[] insertPermissions(int userId, List<DataPointAccess> toInsert);

    int[] deletePermissions(int userId, List<DataPointAccess> toDelete);

    List<ShareUser> selectDataPointShareUsers(int dataPointId);
}
