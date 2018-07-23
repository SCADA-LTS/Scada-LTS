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

/** 
 * Interface mango to change hierarchy
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;

public interface MangoPointHierarchyCacheable {
	
	/**
	 * Add data point in cache
	 * @param dp
	 */
	void addDataPoint(DataPointVO dp);
	
	/**
	 * Update data point in cache
	 * @param dp
	 */
	void updateDataPoint(DataPointVO dp);
	
	/**
	 * Delete data points in cache
	 * @param idsSeparateComma
	 */
	void deleteDataPoint(String idsSeparateComma);
	
	/**
	 * Change data source (update) in cache
	 * @param vo
	 */
	void changeDataSource(DataSourceVO<?> vo);

}
