package org.scada_lts.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.multichangehistory.MultiChangeHistoryValues;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

/**
 * @author grzegorz.bylica@abilit.eu on 16.10.2019
 */
@Repository
public class MultiChangeHistoryDAOImp implements MultiChangesHistory {

    private static final Log LOG = LogFactory.getLog(MultiChangeHistoryDAOImp.class);

    /* PREFIX_TABLE_MULTI_CHANGE_HIST */
    private final static String PREF_T_MCH = "mch";
    /* PREFIX_TABLE_VALUE_MULTI_CHANGE_HIST */
    private final static String PREF_T_VMCH = "vmch";

    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_ID = PREF_T_MCH + ".id";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_USER_ID = PREF_T_MCH + ".userId";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_USER_NAME = PREF_T_MCH + ".username";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_AND_CMP_IDENTIFICATION = PREF_T_MCH + ".viewAndComponentIdentification";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_INTERPRETED_STATE = PREF_T_MCH + ".interpretedState";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_TS = PREF_T_MCH + ".ts";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_VALUE_ID = PREF_T_VMCH + ".valueId";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_VALUE = PREF_T_VMCH + ".value";
    private final static String  COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_DATA_POINT_ID = PREF_T_VMCH + ".dataPointId";
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
                "(mch.id = vmch.multiChangesHistoryId) and " +
                "(mch.viewAndComponentIdentification=?) " +
            "order by mch.ts desc;";

	// @formatter:on
    @Override
    public List<MultiChangeHistoryValues> getHistory(String viewAndCmpId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("SQL MultiChangeHistoryDAOImp viewAndCmpId:" + viewAndCmpId);
        }

        try {

            List<MultiChangeHistoryValues> listUserComents = DAO.getInstance().getJdbcTemp().query(SQL, new String[]{viewAndCmpId.trim()}, (ResultSet rs, int rownumber) -> {
                    MultiChangeHistoryValues multiChangeHistoryValues = new MultiChangeHistoryValues();
                    multiChangeHistoryValues.setId(rs.getInt(COLUMN_NAME_MULTI_CHANGE_HISTORY_ID));
                    multiChangeHistoryValues.setUserId(rs.getInt(COLUMN_NAME_MULTI_CHANGE_HISTORY_USER_ID));
                    multiChangeHistoryValues.setUserName(rs.getString(COLUMN_NAME_MULTI_CHANGE_HISTORY_USER_NAME));
                    multiChangeHistoryValues.setViewAndCmpIdentyfication(rs.getString(COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_AND_CMP_IDENTIFICATION));
                    multiChangeHistoryValues.setInterpretedState(rs.getString(COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_INTERPRETED_STATE));
                    multiChangeHistoryValues.setTimeStamp( rs.getLong(COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_TS));
                    multiChangeHistoryValues.setValueId(rs.getInt(COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_VALUE_ID));
                    multiChangeHistoryValues.setValue(rs.getString(COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_VALUE));
                    multiChangeHistoryValues.setDataPointId(rs.getInt(COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_DATA_POINT_ID));
                    multiChangeHistoryValues.setXidPoint(rs.getString(COLUMN_NAME_MULTI_CHANGE_HISTORY_VIEW_XID_POINT));

                    return multiChangeHistoryValues;
            });

            return listUserComents;
        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }

    @Override
    public void addHistoryFromCMPComponent(Integer userId, String xIdViewAndIdCmp, String interpretedState, Long scadaTime, String values) {

        DAO.getInstance().getJdbcTemp().update("call prc_add_cmp_history(?, ?, ?, ?, ?)",
                userId,
                xIdViewAndIdCmp,
                interpretedState,
                scadaTime,
                values);
    }

}
