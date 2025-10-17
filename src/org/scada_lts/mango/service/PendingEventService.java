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

import com.serotonin.mango.rt.event.type.AlarmLevelType;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.UserComment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.IUserCommentDAO;
import org.scada_lts.dao.PendingEventsDAO;
import org.scada_lts.service.IHighestAlarmLevelService;
import org.scada_lts.utils.SystemSettingsUtils;
import org.scada_lts.web.beans.ApplicationBeans;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class PendingEventService {

	private static final Log LOG = LogFactory.getLog(PendingEventService.class);

	private final IUserCommentDAO userCommentDAO;

	private final PendingEventsDAO pendingEventsDAO;

	private final SystemSettingsService systemSettingsService;

	private final IHighestAlarmLevelService highestAlarmLevelService;

	public PendingEventService() {
		userCommentDAO = ApplicationBeans.getUserCommentDaoBean();
		highestAlarmLevelService = ApplicationBeans.getHighestAlarmLevelServiceBean();
		pendingEventsDAO = new PendingEventsDAO();
		systemSettingsService = new SystemSettingsService();
	}

	public Map<Integer, List<EventInstance>> getPendingEvents() {
		Map<Integer, List<EventInstance>> cacheEvents = new ConcurrentHashMap<>();
		ApplicationBeans.Lazy.getLoggedUsersBean().ifPresent(loggedUsers -> {
			Set<Integer> users = loggedUsers.getUserIds();
			Map<Integer, List<UserComment>> comments = getCacheUserComments(userCommentDAO.getEventComments());
			int limit = systemSettingsService.getMiscSettings().getEventPendingLimit();
			for (int userId: users) {
				Set<EventInstanceEqualsById> events = pendingEventsDAO.getPendingEvents(userId, comments, AlarmLevelType.NONE,
								SystemSettingsUtils.getEventPendingUpdateLimit(), 0).stream()
						.map(EventInstanceEqualsById::new)
						.collect(Collectors.toSet());
				if(!events.isEmpty()) {
					int highestAlarmLevelForUser = highestAlarmLevelService.getAlarmLevel(User.onlyId(userId));
					for (AlarmLevelType alarmLevelType : AlarmLevelType.getAlarmLevelsWithoutNone()) {
						if(alarmLevelType.getCode() <= highestAlarmLevelForUser) {
							long count = events.stream()
									.filter(a -> a.getEventInstance().getAlarmLevel() >= alarmLevelType.getCode())
									.count();
							if (count < limit) {
								events.addAll(pendingEventsDAO.getPendingEvents(userId, comments, alarmLevelType, limit, 0).stream()
										.map(EventInstanceEqualsById::new)
										.collect(Collectors.toSet()));
							}
						}
					}
				}
				cacheEvents.put(userId, events.stream().map(EventInstanceEqualsById::getEventInstance).collect(Collectors.toList()));
			}
		});

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

	static class EventInstanceEqualsById {
		private final EventInstance eventInstance;
		public EventInstanceEqualsById(EventInstance eventInstance) {
			this.eventInstance = eventInstance;
		}

		public EventInstance getEventInstance() {
			return eventInstance;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof EventInstanceEqualsById)) return false;
			EventInstanceEqualsById byId = (EventInstanceEqualsById) o;
			return eventInstance.getId() == byId.eventInstance.getId();
		}

		@Override
		public int hashCode() {
			return Objects.hash(eventInstance.getId());
		}
	}
}
