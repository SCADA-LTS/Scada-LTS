package org.scada_lts.permissions.service;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GetDataPointsWithAccess implements GetObjectsWithAccess<DataPointVO, User> {

    private static final Log LOG = LogFactory.getLog(GetDataPointsWithAccess.class);

    private final DataPointDAO dataPointDAO;

    public GetDataPointsWithAccess(DataPointDAO dataPointDAO) {
        this.dataPointDAO = dataPointDAO;
    }

    @Override
    public List<DataPointVO> getObjectsWithAccess(User user) {
        if(user == null) {
            LOG.warn("user is null");
            return Collections.emptyList();
        }
        if(user.isAdmin())
            return dataPointDAO.getDataPoints();
        return dataPointDAO.selectDataPointsWithAccess(user.getId(), user.getUserProfile());
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User user) {
        if(user == null) {
            LOG.warn("user is null");
            return Collections.emptyList();
        }
        if(user.isAdmin())
            return dataPointDAO.findIdentifiers();
        return dataPointDAO.selectDataPointIdentifiersWithAccess(user.getId(), user.getUserProfile());
    }

    @Override
    public boolean hasReadPermission(User user, DataPointVO object) {
        return GetDataPointsWithAccess.hasDataPointReadPermission(user, object);
    }

    @Override
    public boolean hasSetPermission(User user, DataPointVO object) {
        return GetDataPointsWithAccess.hasDataPointSetPermission(user, object);
    }

    @Override
    public boolean hasOwnerPermission(User user, DataPointVO object) {
        return user.isAdmin();
    }

    public static List<DataPointVO> filteringByAccess(User user, List<DataPointVO> dataPoints) {
        return dataPoints.stream()
                .filter(point -> Permissions.hasDataPointReadPermission(user, point))
                .collect(Collectors.toList());
    }

    public static List<DataPointVO> filteringByNoAccess(User user, List<DataPointVO> dataPoints) {
        return dataPoints.stream()
                .filter(point -> !Permissions.hasDataPointReadPermission(user, point))
                .collect(Collectors.toList());
    }

    public static boolean hasDataPointReadPermission(User user, DataPointVO dataPoint) {
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(dataPoint == null) {
            LOG.warn("dataPoint is null");
            return false;
        }
        return Permissions.getDataPointAccessType(user, dataPoint) > ShareUser.ACCESS_NONE;
    }

    public static boolean hasDataPointSetPermission(User user, DataPointVO dataPoint) {
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(dataPoint == null) {
            LOG.warn("dataPoint is null");
            return false;
        }
        return Permissions.getDataPointAccessType(user, dataPoint) > ShareUser.ACCESS_READ;
    }
}
