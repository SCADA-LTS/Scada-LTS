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
package org.scada_lts.mango.convert;

import javafx.util.Pair;
import org.scada_lts.dao.model.IdName;

import java.util.ArrayList;
import java.util.List;

/** 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class IdNameToIntValuePair {
	
	public static Pair<Integer, String> convert(IdName idName) {

	/*	IntValuePair ivp = new IntValuePair();
		ivp.setKey(idName.getId());
		ivp.setValue(idName.getName());
		return ivp;*/
		Pair<Integer, String> ivp2 = new Pair<>(idName.getId(), idName.getName());
		return ivp2;
	}
	
	public static List<Pair<Integer, String>> convert(List<IdName> lstIdName) {

		ArrayList<Pair<Integer, String>> list = new ArrayList<>();
//		ArrayList<IntValuePair> lstIntValuePair = new ArrayList<IntValuePair>();
		for (IdName idName: lstIdName) {
//			lstIntValuePair.add(convert(idName));
			list.add(convert(idName));
		}
		return list;
//		return lstIntValuePair;
	}

}
