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
 * Exception for Hierarchy View
 *
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
public class ViewHierarchyDaoException extends RuntimeException {
	
	private static final long serialVersionUID = 2634167769103120037L;
	
	public ViewHierarchyDaoException() { super(); }
	public ViewHierarchyDaoException(String message) { super(message); }
	public ViewHierarchyDaoException(String message, Throwable cause) { super(message, cause); }
	public ViewHierarchyDaoException(Throwable cause) { super(cause); }

}