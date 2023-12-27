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

package org.scada_lts.dao.model.pointhierarchy;


import com.serotonin.mango.vo.DataPointVO;

/**
 * Model for buffering info data source in point hierarchy cache.
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class PointHierarchyDataSource {
	
	private int id;
	private String name;
	private String xid;
	private String dataSourceType;
	
	public PointHierarchyDataSource() {
		//
	}
	
	public PointHierarchyDataSource(int id, String name, String xid, String dataSourceType) {
		this.id = id;
		this.name = name;
		this.xid = xid;
		this.dataSourceType = dataSourceType;
	}

	public static PointHierarchyDataSource newInstance(DataPointVO point) {
		return new PointHierarchyDataSource(point.getDataSourceId(), point.getDataSourceName(),
				point.getDataSourceXid(), String.valueOf(point.getDataSourceTypeId()));
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
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
	
	public String getDataSourceType() {
		return dataSourceType;
	}
	
	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}
}
