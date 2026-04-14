package org.scada_lts.utils;

import br.org.scadabr.vo.scripting.ContextualizedScriptVO;
import com.serotonin.db.IntValuePair;
import com.serotonin.db.KeyValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.event.handlers.SetPointHandlerRT;
import com.serotonin.mango.view.View;
import com.serotonin.mango.view.component.CompoundChild;
import com.serotonin.mango.view.component.CompoundComponent;
import com.serotonin.mango.view.component.PointComponent;
import com.serotonin.mango.view.component.ViewComponent;
import com.serotonin.mango.vo.DataPointExtendedNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.link.PointLinkVO;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.vo.publish.PublisherVO;
import com.serotonin.mango.vo.report.ReportVO;
import com.serotonin.mango.web.dwr.beans.DataPointBean;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.EventService;
import org.scada_lts.permissions.service.GetDataPointsWithAccess;

import java.util.*;
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

    public static List<DataPointBean> getDataPointsByContext(MetaPointLocatorVO locator, User user, DataPointService dataPointService) {
        List<IntValuePair> context = locator.getContext();
        return getPointsByContext(context, user, dataPointService).stream().map(DataPointBean::new).collect(Collectors.toList());
    }

    public static List<DataPointVO> getDataPointsByContext(User user, List<DataPointVO> allPoints,
                                                           DataPointService dataPointService,
                                                           Comparator<DataPointVO> comparator) {

        List<DataPointVO> contextPoints = new ArrayList<>();
        for (DataPointVO dp : allPoints) {
            if(dp.getPointLocator() instanceof MetaPointLocatorVO) {
                MetaPointLocatorVO pointLocatorVO = dp.getPointLocator();
                contextPoints.addAll(getPointsByContext(pointLocatorVO.getContext(), user, dataPointService));
            }
        }
        if(comparator != null) {
            contextPoints.sort(comparator);
        }
        return contextPoints;
    }

    public static List<DataPointVO> filteringDataPointsByUser(User user, List<DataPointVO> allPoints, Comparator<DataPointVO> comparator) {

        List<DataPointVO> userPoints = new ArrayList<>();
        for (DataPointVO dp : allPoints) {
            addPointIfHasPermission(dp, user, userPoints);
        }
        if(comparator != null) {
            userPoints.sort(comparator);
        }
        return userPoints;
    }

    public static List<DataPointVO> filteringDataPointsByNumericType(User user, List<DataPointVO> allPoints,
                                                                     Comparator<DataPointVO> comparator) {
        List<DataPointVO> analogPoints = new ArrayList<>();
        for (DataPointVO dp : allPoints) {
            if (dp.getPointLocator().getDataTypeId() == DataTypes.NUMERIC) {
                addPointIfHasPermission(dp, user, analogPoints);
            }
        }
        if(comparator != null) {
            analogPoints.sort(comparator);
        }
        return analogPoints;
    }

    private static void addPointIfHasPermission(DataPointVO dp, User user, List<DataPointVO> userPoints) {
        if (GetDataPointsWithAccess.hasDataPointReadPermission(user, dp)) {
            userPoints.add(dp);
        }
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
        List<IntValuePair> context = report.getPoints()
                .stream()
                .map(reportPointVO -> new IntValuePair(reportPointVO.getPointId(), ""))
                .collect(Collectors.toList());
        return getPointsByContext(context, user, dataPointService).stream()
                .filter(point -> GetDataPointsWithAccess.hasDataPointReadPermission(user, point))
                .map(converter)
                .collect(Collectors.toList());
    }

    private static <T> List<T> getDataPointsByScript(User user, ContextualizedScriptVO contextualizedScriptVO,
                                                    Function<DataPointVO, T> converter, DataPointService dataPointService) {
        return getPointsByContext(contextualizedScriptVO.getPointsOnContext(), user, dataPointService).stream()
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
        List<IntValuePair> context = publishers.stream()
                .flatMap(a -> a.getPoints().stream())
                .map(a -> new IntValuePair(a.getDataPointId(), ""))
                .collect(Collectors.toList());
        return getPointsByContext(context, user, dataPointService).stream()
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

    private static List<DataPointVO> getPointsByContext(List<IntValuePair> context, User user, DataPointService dataPointService) {
        Set<Integer> ids = context.stream()
                .map(IntValuePair::getKey)
                .collect(Collectors.toSet());

        List<DataPointVO> points = dataPointService.getDataPoints(ids, user);
        List<DataPointVO> notExist = findNotExists(context, points);
        points.addAll(notExist);
        return points;
    }

    private static List<DataPointVO> findNotExists(List<IntValuePair> context, List<DataPointVO> points) {
        Set<Integer> fromDatabaseIds = points.stream()
                .map(DataPointVO::getId)
                .collect(Collectors.toSet());
        List<DataPointVO> notExist = new ArrayList<>();
        for(IntValuePair id: context) {
            if(!fromDatabaseIds.contains(id.getKey())) {
                DataPointVO dataPoint = new DataPointVO(-1,-1,-1);
                dataPoint.setId(id.getKey());
                dataPoint.setName("Unknown");
                dataPoint.setXid("Unknown");
                dataPoint.setPointLocator(new AbstractPointLocatorVO() {
                    @Override
                    public int getDataTypeId() {
                        return 0;
                    }

                    @Override
                    public LocalizableMessage getConfigurationDescription() {
                        return super.getDataTypeMessage();
                    }

                    @Override
                    public boolean isSettable() {
                        return false;
                    }

                    @Override
                    public PointLocatorRT createRuntime() {
                        return null;
                    }

                    @Override
                    public void validate(DwrResponseI18n response) {

                    }

                    @Override
                    public void addProperties(List<LocalizableMessage> list) {

                    }

                    @Override
                    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {

                    }
                });
                notExist.add(dataPoint);
            }
        }
        return notExist;
    }
}
