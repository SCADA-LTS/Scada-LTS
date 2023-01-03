package com.serotonin.mango.rt.event.type;

import com.serotonin.mango.util.ChangeComparable;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.impl.PointEventDetectorDAO;

import java.util.List;
import java.util.Optional;


public final class AuditEventUtils {

    private static final Log LOG = LogFactory.getLog(AuditEventUtils.class);

    private AuditEventUtils() {}

    public static void raiseChangedDataSourceEvent(DataSourceVO<?> from, DataSourceVO<?> to) {
        try {
            AuditEventUtils.raiseChangedEvent(AuditEventType.TYPE_DATA_SOURCE, from, (ChangeComparable<DataSourceVO<?>>) to);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
        }
    }

    public static void raiseAddedEvent(int auditEventTypeId, ChangeComparable<?> o) {
        try {
            AuditEventType.raiseAddedEvent(auditEventTypeId, o);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
        }
    }

    public static <T> void raiseChangedEvent(int auditEventTypeId, T from, ChangeComparable<T> to) {
        try {
            AuditEventType.raiseChangedEvent(auditEventTypeId, from, to);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
        }
    }

    public static void raiseDeletedEvent(int auditEventTypeId, ChangeComparable<?> o) {
        try {
            AuditEventType.raiseDeletedEvent(auditEventTypeId, o);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
        }
    }

    public static void raiseAuditDetectorEvent(DataPointVO point, PointEventDetectorVO ped, PointEventDetectorDAO detectorDAO) {
        try {
            List<PointEventDetectorVO> peds = detectorDAO.getPointEventDetectors(point);
            getPointEventDetector(peds, ped).map(fromPed -> {
                AuditEventType.raiseChangedEvent(AuditEventType.TYPE_POINT_EVENT_DETECTOR, fromPed, ped);
                return fromPed;
            }).orElseGet(() -> {
                AuditEventUtils.raiseAddedEvent(AuditEventType.TYPE_POINT_EVENT_DETECTOR, ped);
                return ped;
            });
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
        }
    }

    private static Optional<PointEventDetectorVO> getPointEventDetector(List<PointEventDetectorVO> peds, PointEventDetectorVO ped) {
        return peds.stream().filter(a -> a.getId() == ped.getId()).findAny();
    }
}
