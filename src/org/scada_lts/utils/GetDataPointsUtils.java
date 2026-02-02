package org.scada_lts.utils;

import br.org.scadabr.vo.scripting.ContextualizedScriptVO;
import com.serotonin.db.IntValuePair;
import com.serotonin.db.KeyValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.event.handlers.SetPointHandlerRT;
import com.serotonin.mango.view.View;
import com.serotonin.mango.view.component.CompoundChild;
import com.serotonin.mango.view.component.CompoundComponent;
import com.serotonin.mango.view.component.PointComponent;
import com.serotonin.mango.view.component.ViewComponent;
import com.serotonin.mango.vo.DataPointExtendedNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.link.PointLinkVO;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.vo.publish.PublishedPointVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import com.serotonin.mango.vo.report.ReportPointVO;
import com.serotonin.mango.vo.report.ReportVO;
import com.serotonin.mango.web.dwr.beans.DataPointBean;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.EventService;
import org.scada_lts.permissions.service.GetDataPointsWithAccess;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


public final class GetDataPointsUtils {

    private GetDataPointsUtils() {}

    public static List<DataPointVO> getDataPointsByDataSource(User user, int dataSourceId, DataPointService dataPointService) {
        return getDataPointsByDataSource(user, dataSourceId, point -> point, dataPointService);
    }

    public static List<DataPointBean> getDataPointsByReport(User user, ReportVO report, DataPointService dataPointService) {
        return getDataPointsByReport(user, report, DataPointBean::new, dataPointService);
    }

    public static List<DataPointBean> getDataPointsByScript(User user, ContextualizedScriptVO contextualizedScriptVO, DataPointService dataPointService) {
        return getDataPointsByScript(user, contextualizedScriptVO, DataPointBean::new, dataPointService);
    }

    public static List<DataPointBean> getDataPointsByEventHandlers(User user, List<EventHandlerVO> eventHandlers, DataPointService dataPointService) {
        return getDataPointsByEventHandlers(user, eventHandlers, DataPointBean::new, dataPointService);
    }

    public static List<DataPointBean> getDataPointsByPublishers(User user, List<PublisherVO<?>> publishers, DataPointService dataPointService) {
        return getDataPointsByPublishers(user, publishers, DataPointBean::new, dataPointService);
    }

    public static Set<DataPointBean> getDataPointsByChildren(User user, List<KeyValuePair> childPointIds, DataPointService dataPointService) {
        return new HashSet<>(getDataPointsByChildren(user, childPointIds, DataPointBean::new, dataPointService));
    }

    public static Set<DataPointBean> getDataPointsByView(User user, View view, DataPointService dataPointService) {
        return new HashSet<>(getDataPointsByView(user, view, DataPointBean::new, dataPointService));
    }

    public static Set<DataPointBean> getTargetDataPointsByPointLinks(User user, List<PointLinkVO> pointLinks, DataPointService dataPointService) {
        Set<DataPointBean> dataPoints = new HashSet<>();
        for (PointLinkVO pointLinkVO : pointLinks) {
            DataPointVO targetDataPoint = dataPointService.getDataPoint(pointLinkVO.getTargetPointId());
            if(targetDataPoint != null && targetDataPoint.getPointLocator() != null
                    && targetDataPoint.getPointLocator().isSettable()
                    && GetDataPointsWithAccess.hasDataPointSetPermission(user, targetDataPoint))
                dataPoints.add(new DataPointBean(targetDataPoint));
        }
        return dataPoints;
    }

    public static Set<DataPointBean> getSourceDataPointsByPointLinks(User user, List<PointLinkVO> pointLinks, DataPointService dataPointService) {
        Set<DataPointBean> dataPoints = new HashSet<>();
        for (PointLinkVO pointLinkVO : pointLinks) {
            DataPointVO sourceDataPoint = dataPointService.getDataPoint(pointLinkVO.getSourcePointId());
            if(sourceDataPoint != null && GetDataPointsWithAccess.hasDataPointReadPermission(user, sourceDataPoint))
                dataPoints.add(new DataPointBean(sourceDataPoint));
        }
        return dataPoints;
    }

    public static Set<DataPointBean> getDataPointsByEventHandlers() {
        User user = Common.getUser();
        Permissions.ensureAdmin(user);

        EventService eventService = new EventService();

        List<EventHandlerVO> eventHandlers = eventService.getEventHandlers();
        return new HashSet<>(GetDataPointsUtils.getDataPointsByEventHandlers(user, eventHandlers, new DataPointService()));
    }

    public static List<DataPointBean> getContextPoints(MetaPointLocatorVO locator, User user, DataPointService dataPointService) {
        return dataPointService.getDataPoints(locator.getContext().stream()
                        .map(IntValuePair::getKey)
                        .collect(Collectors.toSet()), user).stream()
                .map(DataPointBean::new)
                .collect(Collectors.toList());
    }

    private static Set<Integer> getDataPointIdsByViewComponents(View view) {
        Set<Integer> ids = new HashSet<>();
        for(ViewComponent viewComponent: view.getViewComponents()) {
            if(viewComponent instanceof CompoundComponent) {
                CompoundComponent compoundComponent = (CompoundComponent) viewComponent;
                for(CompoundChild child: compoundComponent.getChildComponents()) {
                    if(child.getViewComponent() instanceof PointComponent) {
                        PointComponent childPoint = (PointComponent) child.getViewComponent();
                        ids.add(childPoint.getDataPointId());
                    }
                }
            } else if(viewComponent instanceof PointComponent) {
                PointComponent pointComponent = (PointComponent) viewComponent;
                ids.add(pointComponent.getDataPointId());
            }
        }
        return ids;
    }

