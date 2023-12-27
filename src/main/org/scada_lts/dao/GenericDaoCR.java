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

package org.scada_lts.dao;

import java.util.List;


/** 
 * Generic DAO Create, Read 
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */

public interface GenericDaoCR<T> {
	
	static final String LIMIT = " LIMIT ";
	static final int NO_LIMIT = 0;
	List<T> findAllWithUserName();
	List<T> findAll();
	
	/**
	 * Find base on pk
	 * @param pk
	 * @return T
	 */
	T findById(Object[] pk);
	
	List<T> filtered(String filter, Object[] argsFilter, long limit);

	/**
	 * @param entity
	 * @return pk
	 */
	Object[] create(T entity);

}
