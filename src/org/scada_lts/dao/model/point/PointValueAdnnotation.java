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
	private String changeOwner;

	public PointValueAdnnotation() {
		//
	}
	
	public PointValueAdnnotation(long id, String shortString, String longString, Integer sourceType,
			Integer sourceId) {
		this.pointValueId = id;
		this.textPointValueShort = shortString;
		this.textPointValueLong = longString;
		this.sourceType = sourceType;
		this.sourceId = sourceId;
	}
	public String getChangeOwner() {
		return changeOwner;
	}

	public void setChangeOwner(String changeOwner) {
		this.changeOwner = changeOwner;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (pointValueId ^ (pointValueId >>> 32));
		result = prime * result + (int) (sourceId ^ (sourceId >>> 32));
		result = prime * result + sourceType;
		result = prime * result + ((textPointValueLong == null) ? 0 : textPointValueLong.hashCode());
		result = prime * result + ((textPointValueShort == null) ? 0 : textPointValueShort.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PointValueAdnnotation other = (PointValueAdnnotation) obj;
		if (pointValueId != other.pointValueId)
			return false;
		if (sourceId != other.sourceId)
			return false;
		if (sourceType != other.sourceType)
			return false;
		if (textPointValueLong == null) {
			if (other.textPointValueLong != null)
				return false;
		} else if (!textPointValueLong.equals(other.textPointValueLong))
			return false;
		if (textPointValueShort == null) {
			if (other.textPointValueShort != null)
				return false;
		} else if (!textPointValueShort.equals(other.textPointValueShort))
			return false;
		return true;
	}
	public String toString(){
		return new StringBuffer()
				.append("SourceId")
				.append("   ")
				.append(getSourceId())
				.append("   ")
				.append("SourceType")
				.append("   ")
				.append(getSourceType())
				.append("   ")
				.append("getTextPointValueShort")
				.append("   ")
				.append(getTextPointValueShort()).toString();
	}
	
}