    private static Set<DataPointVO> getDataPointsByEventHandlers(List<EventHandlerVO> eventHandlers, DataPointService dataPointService, User user) {
        Set<DataPointVO> dataPoints = new HashSet<>();
        for (EventHandlerVO eventHandler : eventHandlers) {
            if(eventHandler.createRuntime() instanceof SetPointHandlerRT) {
                DataPointVO targetPoint = dataPointService.getDataPoint(eventHandler.getTargetPointId());
                DataPointVO activePoint = dataPointService.getDataPoint(eventHandler.getActivePointId());
                DataPointVO inactivePoint = dataPointService.getDataPoint(eventHandler.getInactivePointId());
                if (targetPoint != null && GetDataPointsWithAccess.hasDataPointSetPermission(user, targetPoint)) {
                    dataPoints.add(targetPoint);
                }
                if (activePoint != null && GetDataPointsWithAccess.hasDataPointReadPermission(user, activePoint)) {
                    dataPoints.add(activePoint);
                }
                if (inactivePoint != null && GetDataPointsWithAccess.hasDataPointReadPermission(user, inactivePoint)) {
                    dataPoints.add(inactivePoint);
                }
            }
        }
        return dataPoints;
    }

    private static <T> List<T> getDataPointsByChildren(User user, List<KeyValuePair> childPointIds, Function<DataPointVO, T> converter, DataPointService dataPointService) {
        Set<Integer> ids = childPointIds.stream().map(pair -> convertToInt(pair.getValue())).collect(Collectors.toSet());
        return dataPointService.getDataPoints(ids)
                .stream()
                .filter(point -> GetDataPointsWithAccess.hasDataPointReadPermission(user, point))
                .map(converter)
                .collect(Collectors.toList());
    }

    private static <T> List<T> getDataPointsByView(User user, View view, Function<DataPointVO, T> converter, DataPointService dataPointService) {
        Set<Integer> ids = getDataPointIdsByViewComponents(view);
        return dataPointService.getDataPoints(ids).stream()
                .filter(point -> GetDataPointsWithAccess.hasDataPointReadPermission(user, point))
                .map(converter)
                .collect(Collectors.toList());
    }

    private static <T> List<T> getDataPointsByReport(User user, ReportVO report, Function<DataPointVO, T> converter, DataPointService dataPointService) {
        Set<Integer> ids = report.getPoints().stream().map(ReportPointVO::getPointId).collect(Collectors.toSet());
        return dataPointService.getDataPoints(ids).stream()
                .filter(point -> GetDataPointsWithAccess.hasDataPointReadPermission(user, point))
                .map(converter)
                .collect(Collectors.toList());
    }

    private static <T> List<T> getDataPointsByScript(User user, ContextualizedScriptVO contextualizedScriptVO,
                                                    Function<DataPointVO, T> converter, DataPointService dataPointService) {
        Set<Integer> ids = new HashSet<>();
        for(IntValuePair pair : contextualizedScriptVO.getPointsOnContext()) {
            ids.add(pair.getKey());
        }
        return dataPointService.getDataPoints(ids).stream()
                .filter(point -> GetDataPointsWithAccess.hasDataPointReadPermission(user, point))
                .map(converter)
                .collect(Collectors.toList());
    }

    private static <T> List<T> getDataPointsByDataSource(User user, int dataSourceId, Function<DataPointVO, T> converter, DataPointService dataPointService) {
        if(dataSourceId == Common.NEW_ID) {
            return Collections.emptyList();
        }
        return dataPointService.getDataPoints(dataSourceId, DataPointExtendedNameComparator.instance).stream()
                .filter(point -> GetDataPointsWithAccess.hasDataPointReadPermission(user, point))
                .map(converter)
                .collect(Collectors.toList());
    }

    private static <T> List<T> getDataPointsByPublishers(User user, List<PublisherVO<?>> publishers, Function<DataPointVO, T> converter, DataPointService dataPointService) {
        Set<Integer> ids = new HashSet<>();

        for (PublisherVO<?> publisher: publishers) {
            List<PublishedPointVO> points = (List<PublishedPointVO>) publisher.getPoints();
            for(PublishedPointVO point: points) {
                ids.add(point.getDataPointId());
            }
        }
        return dataPointService.getDataPoints(ids).stream()
                .filter(dp -> dp.getPointLocator().getDataTypeId() != DataTypes.IMAGE)
                .filter(point -> GetDataPointsWithAccess.hasDataPointReadPermission(user, point))
                .sorted(DataPointExtendedNameComparator.instance)
                .peek(a -> a.setEnabled(a.isEnabled() && Common.ctx.getRuntimeManager().isDataSourceRunning(a.getDataSourceId())))
                .map(converter)
                .collect(Collectors.toList());
    }

    private static <T> List<T> getDataPointsByEventHandlers(User user, List<EventHandlerVO> eventHandlers, Function<DataPointVO, T> converter, DataPointService dataPointService) {
        if(eventHandlers.isEmpty()) {
            return Collections.emptyList();
        }
        return getDataPointsByEventHandlers(eventHandlers, dataPointService, user).stream()
                .map(converter)
                .collect(Collectors.toList());
    }

    private static int convertToInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            return 0;
        }
    }
}
