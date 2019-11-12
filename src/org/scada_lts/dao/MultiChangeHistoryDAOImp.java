package org.scada_lts.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.multichangehistory.MultiChangeHistoryValues;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

/**
 * @author grzegorz.bylica@abilit.eu on 16.10.2019
 */
@Repository
public class MultiChangeHistoryDAOImp implements MultiChangesHistory {

    private static final Log LOG = LogFactory.getLog(MultiChangeHistoryDAOImp.class);

    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_ID = "mch.id";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_USER_ID = "mch.userId";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_USER_NAME = "mch.username";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_AND_CMP_IDENTIFICATION = "mch.viewAndComponentIdentification";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_INTERPRETED_STATE = "mch.interpretedState";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_TS = "mch.ts";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_VALUE_ID = "vmch.valueId";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_VALUE = "vmch.value";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_DATA_POINT_ID = "vmch.dataPointId";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_XID_POINT = "xidPoint";


    // @formatter:off
	private static final String SQL = "" +
            "select " +
              COLUMN_NAME_MULTI_CHANGE_HISTORY_ID + ", " +
              COLUMN_NAME_MULTI_CHANGE_HISTORY_USER_ID + ", " +
              COLUMN_NAME_MULTI_CHANGE_HISTORY_USER_NAME + ", " +
              COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_AND_CMP_IDENTIFICATION + ", " +
              COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_INTERPRETED_STATE + ", " +
              COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_TS + ", " +
              COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_VALUE_ID + ", " +
              COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_VALUE + ", " +
              COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_DATA_POINT_ID + ", " +
              "(select xid from dataPoints where id = vmch.dataPointId) as " + COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_XID_POINT + " " +
            "from " +
                "multi_changes_history mch, " +
                "values_multi_changes_history vmch " +
            "where " +
                "mch.id = vmch.multiChangesHistoryId " +
            "order by mch.id;";

	// @formatter:on
    @Override
    public List<MultiChangeHistoryValues> getHistory(String viewAndCmpId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("SQL UserComents");
        }

        try {

            List<MultiChangeHistoryValues> listUserComents = DAO.getInstance().getJdbcTemp().query(SQL, (ResultSet rs, int rownumber) -> {
                    MultiChangeHistoryValues mchv = new MultiChangeHistoryValues();
                    mchv.setId(rs.getInt(COLUMN_NAME_MULTI_CHANGE_HISTORY_ID));
                    mchv.setUserId(rs.getInt(COLUMN_NAME_MULTI_CHANGE_HISTORY_USER_ID));
                    mchv.setUserName(rs.getString(COLUMN_NAME_MULTI_CHANGE_HISTORY_USER_NAME));
                    mchv.setViewAndCmpIdentyfication(rs.getString(COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_AND_CMP_IDENTIFICATION));
                    mchv.setInterpretedState(rs.getString(COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_INTERPRETED_STATE));
                    mchv.setTs( rs.getLong(COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_TS));
                    mchv.setValueId(rs.getInt(COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_VALUE_ID));
                    mchv.setValue(rs.getString(COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_VALUE));
                    mchv.setDataPointId(rs.getInt(COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_DATA_POINT_ID));
                    mchv.setXidPoint(rs.getString(COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_XID_POINT));

                    return mchv;
            });

            return listUserComents;
        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }

    @Override
    public void prcAddCmpHistory(Integer userId, String xIdViewAndIdCmp, String interpretedState, Long scadaTime, String values) {

        DAO.getInstance().getJdbcTemp().update("call prc_add_cmp_history(?, ?, ?, ?, ?)",
                userId,
                xIdViewAndIdCmp,
                interpretedState,
                scadaTime,
                values);
    }

}
