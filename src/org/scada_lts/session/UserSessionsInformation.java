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

package org.scada_lts.session;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.directwebremoting.WebContextFactory;
import org.json.JSONArray;

/** 
 * @author Abil'I.T. development team, sdt@abilit.eu
 */
public class UserSessionsInformation {

	
	/**
	 * returns array of session information 
	 * @return
	 */
	public static JSONArray toJSON() {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) WebContextFactory.get()
				.getServletContext()
				.getAttribute(HttpSessionListenerImpl.SERVLET_CONTEXT_MAP);

		JSONArray jsonArray = new JSONArray();
		Set<String> keys = map.keySet();
		for (Iterator<String> i = keys.iterator(); i.hasNext();) {
			String key = i.next();
			SessionInfo value = (SessionInfo) map.get(key);
			jsonArray.put(value.toJSON());
		}

		return jsonArray;
	}

}
