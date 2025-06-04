package org.scada_lts.dao;

import com.serotonin.mango.Common;
import org.scada_lts.dao.IDataSourceDAO;
import org.scada_lts.dao.pointhierarchy.IPointHierarchyDAO;
import org.scada_lts.dao.pointhierarchy.PointHierarchyDAO;
import org.scada_lts.dao.pointhierarchy.PointHierarchyPostgresDAO;
import org.scada_lts.dao.watchlist.IWatchListDAO;
import org.scada_lts.dao.watchlist.WatchListDAO;
import org.scada_lts.dao.watchlist.WatchListPostgresDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoConfiguration {

    @Bean(name = "dataSourceDAO")
    public IDataSourceDAO dataSourceDAO() {
        String dbType = Common.getEnvironmentProfile().getString("db.type", "mysql").toLowerCase();

        switch (dbType) {
            case "postgres":
                return new DataSourcePostgresDAO();
            case "mysql":
            default:
                return new DataSourceDAO();
        }
    }

    @Bean(name = "dataPointDAO")
    public IDataPointDAO dataPointDAO() {
        String dbType = Common.getEnvironmentProfile().getString("db.type", "mysql").toLowerCase();

        switch (dbType) {
            case "postgres":
                return new DataPointPostgresDAO();
            case "mysql":
            default:
                return new DataPointDAO();
        }
    }

    @Bean(name = "pointHierarchyDAO")
    public IPointHierarchyDAO pointHierarchyDAO() {
        String dbType = Common.getEnvironmentProfile().getString("db.type", "mysql").toLowerCase();

        switch (dbType) {
            case "postgres":
                return new PointHierarchyPostgresDAO();
            case "mysql":
            default:
                return new PointHierarchyDAO();
        }
    }

    @Bean(name = "watchListDAO")
    public IWatchListDAO watchListDAO() {
        String dbType = Common.getEnvironmentProfile().getString("db.type", "mysql").toLowerCase();

        switch (dbType) {
            case "postgres":
                return new WatchListPostgresDAO();
            case "mysql":
            default:
                return new WatchListDAO();
        }
    }


}
