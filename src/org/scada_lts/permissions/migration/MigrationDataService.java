package org.scada_lts.permissions.migration;

import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.scada_lts.mango.adapter.MangoDataPoint;
import org.scada_lts.mango.adapter.MangoDataSource;
import org.scada_lts.mango.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MigrationDataService {

    private final Map<Integer, DataPointVO> dataPoints;
    private final Map<Integer, DataSourceVO<?>> dataSources;
    private final Map<Integer, View> views;
    private final Map<Integer, WatchList> watchLists;
    private final UsersProfileService usersProfileService;


    public MigrationDataService(Map<Integer, DataPointVO> dataPoints,
                                Map<Integer, DataSourceVO<?>> dataSources,
                                Map<Integer, View> views,
                                Map<Integer, WatchList> watchLists,
                                UsersProfileService usersProfileService) {
        this.dataPoints = dataPoints;
        this.dataSources = dataSources;
        this.views = views;
        this.watchLists = watchLists;
        this.usersProfileService = usersProfileService;
    }

    public DataPointVO getDataPoint(int id) {
        return dataPoints.get(id);
    }

    public DataSourceVO<?> getDataSource(int id) {
        return dataSources.get(id);
    }

    public View getView(int id) {
        return views.get(id);
    }

    public List<View> getViews() {
        return new ArrayList<>(views.values());
    }

    public WatchList getWatchList(int id) {
        return watchLists.get(id);
    }

    public List<WatchList> getWatchLists() {
        return new ArrayList<>(watchLists.values());
    }

    public List<DataPointVO> getDataPoints() {
        return new ArrayList<>(dataPoints.values());
    }

    public List<DataSourceVO<?>> getDataSources() {
        return new ArrayList<>(dataSources.values());
    }

    public UsersProfileService getUsersProfileService() {
        return usersProfileService;
    }

    public void clear() {
        dataPoints.clear();
        dataSources.clear();
        views.clear();
        watchLists.clear();
    }
}
