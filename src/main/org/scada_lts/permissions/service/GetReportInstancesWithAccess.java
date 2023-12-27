package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.PermissionException;
import com.serotonin.mango.vo.report.ReportInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.dao.report.ReportInstanceDAO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GetReportInstancesWithAccess implements GetObjectsWithAccess<ReportInstance, User> {

    private static final Log LOG = LogFactory.getLog(GetReportInstancesWithAccess.class);

    private final ReportInstanceDAO reportInstanceDAO;

    public GetReportInstancesWithAccess(ReportInstanceDAO reportInstanceDAO) {
        this.reportInstanceDAO = reportInstanceDAO;
    }

    @Override
    public List<ReportInstance> getObjectsWithAccess(User user) {
        if(user == null) {
            LOG.warn("user is null");
            return Collections.emptyList();
        }
        if(user.isAdmin())
            return reportInstanceDAO.getReportInstances();
        return reportInstanceDAO.getReportInstances().stream()
                .filter(a -> hasReportInstanceReadPermission(user, a))
                .collect(Collectors.toList());
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User user) {
        if(user == null) {
            LOG.warn("user is null");
            return Collections.emptyList();
        }
        return getObjectsWithAccess(user).stream()
                .map(a -> new ScadaObjectIdentifier(a.getId(), "unknown", a.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasReadPermission(User user, ReportInstance object) {
        return GetReportInstancesWithAccess.hasReportInstanceReadPermission(user, object);
    }

    @Override
    public boolean hasSetPermission(User user, ReportInstance object) {
        return GetReportInstancesWithAccess.hasReportInstanceSetPermission(user, object);
    }

    @Override
    public boolean hasOwnerPermission(User user, ReportInstance object) {
        return GetReportInstancesWithAccess.hasReportInstanceOwnerPermission(user, object);
    }

    public static boolean hasReportInstanceReadPermission(User user, ReportInstance reportInstance) {
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(reportInstance == null) {
            LOG.warn("report is null");
            return false;
        }
        return user.isAdmin() || reportInstance.getUserId() == user.getId();
    }

    public static boolean hasReportInstanceSetPermission(User user, ReportInstance reportInstance) {
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(reportInstance == null) {
            LOG.warn("report is null");
            return false;
        }
        return user.isAdmin() || reportInstance.getUserId() == user.getId();
    }

    public static boolean hasReportInstanceOwnerPermission(User user, ReportInstance reportInstance) {
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(reportInstance == null) {
            LOG.warn("report is null");
            return false;
        }
        return user.isAdmin() || reportInstance.getUserId() == user.getId();
    }

    public static void ensureReportInstanceReadPermission(User user, ReportInstance reportInstance) {
        if(!hasReportInstanceReadPermission(user, reportInstance)) {
            throw new PermissionException("User does not have permission to access the report instance", user);
        }
    }

    public static void ensureReportInstanceSetPermission(User user, ReportInstance reportInstance) {
        if(!hasReportInstanceSetPermission(user, reportInstance)) {
            throw new PermissionException("User does not have permission to access the report instance", user);
        }
    }

    public static void ensureReportInstanceOwnerPermission(User user, ReportInstance reportInstance) {
        if(!hasReportInstanceOwnerPermission(user, reportInstance)) {
            throw new PermissionException("User does not have permission to access the report instance", user);
        }
    }
}
