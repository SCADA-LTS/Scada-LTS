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
        return filteringByAccess(object, dataPointDAO.getDataPoints()).stream()
                .sorted(Comparator.comparing(DataPointVO::getName))
                .collect(Collectors.toList());
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User object) {
        return getObjectsWithAccess(object).stream()
                .map(DataPointVO::toIdentifier)
                .sorted(Comparator.comparing(ScadaObjectIdentifier::getName))
                .collect(Collectors.toList());
    }

    public static List<DataPointVO> filteringByAccess(User user, List<DataPointVO> dataPoints) {
        return dataPoints.stream()
                .filter(point -> Permissions.hasDataPointReadPermission(user, point))
                .collect(Collectors.toList());
    }

    public static boolean hasDataPointReadPermission(User user, DataPointVO dataPoint) {
        return Permissions.hasDataPointReadPermission(user, dataPoint);
    }
}
