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
package org.scada_lts.dao.model.point;

/** 
 * Bean 
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
public class PointValueAdnnotation {
	
	private long pointValueId;
	private String textPointValueShort;
	private String textPointValueLong;
	private int sourceType;
	private long sourceId;
	
	public PointValueAdnnotation() {
		//
	}
	
	public long getPointValueId() {
		return pointValueId;
	}
	public void setPointValueId(long pointValueId) {
		this.pointValueId = pointValueId;
	}
	public String getTextPointValueShort() {
		return textPointValueShort;
	}
	public void setTextPointValueShort(String textPointValueShort) {
		this.textPointValueShort = textPointValueShort;
	}
	public String getTextPointValueLong() {
		return textPointValueLong;
	}
	public void setTextPointValueLong(String textPointValueLong) {
		this.textPointValueLong = textPointValueLong;
	}
	public int getSourceType() {
		return sourceType;
	}
	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}
	public long getSourceId() {
		return sourceId;
	}
	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}
}
