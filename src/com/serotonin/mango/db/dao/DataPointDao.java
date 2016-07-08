/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.db.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.db.IntValuePair;
import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.db.spring.GenericRowMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.DataPointExtendedNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.mango.vo.bean.PointHistoryCount;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.mango.vo.hierarchy.PointFolder;
import com.serotonin.mango.vo.hierarchy.PointHierarchy;
import com.serotonin.mango.vo.hierarchy.PointHierarchyEventDispatcher;
import com.serotonin.mango.vo.link.PointLinkVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.Tuple;
import java.sql.Connection;
import java.sql.DriverManager;

public class DataPointDao extends BaseDao {
    public DataPointDao() {
        super();
    }

    public DataPointDao(DataSource dataSource) {
        super(dataSource);
    }

    //
    //
    // Data Points
    //
    public String generateUniqueXid() {
        return generateUniqueXid(DataPointVO.XID_PREFIX, "dataPoints");
    }

    public boolean isXidUnique(String xid, int excludeId) {
        return isXidUnique(xid, excludeId, "dataPoints");
    }

    public String getExtendedPointName(int dataPointId) {
        DataPointVO vo = getDataPoint(dataPointId);
        if (vo == null)
            return "?";
        return vo.getExtendedName();
    }

    private static final String DATA_POINT_SELECT = "select dp.id, dp.xid, dp.dataSourceId, dp.data, ds.name, " //
            + "ds.xid, ds.dataSourceType " //
            + "from dataPoints dp join dataSources ds on ds.id = dp.dataSourceId ";

    public List<DataPointVO> getDataPoints(Comparator<DataPointVO> comparator, boolean includeRelationalData) {
        List<DataPointVO> dps = query(DATA_POINT_SELECT, new DataPointRowMapper());
        if (includeRelationalData)
            setRelationalData(dps);
        if (comparator != null)
            Collections.sort(dps, comparator);
        return dps;
    }

    public List<DataPointVO> getDataPoints(int dataSourceId, Comparator<DataPointVO> comparator) {
        List<DataPointVO> dps = query(DATA_POINT_SELECT + " where dp.dataSourceId=?", new Object[] { dataSourceId },
                new DataPointRowMapper());
        setRelationalData(dps);
        if (comparator != null)
            Collections.sort(dps, comparator);
        return dps;
    }

    public DataPointVO getDataPoint(int id) {
        DataPointVO dp = queryForObject(DATA_POINT_SELECT + " where dp.id=?", new Object[] { id },
                new DataPointRowMapper(), null);
        setRelationalData(dp);
        return dp;
    }

    public DataPointVO getDataPoint(String xid) {
        DataPointVO dp = queryForObject(DATA_POINT_SELECT + " where dp.xid=?", new Object[] { xid },
                new DataPointRowMapper(), null);
        setRelationalData(dp);
        return dp;
    }

    class DataPointRowMapper implements GenericRowMapper<DataPointVO> {
        public DataPointVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            DataPointVO dp;
            try {
                if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){
                    dp = (DataPointVO) SerializationHelper.readObject(rs.getBinaryStream(4));
                }
                else{
                    dp = (DataPointVO) SerializationHelper.readObject(rs.getBlob(4).getBinaryStream());
                }
            }
            catch (ShouldNeverHappenException e) {
                dp = new DataPointVO();
                dp.setName("Point configuration lost. Please recreate.");
                dp.defaultTextRenderer();
            }
            dp.setId(rs.getInt(1));
            dp.setXid(rs.getString(2));
            dp.setDataSourceId(rs.getInt(3));

            // Data source information.
            dp.setDataSourceName(rs.getString(5));
            dp.setDataSourceXid(rs.getString(6));
            dp.setDataSourceTypeId(rs.getInt(7));

            // The spinwave changes were not correctly implemented, so we need to handle potential errors here.
            if (dp.getPointLocator() == null) {
                // Use the data source tpe id to determine what type of locator is needed.
                dp.setPointLocator(new DataSourceDao().getDataSource(dp.getDataSourceId()).createPointLocator());
            }

