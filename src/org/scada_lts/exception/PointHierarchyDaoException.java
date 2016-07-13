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

package org.scada_lts.exception;

/** 
 * Exception for points hiearchy DAO (data points)
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class PointHierarchyDaoException extends RuntimeException{
	
	private static final long serialVersionUID = 4177184281641026586L;
	
	public PointHierarchyDaoException() { super(); }
	public PointHierarchyDaoException(String message) { super(message); }
	public PointHierarchyDaoException(String message, Throwable cause) { super(message, cause); }
	public PointHierarchyDaoException(Throwable cause) { super(cause); }

}
