package org.scada_lts.dao;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface IDataSourceDAO {
    List<DataSourceVO<?>> getDataSources();

    List<ScadaObjectIdentifier> getAllDataSources();

    List<DataSourceVO<?>> getDataSourcesPlc();

    List<DataSourceVO<?>> getDataSourceBaseOfName(String partOfNameDS);

    List<Integer> getDataSourceUsersId(int id);

    List<Integer> getDataSourceIdFromDsUsers(int userId);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void batchInsert(List<Integer> userIds, int toDataSourceId);

    DataSourceVO<?> getDataSource(int id);

    DataSourceVO<?> getDataSource(String xid);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int insert(DataSourceVO<?> dataSource);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void insertPermissions(User user);

    DataSourceVO<?> create(DataSourceVO<?> entity);

    @Deprecated
    List<ScadaObjectIdentifier> getSimpleList();

    List<DataSourceVO<?>> getAll();

    DataSourceVO<?> getById(int id) throws EmptyResultDataAccessException;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int update(DataSourceVO<?> dataSource);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int delete(int dataSourceId);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void deleteDataSourceUser(int userId);

    List<Integer> selectDataSourcePermissions(int userId);

    int[] insertPermissions(int userId, List<Integer> toInsert);

    int[] deletePermissions(int userId, List<Integer> toDelete);

    List<ShareUser> selectDataSourceShareUsers(int dataSourceId);

    List<DataSourceVO<?>> getDataSources(int type);

    List<DataSourceVO<?>> selectDataSourcesWithAccess(int userId, int profileId);

    List<ScadaObjectIdentifier> selectDataSourceIdentifiersWithAccess(int userId, int profileId);

    List<ScadaObjectIdentifier> findIdentifiers();
}
