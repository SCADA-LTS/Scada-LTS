package org.scada_lts.dao.pointvalues;

import org.scada_lts.dao.DAO;


public interface IPointValueDAO extends IPointValueQuery, IPointValueCommand, ILimitConst {

    static IPointValueDAO newCommandRespository() {
        return new PointValueDAO(DAO.getInstance().getJdbcTemp());
    }
}
