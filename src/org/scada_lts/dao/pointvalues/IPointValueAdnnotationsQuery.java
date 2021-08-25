package org.scada_lts.dao.pointvalues;

import org.scada_lts.dao.model.point.PointValueAdnnotation;

import java.util.List;

public interface IPointValueAdnnotationsQuery {
    List<PointValueAdnnotation> findAllPointValueAdnnotations();
    List<PointValueAdnnotation> filteredPointValueAdnnotations(String filter, Object[] argsFilter, long limit);
    List<PointValueAdnnotation> findAllWithUserNamePointValueAdnnotations();
    //PointValueAdnnotation findByIdPointValueAdnnotation(Object[] pk);
}
