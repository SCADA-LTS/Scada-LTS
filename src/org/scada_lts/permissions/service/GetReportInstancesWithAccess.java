package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.report.ReportInstance;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.dao.report.ReportInstanceDAO;

import java.util.List;
import java.util.stream.Collectors;

public class GetReportInstancesWithAccess implements GetObjectsWithAccess<ReportInstance, User> {

    private final ReportInstanceDAO reportInstanceDAO;

    public GetReportInstancesWithAccess(ReportInstanceDAO reportInstanceDAO) {
        this.reportInstanceDAO = reportInstanceDAO;
    }

    @Override
    public List<ReportInstance> getObjectsWithAccess(User user) {
        return reportInstanceDAO.getReportInstances(user.getId());
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User user) {
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

    public static boolean hasReportInstanceReadPermission(User user, ReportInstance report) {
        return user.isAdmin() || report.getUserId() == user.getId();
    }

    public static boolean hasReportInstanceSetPermission(User user, ReportInstance report) {
        return user.isAdmin() || report.getUserId() == user.getId();
    }

    public static boolean hasReportInstanceOwnerPermission(User user, ReportInstance report) {
        return user.isAdmin() || report.getUserId() == user.getId();
    }
}
