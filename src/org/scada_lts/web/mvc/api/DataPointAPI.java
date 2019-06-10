/*
 * (c) 2017 Abil'I.T. http://abilit.eu/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.scada_lts.web.mvc.api;

import br.org.scadabr.db.dao.ScriptDao;
import br.org.scadabr.vo.scripting.ContextualizedScriptVO;
import br.org.scadabr.vo.scripting.ScriptVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.ViewDao;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.cache.DataSourcePointsCache;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Arkadiusz Parafiniuk
 * E-mail: arkadiusz.parafiniuk@gmail.com
 */
@Controller
public class DataPointAPI {

    private static final Log LOG = LogFactory.getLog(DataPointAPI.class);

    DataPointService dataPointService = new DataPointService();
    DataSourceService dataSourceService = new DataSourceService();

    @RequestMapping(value = "/api/datapoint/getAll", method = RequestMethod.GET)
    public ResponseEntity<String> getAll(HttpServletRequest request) {
        LOG.info("/api/datapoint/getAll");

        try {
            User user = Common.getUser(request);

            if (user != null) {
                class DatapointJSON implements Serializable {
                    private long id;
                    private String name;
                    private String xid;

                    DatapointJSON(long id, String name, String xid) {
                        this.setId(id);
                        this.setName(name);
                        this.setXid(xid);
                    }

                    public long getId() { return id; }
                    public void setId(long id) { this.id = id; }
                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }
                    public String getXid() {
                        return xid;
                    }
                    public void setXid(String xid) {
                        this.xid = xid;
                    }
                }

                List<DataPointVO> lstDP;

                Comparator<DataPointVO> comparator = new Comparator<DataPointVO>() {
                    @Override
                    public int compare(DataPointVO o1, DataPointVO o2) {
                        return 0;
                    }
                };

                if (user.isAdmin()) {
                    lstDP = dataPointService.getDataPoints(comparator, false);
                } else {
                    return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
                }

                List<DatapointJSON> lst = new ArrayList<>();
                for (DataPointVO dp:lstDP){
                    DatapointJSON dpJ = new DatapointJSON(dp.getId(), dp.getName(), dp.getXid());
                    lst.add(dpJ);
                }

                String json = null;
                ObjectMapper mapper = new ObjectMapper();
                json = mapper.writeValueAsString(lst);

                return new ResponseEntity<String>(json,HttpStatus.OK);
            }

            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/api/dataPoint/getViewsContainsPoint/{dataPointId}", method = RequestMethod.GET)
    public ResponseEntity<List<View>>  getViewsContainsPoint(@PathVariable("dataPointId") int dataPointId, HttpServletRequest request) {
        LOG.info("/api/dataPoint/getIfContainsPoint/{dataPointId} dataPointId:"+dataPointId);

        try {
            User user = Common.getUser(request);

            if (user != null) {
                List<View> allViews = new ViewDao().getViews();
                List<View> views = new LinkedList<>();
                for (View view : allViews) {
                    view.validateViewComponents(false);
                    if (view.containsValidVisibleDataPoint(dataPointId))
                        views.add(view);
                }

                return new ResponseEntity<>(views, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/api/dataPoint/getScriptsContainsPoint/{dataPointId}", method = RequestMethod.GET)
    public ResponseEntity<List<ScriptVO>>  getScriptsContainsPoint(@PathVariable("dataPointId") int dataPointId, HttpServletRequest request) {
        LOG.info("/api/dataPoint/getScriptsContainsPoint/{dataPointId} dataPointId:"+dataPointId);

        try {
            User user = Common.getUser(request);

            if (user != null) {
                List<ScriptVO<?>> allScripts = new ScriptDao().getScripts();
                List<ScriptVO> scripts = new LinkedList<>();
                for (ScriptVO script : allScripts) {
                    ContextualizedScriptVO contextualizedScript = (ContextualizedScriptVO) script;
                    if (contextualizedScript.containsDataPoint(dataPointId)) {
                        scripts.add(script);
                    }
                }

                return new ResponseEntity<>(scripts, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/api/dataPoint/getMetadataPointsContainsPoint/{dataPointId}", method = RequestMethod.GET)
    public ResponseEntity<List<DataPointVO>>  getMetadataPointsContainsPoint(@PathVariable("dataPointId") int dataPointId, HttpServletRequest request) {
        LOG.info("/api/dataPoint/getMetadataPointsContainsPoint/{dataPointId} dataPointId:"+dataPointId);

        try {
            User user = Common.getUser(request);

            if (user != null) {
                List<DataPointVO> metaDataPoints = new ArrayList<>();

                dataSourceService.getDataSources()
                        .stream()
                        .filter(ds -> ds.getType().getId() == DataSourceVO.Type.META.getId())
                        .forEach(ds -> {
                            if (DataSourcePointsCache.getInstance().isCacheEnabled()) {
                                metaDataPoints.addAll(DataSourcePointsCache.getInstance().getDataPoints((long) ds.getId()));
                            } else {
                                metaDataPoints.addAll(dataPointService.getDataPoints(ds.getId(), null));
                            }
                        });

                List<DataPointVO> dataPoints = new ArrayList<>();

                for (DataPointVO mdp:
                     metaDataPoints) {
                    MetaPointLocatorVO mpl = mdp.getPointLocator();
                    for (IntValuePair pair:
                            mpl.getContext()) {
                        if (pair.getKey()==dataPointId) dataPoints.add(mdp);
                    }
                }

                return new ResponseEntity<>(dataPoints, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}

