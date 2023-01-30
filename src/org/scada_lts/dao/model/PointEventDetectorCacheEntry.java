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
package org.scada_lts.dao.model;

import com.serotonin.mango.vo.event.PointEventDetectorVO;

/** 
 * Model for buffering PointEventDetector
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
@Deprecated
public class PointEventDetectorCacheEntry {
	
	private PointEventDetectorVO pointEventDetector;
	private int dataPointId;

	public PointEventDetectorCacheEntry(PointEventDetectorVO pointEventDetector, int dataPointId) {
		this.pointEventDetector = pointEventDetector;
		this.dataPointId = dataPointId;
	}

	public PointEventDetectorCacheEntry() {
		//
	}
	
	public PointEventDetectorVO getPointEventDetector() {
		return pointEventDetector;
	}
	public void setPointEventDetector(PointEventDetectorVO pointEventDetector) {
		this.pointEventDetector = pointEventDetector;
	}
	public int getDataPointId() {
		return dataPointId;
	}
	public void setDataPointId(int dataPointId) {
		this.dataPointId = dataPointId;
	}

	@Override
	public String toString() {
		return "{" +
				"pointEventDetector=" + pointEventDetector +
				", dataPointId=" + dataPointId +
				'}';
	}
}
