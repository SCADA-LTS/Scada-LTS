package org.scada_lts.dao.pointvalues;

import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.dao.model.point.PointValueAdnnotation;

import java.util.List;

public interface IPointValueAdnnotationsCommand {
    Object[] create(PointValueAdnnotation entity);
    void update(int userId);
    void updateAnnotations(List<PointValue> values);
}
