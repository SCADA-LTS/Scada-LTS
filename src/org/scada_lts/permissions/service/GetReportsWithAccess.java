package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.report.ReportVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.dao.report.ReportDAO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GetReportsWithAccess implements GetObjectsWithAccess<ReportVO, User> {

    private static final Log LOG = LogFactory.getLog(GetReportsWithAccess.class);

    private final ReportDAO reportDAO;

    public GetReportsWithAccess(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    @Override
    public List<ReportVO> getObjectsWithAccess(User user) {
        if(user == null) {
            LOG.warn("user is null");
            return Collections.emptyList();
        }
        if(user.isAdmin())
            return reportDAO.getReports();
        return reportDAO.getReports().stream()
                .filter(a -> hasReportReadPermission(user, a))
                .collect(Collectors.toList());
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User user) {
        if(user == null) {
            LOG.warn("user is null");
            return Collections.emptyList();
        }
        return getObjectsWithAccess(user).stream()
                .map(a -> new ScadaObjectIdentifier(a.getId(), a.getXid(), a.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasReadPermission(User user, ReportVO object) {
        return GetReportsWithAccess.hasReportReadPermission(user, object);
    }

    @Override
    public boolean hasSetPermission(User user, ReportVO object) {
        return GetReportsWithAccess.hasReportSetPermission(user, object);
    }

    @Override
    public boolean hasOwnerPermission(User user, ReportVO object) {
        return GetReportsWithAccess.hasReportOwnerPermission(user, object);
    }

    public static boolean hasReportReadPermission(User user, ReportVO report) {
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(report == null) {
            LOG.warn("report is null");
            return false;
        }
        return user.isAdmin() || report.getUserId() == user.getId();
    }

    public static boolean hasReportSetPermission(User user, ReportVO report) {
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(report == null) {
            LOG.warn("report is null");
            return false;
        }
        return user.isAdmin() || report.getUserId() == user.getId();
    }

    public static boolean hasReportOwnerPermission(User user, ReportVO report) {
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(report == null) {
            LOG.warn("report is null");
            return false;
        }
        return user.isAdmin() || report.getUserId() == user.getId();
    }
}
