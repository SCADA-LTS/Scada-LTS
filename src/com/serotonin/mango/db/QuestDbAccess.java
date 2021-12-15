package com.serotonin.mango.db;

import com.serotonin.db.spring.ExtendedJdbcTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.scada_lts.dao.migration.query.questdb.V2_7_1_3__CreatePointValuesDenormalized;

import javax.servlet.ServletContext;

public class QuestDbAccess extends PostgreSQLAccess {

    private static final Log LOG = LogFactory.getLog(QuestDbAccess.class);

    public QuestDbAccess(ServletContext ctx) {
        super(ctx);
    }

    public QuestDbAccess(ServletContext ctx, String dbPrefix) {
        super(ctx, dbPrefix);
    }

    @Override
    protected boolean newDatabaseCheck(ExtendedJdbcTemplate ejt) {
        try {
            new V2_7_1_3__CreatePointValuesDenormalized().migrate(null);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }
}
