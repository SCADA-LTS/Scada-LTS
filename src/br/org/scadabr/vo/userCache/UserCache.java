package br.org.scadabr.vo.userCache;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.vo.User;
import com.serotonin.util.ILifecycle;
import com.serotonin.util.LifecycleException;

import br.org.scadabr.db.dao.UsersProfileDao;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;

public class UserCache implements ILifecycle {

	private final Log LOG = LogFactory.getLog(UserCache.class);

	// centralize all user access here
	private UserDao userDao;
	private UsersProfileDao profileDao;

	private List<User> permissionedUsers = new LinkedList<User>();
	private List<User> activeUsers = new LinkedList<User>();

	public List<User> getActiveUsers() {
		return activeUsers;
	}

	public List<User> getPermissionedUsers() {
		return permissionedUsers;
	}

	public User getUser(int userId) {
		LOG.trace("getUser(" + userId + ");");
		synchronized (permissionedUsers) {
			Iterator<User> iterUser = permissionedUsers.iterator();

			while (iterUser.hasNext()) {
				User current = iterUser.next();

				// if its creating a new user...
				if (current == null) {
					iterUser.remove();
					break;
				}

				LOG.trace("Check if " + current.getId());

				if (current.getId() == userId) {
					LOG.trace("User found in cache");
					return current;
				}
			}

			User user = userDao.getUser(userId);
			user = getUpdatedPermissions(user);
			permissionedUsers.add(user);
			return user;
		}
	}

	public User getUser(String username) {
		LOG.trace("getUser(" + username + ")");
		synchronized (permissionedUsers) {
			Iterator<User> iterUser = permissionedUsers.iterator();

			while (iterUser.hasNext()) {
				User current = iterUser.next();

				// if its creating a new user...
				if (current == null) {
					iterUser.remove();
					break;
				}

				LOG.trace("User: " + current.getUsername() + ", hash: " + current.getPassword());
				LOG.trace("Check if " + current.getUsername());

				if (current.getUsername().equals(username)) {
					LOG.trace("User found in cache");
					return current;
				}
			}
			LOG.trace("User not found");

			User user = userDao.getUser(username);
			user = getUpdatedPermissions(user);
			permissionedUsers.add(user);
			return user;
		}
	}

	private User getUpdatedPermissions(User user) {
		UsersProfileVO profile = null;
		try {
			profile = profileDao.getUserProfileByUserId(user.getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.trace("User may not have profile or defined ID - " + e.getMessage());
		}
		LOG.trace("update Permissions");
		if (profile != null) {
			LOG.trace("User has profile");
			user.setDataSourcePermissions(profile.getDataSourcePermissions());
			user.setDataPointPermissions(profile.getDataPointPermissions());
			user.setUserProfile(profile);
		} else {
			LOG.trace("User does not have profile");
			userDao.populateUserPermissions(user);
		}
		return user;
	}

	private List<User> getPermissionedUsers(List<User> users) {
		Iterator<User> iterUser = users.iterator();

		while (iterUser.hasNext()) {
			User current = iterUser.next();
			current = getUpdatedPermissions(current);
		}
		return users;
	}

	private List<User> filterActiveUsers(List<User> users) {
		Iterator<User> iterUser = users.iterator();

		while (iterUser.hasNext()) {
			User current = iterUser.next();

			if (current.isDisabled())
				continue;

			current = getUpdatedPermissions(current);
		}
		return users;
	}

	public void updateUser(User user) {
		synchronized (permissionedUsers) {
			Iterator<User> iterUser = permissionedUsers.iterator();

			// Remove from cache
			while (iterUser.hasNext()) {
				User current = iterUser.next();
				// if its creating a new user...
				if (current == null) {
					iterUser.remove();
					break;
				}

				LOG.trace("User: " + current.getUsername() + ", hash: " + current.getPassword());
				if (current.getId() == user.getId() && current.getUsername() == user.getUsername()) {
					try {
						iterUser.remove();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
			permissionedUsers.add(getUpdatedPermissions(user));

			activeUsers.clear();
			activeUsers = filterActiveUsers(permissionedUsers);
		}
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public UserCache() {
	}

	public void initialize() {
		// TODO Auto-generated method stub
		userDao = new UserDao();
		profileDao = new UsersProfileDao();
		permissionedUsers = getPermissionedUsers(userDao.getPermissionedUsers());
		activeUsers = filterActiveUsers(permissionedUsers);
	}

	@Override
	public void terminate() throws LifecycleException {
		// TODO Auto-generated method stub

	}

	@Override
	public void joinTermination() {
		// TODO Auto-generated method stub

	}

}
