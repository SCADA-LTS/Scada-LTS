/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.db.dao;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.adapter.MangoDataPoint;
import org.scada_lts.mango.service.DataPointService;
import org.springframework.dao.EmptyResultDataAccessException;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.bean.PointHistoryCount;
import com.serotonin.mango.vo.hierarchy.PointFolder;
import com.serotonin.mango.vo.hierarchy.PointHierarchy;

public class DataPointDao {

    private MangoDataPoint dataPointService = new DataPointService();
    private static final Log LOG = LogFactory.getLog(DataPointDao.class);


    //
    //
    // Data Points
    //
    public String generateUniqueXid() {
		return dataPointService.generateUniqueXid();
    }

    public boolean isXidUnique(String xid, int excludeId) {
		return dataPointService.isXidUnique(xid, excludeId);
    }

    public String getExtendedPointName(int dataPointId) {
		return dataPointService.getExtendedPointName(dataPointId);
    }


    public List<DataPointVO> getDataPoints(Comparator<DataPointVO> comparator, boolean includeRelationalData) {
		return dataPointService.getDataPoints(comparator, includeRelationalData);
    }

    public List<DataPointVO> getDataPoints(int dataSourceId, Comparator<DataPointVO> comparator) {
		return dataPointService.getDataPoints(dataSourceId, comparator);
    }

    public DataPointVO getDataPoint(int id) {
    	try {
    		return dataPointService.getDataPoint(id);
    	} catch (EmptyResultDataAccessException e) {
            LOG.warn("datapoint does not exist for id: " + id + ", msg: " + e.getMessage());
    		return null;
    	}
    	
    }
    public DataPointVO getDataPointByXid(String xid){
        return dataPointService.getDataPointByXid(xid);
    }
    public DataPointVO getDataPoint(String xid) {
		return dataPointService.getDataPoint(xid);
    }


    public void saveDataPoint(final DataPointVO dp) {
		dataPointService.saveDataPoint(dp);
    }

    void insertDataPoint(final DataPointVO dp) {
		dataPointService.insertDataPoint(dp);
    }

    void updateDataPoint(final DataPointVO dp) {
		dataPointService.updateDataPoint(dp);
    }

    public void updateDataPointShallow(final DataPointVO dp) {
		dataPointService.updateDataPointShallow(dp);
    }

    public void deleteDataPoints(final int dataSourceId) {
		dataPointService.deleteDataPoints(dataSourceId);
    }

    public void deleteDataPoint(final int dataPointId) {
		dataPointService.deleteDataPoint(dataPointId);
    }

    void deletePointHistory(int dataPointId) {
		dataPointService.deletePointHistory(dataPointId);
    }

    void deletePointHistory(int dataPointId, long min, long max) {
		dataPointService.deletePointHistory(dataPointId, min, max);
    }

    void deleteDataPointImpl(String dataPointIdList) {
		dataPointService.deleteDataPointImpl(dataPointIdList);
        
    }

    //
    //
    // Event detectors
    //
    public int getDataPointIdFromDetectorId(int pedId) {
		return dataPointService.getDataPointIdFromDetectorId(pedId);
    }

    public String getDetectorXid(int pedId) {
		return dataPointService.getDetectorXid(pedId);
    }

    public int getDetectorId(String pedXid, int dataPointId) {
		return dataPointService.getDetectorId(pedXid, dataPointId);
    }

    public String generateEventDetectorUniqueXid(int dataPointId) {
		return dataPointService.generateEventDetectorUniqueXid(dataPointId);
    }

    public boolean isEventDetectorXidUnique(int dataPointId, String xid, int excludeId) {
		return dataPointService.isEventDetectorXidUnique(dataPointId, xid, excludeId);
    }

    public void copyPermissions(final int fromDataPointId, final int toDataPointId) {
		dataPointService.copyPermissions(fromDataPointId, toDataPointId);
    }

    //
    //
    // Point hierarchy
    //

    public PointHierarchy getPointHierarchy() {
		return dataPointService.getPointHierarchy();
    }

    public void savePointHierarchy(final PointFolder root) {
		dataPointService.savePointHierarchy(root);
    }

    void savePointFolder(PointFolder folder, int parentId) {
		dataPointService.savePointFolder(folder, parentId);
    }

    void savePointsInFolder(PointFolder folder) {
		dataPointService.savePointsInFolder(folder);
    }

    public List<PointHistoryCount> getTopPointHistoryCounts() {
		return dataPointService.getTopPointHistoryCounts();
    }
}
