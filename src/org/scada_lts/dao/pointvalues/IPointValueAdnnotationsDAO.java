package org.scada_lts.dao.pointvalues;

import org.scada_lts.dao.DAO;

public interface IPointValueAdnnotationsDAO extends IPointValueAdnnotationsQuery,
        IPointValueAdnnotationsCommand, ILimitConst {

    static IPointValueAdnnotationsDAO newCommandRepository() {
        return new PointValueAdnnotationsDAO(DAO.getInstance().getJdbcTemp());
    }
}
