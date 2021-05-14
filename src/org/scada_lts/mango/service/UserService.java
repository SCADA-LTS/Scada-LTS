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

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.web.taglib.Functions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.UserCommentDAO;
import org.scada_lts.dao.UserDAO;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.permissions.service.*;
import org.scada_lts.service.UserRoleService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

import static org.scada_lts.permissions.service.util.PermissionsUtils.*;

/**
 * UserService
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class UserService implements MangoUser {

	private static final Log LOG = LogFactory.getLog(UserService.class);

	private UserDAO userDAO = new UserDAO();
	private UserCommentDAO userCommentDAO = new UserCommentDAO();

	private MailingListService mailingListService = new MailingListService();
	private EventService eventService = new EventService();
	private PointValueService pointValueService = new PointValueService();
	private UsersProfileService usersProfileService = new UsersProfileService();
	private UserRoleService userRoleService = new UserRoleService();

	private PermissionsService<DataPointAccess, User> dataPointPermissionsService = new DataPointUserPermissionsService();
	private PermissionsService<Integer, User> dataSourcePermissionsService = new DataSourceUserPermissionsService();

	private PermissionsService<DataPointAccess, UsersProfileVO> dataPointProfilePermissionsService = new DataPointProfilePermissionsService();
	private PermissionsService<Integer, UsersProfileVO> dataSourceProfilePermissionsService = new DataSourceProfilePermissionsService();
	private PermissionsService<ViewAccess, UsersProfileVO> viewProfilePermissionsService = new ViewProfilePermissionsService();
	private PermissionsService<WatchListAccess, UsersProfileVO> watchListProfilePermissionsService = new WatchListProfilePermissionsService();

	@Override
	public User getUser(int id) {
		User user = userDAO.getUser(id);
		populateUserPermissions(user);
		return user;
	}

	@Override
	public User getUser(String username) {
		User user = userDAO.getUser(username);
		populateUserPermissions(user);
		return user;
	}

	@Override
	public List<User> getUsers() {
		return userDAO.getUsers();
	}

	@Override
	public List<User> getUsersWithProfile() {
		List<User> users = userDAO.getUsers();
		populateUserPermissionsWithProfile(users);
		return users;
	}

	@Override
	public List<User> getActiveUsers() {
		List<User> users = userDAO.getActiveUsers();
		populateUserPermissions(users);
		return users;
	}

	private void populateUserPermissions(List<User> users) {
		for (User user : users) {
			populateUserPermissions(user);
		}
	}

	private void populateUserPermissionsWithProfile(List<User> users) {
		for (User user : users) {
			populateUserPermissionsWithProfile(user);
		}
	}

	@Override
	public void populateUserPermissions(User user) {
		if (user != null) {
			user.setDataSourcePermissions(dataSourcePermissionsService.getPermissions(user));
			user.setDataPointPermissions(dataPointPermissionsService.getPermissions(user));

			usersProfileService.getProfileByUser(user).ifPresent(a -> {
				user.setUserProfileId(a.getId());
				user.setDataPointProfilePermissions(dataPointProfilePermissionsService.getPermissions(a));
				user.setDataSourcePermissions(dataSourceProfilePermissionsService.getPermissions(a));
				user.setViewProfilePermissions(viewProfilePermissionsService.getPermissions(a));
				user.setWatchListProfilePermissions(watchListProfilePermissionsService.getPermissions(a));
			});
		}
	}

	public void populateUserPermissionsWithProfile(User user) {
		if (user != null) {
			user.setDataSourcePermissions(dataSourcePermissionsService.getPermissions(user));
			user.setDataPointPermissions(dataPointPermissionsService.getPermissions(user));

			usersProfileService.getProfileByUser(user).ifPresent(a -> {
				user.setUserProfileId(a.getId());
			});
		}
	}

	@Override
	public void saveUser(User user) {
		if (user.getId() == Common.NEW_ID) {
			insertUser(user);
		} else {
			updateUser(user);
		}
	}

	@Override
	public void insertUser(User user) {
		try {
			int id = userDAO.insert(user);
			user.setId(id);
			updatePermissions(user);
			userRoleService.insertUserRole(id, user.isAdmin());
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
		}
	}

	@Override
	public void updateUser(User user) {
		if (user.getPhone() == null) {
			user.setPhone("");
		}
		if (user.getHomeUrl() == null) {
			user.setHomeUrl("");
		}

		userDAO.update(user);
		updatePermissions(user);
		userRoleService.updateUserRole(user.getId(), user.isAdmin());
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public void deleteUser(int userId) {
		userCommentDAO.update(userId);
		mailingListService.deleteMailingListMemberWithUserId(userId);
		pointValueService.updatePointValueAnnotations(userId);
		eventService.deleteUserEvent(userId);
		eventService.updateEventAckUserId(userId);
		userRoleService.deleteUserRoleUser(userId);
		userDAO.delete(userId);
	}

	@Override
	public void recordLogin(int userId) {
		userDAO.updateLogin(userId);
	}

	@Override
	public void saveHomeUrl(int userId, String homeUrl) {
		userDAO.updateHomeUrl(userId, homeUrl);
	}

	@Override
	public void insertUserComment(int typeId, int referenceId, UserComment comment) {
		//TODO seroUtils
		comment.setComment(Functions.truncate(comment.getComment(), 1024));
		userCommentDAO.insert(comment, typeId, referenceId);
	}

	private void updatePermissions(User user) {
		updateDataSourcePermissions(user, dataSourcePermissionsService);
		updateDataPointPermissions(user, dataPointPermissionsService);
	}
}
