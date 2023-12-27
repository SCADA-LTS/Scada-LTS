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


import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.web.taglib.Functions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.IUserCommentDAO;
import org.scada_lts.dao.IUserDAO;
import org.scada_lts.dao.error.EntityNotUniqueException;
import org.scada_lts.exception.PasswordMismatchException;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.permissions.service.PermissionsService;
import org.scada_lts.web.beans.ApplicationBeans;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;

import static com.serotonin.mango.web.mvc.controller.ScadaLocaleUtils.findLocale;
import static org.scada_lts.permissions.service.util.PermissionsUtils.updateDataPointPermissions;
import static org.scada_lts.permissions.service.util.PermissionsUtils.updateDataSourcePermissions;

/**
 * UserService
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */

@Service
public class UserService implements MangoUser {

	private static final Log LOG = LogFactory.getLog(UserService.class);

	private final IUserDAO userDAO;
	private final IUserCommentDAO userCommentDAO;

	private MailingListService mailingListService = new MailingListService();
	private EventService eventService = new EventService();
	private PointValueService pointValueService = new PointValueService();
	private final UsersProfileService usersProfileService;

	private final PermissionsService<DataPointAccess, User> dataPointPermissionsService;
	private final PermissionsService<Integer, User> dataSourcePermissionsService;

	public UserService() {
		userDAO = ApplicationBeans.getUserDaoBean();
		dataPointPermissionsService = ApplicationBeans.getDataPointUserPermissionsServiceBean();
		dataSourcePermissionsService = ApplicationBeans.getDataSourceUserPermissionsServiceBean();
		usersProfileService = ApplicationBeans.getUsersProfileService();
		userCommentDAO = ApplicationBeans.getUserCommentDaoBean();
	}

	public UserService(IUserDAO userDAO, IUserCommentDAO userCommentDAO, MailingListService mailingListService,
					   EventService eventService, PointValueService pointValueService,
					   UsersProfileService usersProfileService,
					   PermissionsService<DataPointAccess, User> dataPointPermissionsService,
					   PermissionsService<Integer, User> dataSourcePermissionsService) {
		this.userDAO = userDAO;
		this.userCommentDAO = userCommentDAO;
		this.mailingListService = mailingListService;
		this.eventService = eventService;
		this.pointValueService = pointValueService;
		this.usersProfileService = usersProfileService;
		this.dataPointPermissionsService = dataPointPermissionsService;
		this.dataSourcePermissionsService = dataSourcePermissionsService;
	}

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

			usersProfileService.getProfileByUser(user).ifPresent(profile -> {
				user.setUserProfileId(profile.getId());
				user.setDataPointProfilePermissions(profile.getDataPointPermissions());
				user.setDataSourcePermissions(profile.getDataSourcePermissions());
				user.setViewProfilePermissions(profile.getViewPermissions());
				user.setWatchListProfilePermissions(profile.getWatchlistPermissions());
			});
		}
	}

	public void populateUserPermissionsWithProfile(User user) {
		if (user != null) {
			user.setDataSourcePermissions(dataSourcePermissionsService.getPermissions(user));
			user.setDataPointPermissions(dataPointPermissionsService.getPermissions(user));

			usersProfileService.getProfileByUser(user).ifPresent(profile -> {
				user.setUserProfileId(profile.getId());
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
	public void updateHideMenu(User user) {
		updateUser(user);
	}

	@Override
	public void updateScadaTheme(User user) {
		updateUser(user);
	}

	@Override
	public void insertUser(User user) {
		if(!isUsernameUnique(user.getUsername())) {
			throw new EntityNotUniqueException("That username already exists!");
		}
		try {
			int id = userDAO.insert(user);
			user.setId(id);
			updatePermissions(user);
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
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
	public void deleteUser(int userId) {
		userCommentDAO.update(userId);
		mailingListService.deleteMailingListMemberWithUserId(userId);
		pointValueService.updatePointValueAnnotations(userId);
		eventService.deleteUserEvent(userId);
		eventService.updateEventAckUserId(userId);
		userDAO.delete(userId);
		usersProfileService.updatePermissions();
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

	@Override
	public boolean isUsernameUnique(String username) {
		return (userDAO.getUser(username) == null);
	}

	@Override
	public void updateUserProfile(User user) {
		if (user.getUserProfile() == Common.NEW_ID) {
			usersProfileService.resetUserProfile(user);
		} else {
			UsersProfileVO profile = usersProfileService.getUserProfileById(user.getUserProfile());
			usersProfileService.updateUsersProfile(user, profile);
		}
	}

	@Override
	public void updateUserPassword(int userId, String newPassword) {
		userDAO.updateUserPassword(userId, Common.encrypt(newPassword));
	}


	@Override
	public void updateUserPassword(int userId, String newPassword, String oldPassword) throws PasswordMismatchException {
		oldPassword = Common.encrypt(oldPassword);
		if(oldPassword.equals(userDAO.getUser(userId).getPassword())) {
			updateUserPassword(userId, newPassword);
		} else {
			throw new PasswordMismatchException();
		}
	}

	@Override
	public void updateUserLang(int userId, String lang) throws IllformedLocaleException {
		Locale locale = findLocale(lang);
		if (locale == null)
			throw new IllformedLocaleException(
					"Locale for given language not found: " + lang);
		userDAO.updateUserLang(userId, lang);
	}

	private void updatePermissions(User user) {
		updateDataSourcePermissions(user, dataSourcePermissionsService);
		updateDataPointPermissions(user, dataPointPermissionsService);
		usersProfileService.updateDataPointPermissions();
		usersProfileService.updateDataSourcePermissions();
		updateUserProfile(user);
	}
}
