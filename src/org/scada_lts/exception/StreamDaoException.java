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
 * Exception for serialization data from database
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class StreamDaoException extends RuntimeException {
	
	private static final long serialVersionUID = -4893966498449116229L;
	
	public StreamDaoException() { super(); }
	public StreamDaoException(String message) { super(message); }
	public StreamDaoException(String message, Throwable cause) { super(message, cause); }
	public StreamDaoException(Throwable cause) { super(cause); }

}