            return dp;
        }
    }

    private void setRelationalData(List<DataPointVO> dps) {
        for (DataPointVO dp : dps)
            setRelationalData(dp);
    }

    private void setRelationalData(DataPointVO dp) {
        if (dp == null)
            return;
        setEventDetectors(dp);
        setPointComments(dp);
    }

    public void saveDataPoint(final DataPointVO dp) {
        getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // Decide whether to insert or update.
                if (dp.getId() == Common.NEW_ID) {
                    insertDataPoint(dp);
                    // Reset the point hierarchy so that the new point gets included.
                    cachedPointHierarchy = null;
                }
                else
                    updateDataPoint(dp);
            }
        });
    }

    void insertDataPoint(final DataPointVO dp) {
        // Create a default text renderer
        if (dp.getTextRenderer() == null)
            dp.defaultTextRenderer();

        // Insert the main data point record.
        if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){                
            try {
                Connection conn = DriverManager.getConnection(Common.getEnvironmentProfile().getString("db.url"),
                                            Common.getEnvironmentProfile().getString("db.username"),
                                            Common.getEnvironmentProfile().getString("db.password"));
                PreparedStatement preStmt = conn.prepareStatement("insert into dataPoints (xid, dataSourceId, data) values (?,?,?)");
                preStmt.setString(1, dp.getXid());
                preStmt.setInt(2, dp.getDataSourceId());
                preStmt.setBytes(3, SerializationHelper.writeObjectToArray(dp));
                preStmt.executeUpdate();

                ResultSet resSEQ = conn.createStatement().executeQuery("SELECT currval('datapoints_id_seq')");
                resSEQ.next();
                int id = resSEQ.getInt(1);

                conn.close(); 

                dp.setId(id);

            } catch (SQLException ex) {
                ex.printStackTrace();
                dp.setId(0);
            }
        }   
        else{
            dp.setId(doInsert("insert into dataPoints (xid, dataSourceId, data) values (?,?,?)", new Object[] {
                    dp.getXid(), dp.getDataSourceId(), SerializationHelper.writeObject(dp) }, new int[] { Types.VARCHAR,
                    Types.INTEGER, Common.getEnvironmentProfile().getString("db.type").equals("postgres") ? Types.BINARY: Types.BLOB }));
        }
        // Save the relational information.
        saveEventDetectors(dp);

        AuditEventType.raiseAddedEvent(AuditEventType.TYPE_DATA_POINT, dp);
    }

    void updateDataPoint(final DataPointVO dp) {
        DataPointVO old = getDataPoint(dp.getId());

        if (old.getPointLocator().getDataTypeId() != dp.getPointLocator().getDataTypeId())
            // Delete any point values where data type doesn't match the vo, just in case the data type was changed.
            // Only do this if the data type has actually changed because it is just really slow if the database is
            // big or busy.
            new PointValueDao().deletePointValuesWithMismatchedType(dp.getId(), dp.getPointLocator().getDataTypeId());

        // Save the VO information.
        updateDataPointShallow(dp);

        AuditEventType.raiseChangedEvent(AuditEventType.TYPE_DATA_POINT, old, dp);

        // Save the relational information.
        saveEventDetectors(dp);
    }

    public void updateDataPointShallow(final DataPointVO dp) {
        ejt.update("update dataPoints set xid=?, data=? where id=?",
                new Object[] { dp.getXid(), SerializationHelper.writeObject(dp), dp.getId() }, new int[] {
                        Types.VARCHAR, Common.getEnvironmentProfile().getString("db.type").equals("postgres") ? Types.BINARY: Types.BLOB, Types.INTEGER });
    }

    public void deleteDataPoints(final int dataSourceId) {
        List<DataPointVO> old = getDataPoints(dataSourceId, null);
        for (DataPointVO dp : old)
            beforePointDelete(dp.getId());

        for (DataPointVO dp : old)
            deletePointHistory(dp.getId());

        getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
            @SuppressWarnings("synthetic-access")
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                List<Integer> pointIds = queryForList("select id from dataPoints where dataSourceId=?",
                        new Object[] { dataSourceId }, Integer.class);
                if (pointIds.size() > 0)
                    deleteDataPointImpl(createDelimitedList(new HashSet<Integer>(pointIds), ",", null));
            }
        });

        for (DataPointVO dp : old)
            AuditEventType.raiseDeletedEvent(AuditEventType.TYPE_DATA_POINT, dp);
    }

    public void deleteDataPoint(final int dataPointId) {
        DataPointVO dp = getDataPoint(dataPointId);
        if (dp != null) {
            beforePointDelete(dataPointId);
            deletePointHistory(dataPointId);
            getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    deleteDataPointImpl(Integer.toString(dataPointId));
                }
            });

            AuditEventType.raiseDeletedEvent(AuditEventType.TYPE_DATA_POINT, dp);
        }
    }

    private void beforePointDelete(int dataPointId) {
        for (PointLinkVO link : new PointLinkDao().getPointLinksForPoint(dataPointId))
            Common.ctx.getRuntimeManager().deletePointLink(link.getId());
    }

    void deletePointHistory(int dataPointId) {
        Object[] p = new Object[] { dataPointId };
        long min = ejt.queryForLong("select min(ts) from pointValues where dataPointId=?", p);
        long max = ejt.queryForLong("select max(ts) from pointValues where dataPointId=?", p);
        deletePointHistory(dataPointId, min, max);
    }

    void deletePointHistory(int dataPointId, long min, long max) {
        while (true) {
            try {
                ejt.update("delete from pointValues where dataPointId=? and ts <= ?", new Object[] { dataPointId, max });
                break;
            }
            catch (UncategorizedSQLException e) {
                if ("The total number of locks exceeds the lock table size".equals(e.getSQLException().getMessage())) {
                    long mid = (min + max) >> 1;
                    deletePointHistory(dataPointId, min, mid);
                    min = mid;
                }
                else
                    throw e;
            }
        }
    }

    void deleteDataPointImpl(String dataPointIdList) {
        dataPointIdList = "(" + dataPointIdList + ")";
        ejt.update("delete from eventHandlers where eventTypeId=" + EventType.EventSources.DATA_POINT
                + " and eventTypeRef1 in " + dataPointIdList);
        ejt.update("delete from userComments where commentType=2 and typeKey in " + dataPointIdList);
        ejt.update("delete from pointEventDetectors where dataPointId in " + dataPointIdList);
        ejt.update("delete from dataPointUsers where dataPointId in " + dataPointIdList);
        ejt.update("delete from watchListPoints where dataPointId in " + dataPointIdList);
        ejt.update("delete from dataPoints where id in " + dataPointIdList);

        cachedPointHierarchy = null;
    }

    //
    //
    // Event detectors
    //
    public int getDataPointIdFromDetectorId(int pedId) {
        return ejt.queryForInt("select dataPointId from pointEventDetectors where id=?", new Object[] { pedId });
    }

    public String getDetectorXid(int pedId) {
        return queryForObject("select xid from pointEventDetectors where id=?", new Object[] { pedId }, String.class,
                null);
    }

    public int getDetectorId(String pedXid, int dataPointId) {
        return ejt.queryForInt("select id from pointEventDetectors where xid=? and dataPointId=?", new Object[] {
                pedXid, dataPointId }, -1);
    }

    public String generateEventDetectorUniqueXid(int dataPointId) {
        String xid = Common.generateXid(PointEventDetectorVO.XID_PREFIX);
        while (!isEventDetectorXidUnique(dataPointId, xid, -1))
            xid = Common.generateXid(PointEventDetectorVO.XID_PREFIX);
        return xid;
    }

    public boolean isEventDetectorXidUnique(int dataPointId, String xid, int excludeId) {
        return ejt.queryForInt("select count(*) from pointEventDetectors where dataPointId=? and xid=? and id<>?",
                new Object[] { dataPointId, xid, excludeId }) == 0;
    }

    private void setEventDetectors(DataPointVO dp) {
        dp.setEventDetectors(getEventDetectors(dp));
    }

    private List<PointEventDetectorVO> getEventDetectors(DataPointVO dp) {
        return query(
                "select id, xid, alias, detectorType, alarmLevel, stateLimit, duration, durationType, binaryState, " //
                        + "  multistateState, changeCount, alphanumericState, weight " //
                        + "from pointEventDetectors " //
                        + "where dataPointId=? " // 
                        + "order by id", new Object[] { dp.getId() }, new EventDetectorRowMapper(dp));
    }

    class EventDetectorRowMapper implements GenericRowMapper<PointEventDetectorVO> {
        private final DataPointVO dp;

        public EventDetectorRowMapper(DataPointVO dp) {
            this.dp = dp;
        }

        public PointEventDetectorVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            PointEventDetectorVO detector = new PointEventDetectorVO();
            int i = 0;
            detector.setId(rs.getInt(++i));
            detector.setXid(rs.getString(++i));
            detector.setAlias(rs.getString(++i));
            detector.setDetectorType(rs.getInt(++i));
            detector.setAlarmLevel(rs.getInt(++i));
            detector.setLimit(rs.getDouble(++i));
            detector.setDuration(rs.getInt(++i));
            detector.setDurationType(rs.getInt(++i));
            detector.setBinaryState(charToBool(rs.getString(++i)));
            detector.setMultistateState(rs.getInt(++i));
            detector.setChangeCount(rs.getInt(++i));
            detector.setAlphanumericState(rs.getString(++i));
            detector.setWeight(rs.getDouble(++i));
            detector.njbSetDataPoint(dp);
            return detector;
        }
    }

    private void saveEventDetectors(DataPointVO dp) {
        // Get the ids of the existing detectors for this point.
        final List<PointEventDetectorVO> existingDetectors = getEventDetectors(dp);

        // Insert or update each detector in the point.
        for (PointEventDetectorVO ped : dp.getEventDetectors()) {
            if (ped.getId() < 0) {
                // Insert the record.
                if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){                
                    try {
                        Connection conn = DriverManager.getConnection(Common.getEnvironmentProfile().getString("db.url"),
                                                    Common.getEnvironmentProfile().getString("db.username"),
                                                    Common.getEnvironmentProfile().getString("db.password"));
                        PreparedStatement preStmt = conn.prepareStatement("insert into pointEventDetectors "
                                    + "  (xid, alias, dataPointId, detectorType, alarmLevel, stateLimit, duration, durationType, "
                                    + "  binaryState, multistateState, changeCount, alphanumericState, weight) "
                                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?)");
                     
                        preStmt.setString(1, ped.getXid());
                        preStmt.setString(2, ped.getAlias());
                        preStmt.setInt(3, dp.getId());
                        preStmt.setInt(4, ped.getDetectorType());
                        preStmt.setInt(5, ped.getAlarmLevel());
                        preStmt.setDouble(6, ped.getLimit());
                        preStmt.setInt(7, ped.getDuration());
                        preStmt.setInt(8, ped.getDurationType());
                        preStmt.setString(9, boolToChar(ped.isBinaryState()));
                        preStmt.setInt(10, ped.getMultistateState());
                        preStmt.setInt(11, ped.getChangeCount());
                        preStmt.setString(12, ped.getAlphanumericState());
                        preStmt.setDouble(13, ped.getWeight());

                        preStmt.executeUpdate();

                        ResultSet resSEQ = conn.createStatement().executeQuery("SELECT currval('pointeventdetectors_id_seq')");
                        resSEQ.next();
                        int id = resSEQ.getInt(1);

                        conn.close(); 

                        ped.setId(id);

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        ped.setId(0);
                    }
                }      
                else{
                    ped.setId(doInsert(
                            "insert into pointEventDetectors "
                                    + "  (xid, alias, dataPointId, detectorType, alarmLevel, stateLimit, duration, durationType, "
                                    + "  binaryState, multistateState, changeCount, alphanumericState, weight) "
                                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?)",
                            new Object[] { ped.getXid(), ped.getAlias(), dp.getId(), ped.getDetectorType(),
                                    ped.getAlarmLevel(), ped.getLimit(), ped.getDuration(), ped.getDurationType(),
                                    boolToChar(ped.isBinaryState()), ped.getMultistateState(), ped.getChangeCount(),
                                    ped.getAlphanumericState(), ped.getWeight() }, new int[] { Types.VARCHAR,
                                    Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.DOUBLE,
                                    Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.INTEGER,
                                    Types.VARCHAR, Types.DOUBLE }));
                }
                AuditEventType.raiseAddedEvent(AuditEventType.TYPE_POINT_EVENT_DETECTOR, ped);
            }
            else {
                PointEventDetectorVO old = removeFromList(existingDetectors, ped.getId());

                ejt.update(
                        "update pointEventDetectors set xid=?, alias=?, alarmLevel=?, stateLimit=?, duration=?, "
                                + "  durationType=?, binaryState=?, multistateState=?, changeCount=?, alphanumericState=?, "
                                + "  weight=? " + "where id=?",
                        new Object[] { ped.getXid(), ped.getAlias(), ped.getAlarmLevel(), ped.getLimit(),
                                ped.getDuration(), ped.getDurationType(), boolToChar(ped.isBinaryState()),
                                ped.getMultistateState(), ped.getChangeCount(), ped.getAlphanumericState(),
                                ped.getWeight(), ped.getId() }, new int[] { Types.VARCHAR, Types.VARCHAR,
                                Types.INTEGER, Types.DOUBLE, Types.INTEGER, Types.INTEGER, Types.VARCHAR,
                                Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.DOUBLE, Types.INTEGER });

                AuditEventType.raiseChangedEvent(AuditEventType.TYPE_POINT_EVENT_DETECTOR, old, ped);
            }
        }

        // Delete detectors for any remaining ids in the list of existing detectors.
        for (PointEventDetectorVO ped : existingDetectors) {
            ejt.update("delete from eventHandlers " + "where eventTypeId=" + EventType.EventSources.DATA_POINT
                    + " and eventTypeRef1=? and eventTypeRef2=?", new Object[] { dp.getId(), ped.getId() });
            ejt.update("delete from pointEventDetectors where id=?", new Object[] { ped.getId() });

            AuditEventType.raiseDeletedEvent(AuditEventType.TYPE_POINT_EVENT_DETECTOR, ped);
        }
    }

    private PointEventDetectorVO removeFromList(List<PointEventDetectorVO> list, int id) {
        for (PointEventDetectorVO ped : list) {
            if (ped.getId() == id) {
                list.remove(ped);
                return ped;
            }
        }
        return null;
    }

    public void copyPermissions(final int fromDataPointId, final int toDataPointId) {
        final List<Tuple<Integer, Integer>> ups = query(
                "select userId, permission from dataPointUsers where dataPointId=?", new Object[] { fromDataPointId },
                new GenericRowMapper<Tuple<Integer, Integer>>() {
                    @Override
                    public Tuple<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new Tuple<Integer, Integer>(rs.getInt(1), rs.getInt(2));
                    }
                });

        ejt.batchUpdate("insert into dataPointUsers values (?,?,?)", new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return ups.size();
            }

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, toDataPointId);
                ps.setInt(2, ups.get(i).getElement1());
                ps.setInt(3, ups.get(i).getElement2());
            }
        });
    }

    //
    //
    // Point comments
    //
    private static final String POINT_COMMENT_SELECT = UserCommentRowMapper.USER_COMMENT_SELECT
            + "where uc.commentType= " + UserComment.TYPE_POINT + " and uc.typeKey=? " + "order by uc.ts";

    private void setPointComments(DataPointVO dp) {
        dp.setComments(query(POINT_COMMENT_SELECT, new Object[] { dp.getId() }, new UserCommentRowMapper()));
    }

    //
    //
    // Point hierarchy
    //
    static PointHierarchy cachedPointHierarchy;

    public PointHierarchy getPointHierarchy() {
        if (cachedPointHierarchy == null) {
            final Map<Integer, List<PointFolder>> folders = new HashMap<Integer, List<PointFolder>>();

            // Get the folder list.
            ejt.query("select id, parentId, name from pointHierarchy", new RowCallbackHandler() {
                public void processRow(ResultSet rs) throws SQLException {
                    PointFolder f = new PointFolder(rs.getInt(1), rs.getString(3));
                    int parentId = rs.getInt(2);
                    List<PointFolder> folderList = folders.get(parentId);
                    if (folderList == null) {
                        folderList = new LinkedList<PointFolder>();
                        folders.put(parentId, folderList);
                    }
                    folderList.add(f);
                }
            });

            // Create the folder hierarchy.
            PointHierarchy ph = new PointHierarchy();
            addFoldersToHeirarchy(ph, 0, folders);

            // Add data points.
            List<DataPointVO> points = getDataPoints(DataPointExtendedNameComparator.instance, false);
            for (DataPointVO dp : points)
                ph.addDataPoint(dp.getId(), dp.getPointFolderId(), dp.getExtendedName());

            cachedPointHierarchy = ph;
        }

        return cachedPointHierarchy;
    }

    private void addFoldersToHeirarchy(PointHierarchy ph, int parentId, Map<Integer, List<PointFolder>> folders) {
        List<PointFolder> folderList = folders.remove(parentId);
        if (folderList == null)
            return;

        for (PointFolder f : folderList) {
            ph.addPointFolder(f, parentId);
            addFoldersToHeirarchy(ph, f.getId(), folders);
        }
    }

    public void savePointHierarchy(final PointFolder root) {
        final ExtendedJdbcTemplate ejt2 = ejt;
        getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // Dump the hierarchy table.
                ejt2.update("delete from pointHierarchy");

                // Save the point folders.
                savePointFolder(root, 0);
            }
        });

        // Save the point folders. This is not done in the transaction because it can cause deadlocks in Derby.
        savePointsInFolder(root);

        cachedPointHierarchy = null;
        cachedPointHierarchy = getPointHierarchy();
        PointHierarchyEventDispatcher.firePointHierarchySaved(root);
    }

    void savePointFolder(PointFolder folder, int parentId) {
        // Save the folder.
        if (folder.getId() == Common.NEW_ID){
            if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){                
                try {
                    //id = doInsert(EVENT_INSERT, args, EVENT_INSERT_TYPES);
                    Connection conn = DriverManager.getConnection(Common.getEnvironmentProfile().getString("db.url"),
                                                Common.getEnvironmentProfile().getString("db.username"),
                                                Common.getEnvironmentProfile().getString("db.password"));
                    PreparedStatement preStmt = conn.prepareStatement("insert into pointHierarchy (parentId, name) values (?,?)");
                    preStmt.setInt(1, parentId);
                    preStmt.setString(2, folder.getName());
                    preStmt.executeUpdate();

                    ResultSet resSEQ = conn.createStatement().executeQuery("SELECT currval('pointhierarchy_id_seq')");
                    resSEQ.next();
                    int id = resSEQ.getInt(1);

                    conn.close(); 

                    folder.setId(id);

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    folder.setId(0);
                }
            }    
            else{
                folder.setId(doInsert("insert into pointHierarchy (parentId, name) values (?,?)",
                        new Object[] { parentId, folder.getName() }));                
            }
        }
        else if (folder.getId() != 0){
            ejt.update("insert into pointHierarchy (id, parentId, name) values (?,?,?)", new Object[] { folder.getId(),
                    parentId, folder.getName() });
        }

        // Save the subfolders
        for (PointFolder sf : folder.getSubfolders())
            savePointFolder(sf, folder.getId());
    }

    void savePointsInFolder(PointFolder folder) {
        // Save the points in the subfolders
        for (PointFolder sf : folder.getSubfolders())
            savePointsInFolder(sf);

        // Update the folder references in the points.
        DataPointVO dp;
        for (IntValuePair p : folder.getPoints()) {
            dp = getDataPoint(p.getKey());
            // The point may have been deleted while editing the hierarchy.
            if (dp != null) {
                dp.setPointFolderId(folder.getId());
                updateDataPointShallow(dp);
            }
        }
    }

    public List<PointHistoryCount> getTopPointHistoryCounts() {
        List<PointHistoryCount> counts = query(
                "select dataPointId, count(*) from pointValues group by dataPointId order by 2 desc",
                new GenericRowMapper<PointHistoryCount>() {
                    @Override
                    public PointHistoryCount mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PointHistoryCount c = new PointHistoryCount();
                        c.setPointId(rs.getInt(1));
                        c.setCount(rs.getInt(2));
                        return c;
                    }
                });

        List<DataPointVO> points = getDataPoints(DataPointExtendedNameComparator.instance, false);

        // Collate in the point names.
        for (PointHistoryCount c : counts) {
            for (DataPointVO point : points) {
                if (point.getId() == c.getPointId()) {
                    c.setPointName(point.getExtendedName());
                    break;
                }
            }
        }

        return counts;
    }
}
