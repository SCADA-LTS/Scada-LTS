/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
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

package org.scada_lts.mango.adapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.cache.PointHierarchyCache;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyDataSource;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;

/**
 * Adapter for MangoPointHierarchyCacheable
 *
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class MangoPointHierarchy implements MangoPointHierarchyCacheable {

    private static final Log LOG = LogFactory.getLog(MangoPointHierarchy.class);

    private static MangoPointHierarchy instance = null;

    private MangoPointHierarchy() {
        //
    }

    public static MangoPointHierarchyCacheable getInst() {
        if (instance == null) {
            instance = new MangoPointHierarchy();
        }
        return instance;
    }

    @Override
    public void addDataPoint(DataPointVO dp) {
        try {
            if (LOG.isTraceEnabled()) {
                LOG.trace("dp:" + dp.toString());
                PointHierarchyCache.getInstance().printTreeInCash("_", 0);
            }

            //TODO nie ma nazwy datasource i niema info o datasource
            PointHierarchyDataSource phds = new PointHierarchyDataSource(dp.getDataSourceId(), dp.getDataSourceName(), dp.getDataSourceXid(), String.valueOf(dp.getDataSourceTypeId()));
            PointHierarchyNode phn = new PointHierarchyNode(
                    dp.getId(),
                    dp.getXid(),
                    dp.getPointFolderId(),
                    dp.getName(),
                    false,
                    phds);

            if (phn.isFolder()) {
                throw new Exception("DDDD");
            }
            if (phn.isFolder()) {
                PointHierarchyCache.getInstance().addFolder(phn);
            } else {
                PointHierarchyCache.getInstance().addPoint(phn);
            }

            if (LOG.isTraceEnabled()) {
                LOG.trace("after");
                PointHierarchyCache.getInstance().printTreeInCash("_", 0);
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    @Override
    public void updateDataPoint(DataPointVO dp) {

        try {
            if (LOG.isTraceEnabled()) {
                LOG.trace("update dp:" + dp.toString());
                PointHierarchyCache.getInstance().printTreeInCash("_", 0);
            }
            PointHierarchyCache.getInstance().edit(dp.getPointFolderId(), dp.getId(), dp.getName(), false);
            if (LOG.isTraceEnabled()) {
                LOG.trace("after");
                PointHierarchyCache.getInstance().printTreeInCash("_", 0);
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    @Override
    public void deleteDataPoint(String idsSeparateComma) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("idsSeparateComma:" + idsSeparateComma);
        }
        String[] aIdsStr = idsSeparateComma.split(",");
        int[] aIdsInt = new int[aIdsStr.length];
        for (int i = 0; i < aIdsStr.length; i++) {
            aIdsInt[i] = Integer.valueOf(aIdsStr[i]);
        }
        try {
            PointHierarchyCache.getInstance().delete(aIdsInt);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    @Override
    public void changeDataSource(DataSourceVO<?> vo) {
        PointHierarchyDataSource phds = new PointHierarchyDataSource();
        phds.setId(vo.getId());
        phds.setName(vo.getName());
        phds.setXid(vo.getXid());
        phds.setDataSourceType(vo.getTypeKey());
        try {
            PointHierarchyCache.getInstance().update(phds);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

}
