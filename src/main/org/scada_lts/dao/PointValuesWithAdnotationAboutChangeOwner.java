package org.scada_lts.dao;

import java.util.List;

public interface PointValuesWithAdnotationAboutChangeOwner<T> {
    List<T> findAllWithAdnotationsAboutChangeOwner();
}
