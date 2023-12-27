package org.scada_lts.mango.adapter;

import org.scada_lts.dao.model.point.PointValueAdnnotation;

import java.util.List;

public interface MangoPointValuesWithChangeOwner {

    List<PointValueAdnnotation> findAllWithAdnotationsAboutChangeOwner();
}
