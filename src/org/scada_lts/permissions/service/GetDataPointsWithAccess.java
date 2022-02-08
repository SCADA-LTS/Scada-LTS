package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;

public class GetDataPointsWithAccess implements GetObjectsWithAccess<DataPointVO, User> {

    private final DataPointDAO dataPointDAO;

    public GetDataPointsWithAccess(DataPointDAO dataPointDAO) {
        this.dataPointDAO = dataPointDAO;
    }

    @Override
    public List<DataPointVO> getObjectsWithAccess(User object) {
        if(object.isAdmin())
            return dataPointDAO.getDataPoints();
        return dataPointDAO.selectDataPointsWithAccess(object.getId(), object.getUserProfile());
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User object) {
        if(object.isAdmin())
            return dataPointDAO.getSimpleList();
        return dataPointDAO.selectDataPointIdentifiersWithAccess(object.getId(), object.getUserProfile());
    }
}
