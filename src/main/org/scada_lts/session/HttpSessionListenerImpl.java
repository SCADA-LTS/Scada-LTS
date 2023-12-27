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

import java.util.HashMap;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/** 
 * @author Abil'I.T. development team, sdt@abilit.eu
 */
@WebListener
public class HttpSessionListenerImpl implements HttpSessionListener {

	public static final String SERVLET_CONTEXT_MAP = "com.abilit.session.SERVLET_CONTEXT_MAP";

	/**
	 * object created for new session containing presentation user data  
	 * {@inheritDoc}
	 */
	@Override
	public void sessionCreated(HttpSessionEvent sessionEvent) {

		SessionInfo sessionInfo = new SessionInfo(sessionEvent.getSession());

		@SuppressWarnings("unchecked")
		HashMap<String, Object> map = (HashMap<String, Object>) sessionEvent.getSession().getServletContext().getAttribute(SERVLET_CONTEXT_MAP);
		if (map == null) {
			map = new HashMap<String, Object>();
			map.put(sessionEvent.getSession().getId(), sessionInfo);
			sessionEvent.getSession().getServletContext().setAttribute(SERVLET_CONTEXT_MAP, map);
		} else {
			map.put(sessionEvent.getSession().getId(), sessionInfo);
		}
	}

	/**
	 * method removes user session data 
	 * {@inheritDoc}
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {

		@SuppressWarnings("unchecked")
		HashMap<String, Object> map = (HashMap<String, Object>) sessionEvent.getSession()
				.getServletContext().getAttribute(SERVLET_CONTEXT_MAP);
		map.remove(sessionEvent.getSession().getId());
	}
}