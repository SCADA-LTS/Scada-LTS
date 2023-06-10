/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
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

package org.scada_lts.mango.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.UserComment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.IUserCommentDAO;
import org.scada_lts.dao.IUserDAO;
import org.scada_lts.dao.PendingEventsDAO;
import org.scada_lts.web.beans.ApplicationBeans;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class PendingEventService {

	private static final Log LOG = LogFactory.getLog(PendingEventService.class);

	private final IUserCommentDAO userCommentDAO;

	private final PendingEventsDAO pendingEventsDAO;

	private final IUserDAO userDAO;

	public PendingEventService() {
		userCommentDAO = ApplicationBeans.getUserCommentDaoBean();
		pendingEventsDAO = new PendingEventsDAO();
		userDAO = ApplicationBeans.getUserDaoBean();
	}

	public Map<Integer, List<EventInstance>> getPendingEvents() {

		List<Integer> users = userDAO.getAll();
		Map<Integer, List<UserComment>> comments = getCacheUserComments(userCommentDAO.getEventComments());

		Map<Integer,List<EventInstance>> cacheEvents = new ConcurrentHashMap<>();
		for (int userId: users) {
			List<EventInstance> events = new CopyOnWriteArrayList<>(pendingEventsDAO.getPendingEvents(userId, comments));
			cacheEvents.put(userId, events);
		}
		return cacheEvents;
	}

	private Map<Integer, List<UserComment>> getCacheUserComments(List<UserComment> commentsCache) {

		Map<Integer, List<UserComment>> mappedUserCommentForEvent = new ConcurrentHashMap<>();

		for (UserComment u: commentsCache) {
			int key = u.getTypeKey();
			mappedUserCommentForEvent.putIfAbsent(key, new CopyOnWriteArrayList<>());
			UserComment uc = new UserComment();
			uc.setComment(u.getComment());
			uc.setTs(u.getTs());
			uc.setUserId(u.getUserId());
			uc.setUsername(u.getUsername());
			uc.setTypeKey(key);
			mappedUserCommentForEvent.get(key).add(uc);
		}
		return mappedUserCommentForEvent;
	}
}
