package org.scada_lts.dao.pointvalues;

import org.scada_lts.dao.GenericDaoCR;
import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.dao.model.point.PointValueAdnnotation;

import java.util.List;

public interface IPointValueAdnnotationsDAO extends GenericDaoCR<PointValueAdnnotation> {

    void update(int pointValueId);

    void updateAnnotations(List<PointValue> pointValues);
}

