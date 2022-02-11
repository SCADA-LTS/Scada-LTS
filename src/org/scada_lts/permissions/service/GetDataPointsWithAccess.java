package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GetDataPointsWithAccess implements GetObjectsWithAccess<DataPointVO, User> {

    private final DataPointDAO dataPointDAO;

    public GetDataPointsWithAccess(DataPointDAO dataPointDAO) {
        this.dataPointDAO = dataPointDAO;
    }

    @Override
    public List<DataPointVO> getObjectsWithAccess(User object) {
        return dataPointDAO.getDataPoints().stream()
                .filter(point -> Permissions.hasDataPointReadPermission(object, point))
                .sorted(Comparator.comparing(DataPointVO::getName))
                .collect(Collectors.toList());
//        if(object.isAdmin())
//            return dataPointDAO.getDataPoints();
//        return dataPointDAO.selectDataPointsWithAccess(object.getId(), object.getUserProfile());
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User object) {
        return getObjectsWithAccess(object).stream()
                .map(point -> new ScadaObjectIdentifier(point.getId(), point.getXid(), point.getName()))
                .sorted(Comparator.comparing(ScadaObjectIdentifier::getName))
                .collect(Collectors.toList());
//        if(object.isAdmin())
//            return dataPointDAO.getSimpleList();
//        return dataPointDAO.selectDataPointIdentifiersWithAccess(object.getId(), object.getUserProfile());
    }
}
