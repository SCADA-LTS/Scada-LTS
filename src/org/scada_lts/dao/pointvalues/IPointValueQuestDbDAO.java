package org.scada_lts.dao.pointvalues;

import org.scada_lts.dao.DAO;

public interface IPointValueQuestDbDAO extends IPointValueDAO, ILimitConst{

    static IPointValueQuestDbDAO newQueryRespository() {
        return new PointValueQuestDbDAO(DAO.query().getJdbcTemp());
    }

    long dropPartition(long time, int dataPointId);
}
