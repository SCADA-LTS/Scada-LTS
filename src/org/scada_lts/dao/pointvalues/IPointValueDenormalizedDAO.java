package org.scada_lts.dao.pointvalues;

import org.scada_lts.dao.DAO;

public interface IPointValueDenormalizedDAO extends IPointValueDAO, IPointValueAdnnotationsDAO, ILimitConst {

    static IPointValueDenormalizedDAO newQueryRespository() {
        return new PointValueDenormalizedDAO(DAO.query().getJdbcTemp());
    }
}
