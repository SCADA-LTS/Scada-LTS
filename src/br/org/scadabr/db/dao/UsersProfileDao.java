package br.org.scadabr.db.dao;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.UsersProfileService;
import org.scada_lts.permissions.service.*;
import org.scada_lts.serorepl.utils.StringUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import br.org.scadabr.api.exception.DAOException;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.BaseDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.db.dao.ViewDao;
import com.serotonin.mango.db.dao.WatchListDao;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;

import javax.sql.DataSource;

public class UsersProfileDao extends BaseDao {
	public static final Log LOG = LogFactory.getLog(UsersProfileDao.class);

	private static Set<UsersProfileVO> currentProfileList = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private static AtomicInteger lock = new AtomicInteger();

	private WatchListDao watchlistDao;
	private ViewDao viewDao;
	private UserDao userDao;
	private UsersProfileService usersProfileService;
	private PermissionsService<WatchListAccess, WatchList> watchListPermissionsService;
    private PermissionsService<DataPointAccess, DataPointVO> dataPointPermissionsService;
    private PermissionsService<Integer, DataSourceVO<?>> dataSourcePermissionsService;
	private PermissionsService<ViewAccess, View> viewPermissionsService;

    public UsersProfileDao() {
		this.watchlistDao = new WatchListDao();
		this.viewDao = new ViewDao();
		this.userDao = new UserDao();
		this.usersProfileService = new UsersProfileService();
		this.watchListPermissionsService = new WatchListPermissionsService();
		this.dataPointPermissionsService = new DataPointPermissionsService();
		this.dataSourcePermissionsService = new DataSourcePermissionsService();
		this.viewPermissionsService = new ViewPermissionsService();
	}

	public UsersProfileDao(DataSource dataSource, WatchListDao watchlistDao, ViewDao viewDao,
						   UserDao userDao, UsersProfileService usersProfileService,
						   WatchListPermissionsService watchListPermissionsService,
						   DataPointPermissionsService dataPointPermissionsService,
						   DataSourcePermissionsService dataSourcePermissionsService,
						   ViewPermissionsService viewPermissionsService) {
		super(dataSource);
		this.watchlistDao = watchlistDao;
		this.viewDao = viewDao;
		this.userDao = userDao;
		this.usersProfileService = usersProfileService;
		this.watchListPermissionsService = watchListPermissionsService;
		this.dataPointPermissionsService = dataPointPermissionsService;
		this.dataSourcePermissionsService = dataSourcePermissionsService;
		this.viewPermissionsService = viewPermissionsService;
	}

	public List<UsersProfileVO> getUsersProfiles() {
		return getUsersProfiles(Comparator.comparing(UsersProfileVO::getName));
	}

	public List<UsersProfileVO> getUsersProfiles(Comparator<UsersProfileVO> comparator) {
		if(currentProfileList.isEmpty()) {
			if(lock.getAndDecrement() == 0) {
				try {
                    usersProfileService.getProfiles(Integer.MAX_VALUE)
                            .forEach(a -> {
                                populateUserProfilePermissions(a);
                                currentProfileList.add(a);
                            });
				} finally {
					lock.set(0);
				}
			}
		}
		return currentProfileList.stream()
				.sorted(comparator)
				.collect(Collectors.toList());
	}

	public UsersProfileVO getUserProfileByName(String name) {
		return getUsersProfile(a -> !StringUtils.isEmpty(name) && name.equals(a.getName()));
    }

	public UsersProfileVO getUserProfileById(int id) {
		return getUsersProfile(a -> id != Common.NEW_ID && a.getId() == id);
	}

	public UsersProfileVO getUserProfileByXid(String xid) {
		return getUsersProfile(a -> !StringUtils.isEmpty(xid) && xid.equals(a.getXid()));
	}

	public void saveUsersProfile(UsersProfileVO profile) throws DAOException {
		if (profileExistsWithThatName(profile)
				&& profile.getId() == Common.NEW_ID) {
			throw new DAOException();
		}

		saveUsersProfileWithoutNameConstraint(profile);
	}

	private boolean profileExistsWithThatName(UsersProfileVO profile) {
		return getUsersProfile(a -> !StringUtils.isEmpty(profile.getName()) && profile.getName().equals(a.getName())) != null;
	}

	public void saveUsersProfileWithoutNameConstraint(UsersProfileVO profile)
			throws DAOException {
		if (profile.getName() == null
				|| profile.getName().replaceAll("\\s+", "").isEmpty()) {
			throw new DAOException();
		}

		if (profile.getXid() == null) {
			profile.setXid(generateUniqueXid(UsersProfileVO.XID_PREFIX,
					"usersProfiles"));
		}

		if (profile.getId() == Common.NEW_ID) {
			insertProfile(profile);
		} else {
			updateProfile(profile);
		}
	}

	public void updatePermissions() {
		updateViewPermissions();
		updateDataPointPermissions();
		updateWatchlistPermissions();
		updateDataSourcePermissions();
	}

	public void updateWatchlistPermissions() {
		currentProfileList.forEach(this::populateWatchlists);
	}

	public void updateViewPermissions() {
		currentProfileList.forEach(this::populateViews);
	}

	public void updateDataPointPermissions() {
		currentProfileList.forEach(this::populateDatapoints);
	}

	public void updateDataSourcePermissions() {
		currentProfileList.forEach(this::populateDataSources);
	}

	public void updateProfile(UsersProfileVO profile) {
		usersProfileService.setProfileName(profile.getName(), profile);
		List<Integer> usersIds = usersProfileService.getUsersByProfile(profile);

		for (Integer userId : usersIds) {
			User profileUser = userDao.getUser(userId);
			profile.apply(profileUser);
			updateUsersProfile(profileUser, profile);
			userDao.saveUser(profileUser);
		}

		usersProfileService.saveRelationalData(profile);

	}

	public void updateUsersProfile(User user, UsersProfileVO profile) {
		if (user != null) {
			usersProfileService.getProfileByUser(user)
					.ifPresent(a -> usersProfileService.removeUserProfile(user));
			usersProfileService.createUserProfile(user, profile);
		}

		//sharing an object doesn't work
		/*
		for (WatchList watchlist : profile.retrieveWatchlists()) {
			watchlistDao.saveWatchList(watchlist);
		}

		for (View view : profile.retrieveViews()) {
			viewDao.saveView(view);
		}*/

		currentProfileList.add(profile);
	}

	private void insertProfile(UsersProfileVO profile) {
		profile.setId(usersProfileService.createProfile(profile.getXid(), profile.getName()));

		setViews(profile);
		usersProfileService.saveRelationalData(profile);

		currentProfileList.add(profile);
	}

	private void populateUserProfilePermissions(UsersProfileVO profile) {
		if (profile == null) {
			return;
		}

		LOG.debug("populateDataSources");
		populateDataSources(profile);
		LOG.debug("populateDatapoints");
		populateDatapoints(profile);
		LOG.debug("populateWatchlists");
		populateWatchlists(profile);
		LOG.debug("populateViews");
		populateViews(profile);
		LOG.debug("populateUsers");
		populateUsers(profile);
		LOG.debug("end");

	}

	private void populateUsers(UsersProfileVO profile) {
		profile.defineUsers(usersProfileService.getUsersByProfile(profile));
	}

	private void populateWatchlists(UsersProfileVO profile) {
		profile.setWatchlistPermissions(watchListPermissionsService.getPermissionsByProfile(profile));

		WatchListDao watchListDao = new WatchListDao();
		List<WatchList> allwatchlists = watchListDao.getWatchLists();
        allwatchlists.forEach(a -> a.setWatchListUsers(watchListPermissionsService.getShareUsers(a)));
		profile.defineWatchlists(allwatchlists);
	}

	private void populateDatapoints(UsersProfileVO profile) {
        profile.setDataPointPermissions(dataPointPermissionsService.getPermissionsByProfile(profile));
	}

	private void populateDataSources(UsersProfileVO profile) {
        profile.setDataSourcePermissions(dataSourcePermissionsService.getPermissionsByProfile(profile));
	}

	private void populateViews(UsersProfileVO profile) {
		profile.setViewPermissions(viewPermissionsService.getPermissionsByProfile(profile));

		setViews(profile);
	}

	private void setViews(UsersProfileVO profile) {
		List<View> allviews = viewDao.getSimpleViews().stream().map(a -> {
			View view = new View();
			view.setId(a.getId());
			view.setXid(a.getXid());
			view.setName(a.getName());
			return view;
		}).collect(Collectors.toList());
		allviews.forEach(a -> a.setViewUsers(viewPermissionsService.getShareUsers(a)));
		profile.defineViews(allviews);
	}

    public UsersProfileVO getUserProfileByUserId(int userid) {
		return usersProfileService.getProfileByUserId(userid).map(this::populateUsersProfile).orElse(null);
	}

	public void grantUserAdminProfile(User user) {
		usersProfileService.removeUserProfile(user);

		// Add user to watchLists
		List<WatchList> watchLists = watchlistDao.getWatchLists();
		for (WatchList wl : watchLists) {
			watchlistDao.removeUserFromWatchList(wl.getId(), user.getId());
		}

		// Remove user from Views
		List<View> views = viewDao.getViews();
		for (View view : views) {
			viewDao.removeUserFromView(view.getId(), user.getId());
		}

		user.resetUserProfile();
	}

	public void resetUserProfile(User user) {
		usersProfileService.removeUserProfile(user);

		// Remove user from watchLists
		List<WatchList> watchLists = watchlistDao.getWatchLists();
		for (WatchList wl : watchLists) {
			watchlistDao.removeUserFromWatchList(wl.getId(), user.getId());
		}

		// Remove user from Views
		List<View> views = viewDao.getViews();
		for (View view : views) {
			viewDao.removeUserFromView(view.getId(), user.getId());
		}

		user.resetUserProfile();
	}

	public void setWatchlistDao(WatchListDao dao) {
		this.watchlistDao = dao;
	}

	public void setViewDao(ViewDao dao) {
		this.viewDao = dao;
	}

	public boolean userProfileExists(String xid) {
		UsersProfileVO profile = getUserProfileByXid(xid);
		return profile != null;
	}

	public void deleteUserProfile(final int usersProfileId) {
		// Get Users from Profile
		List<Integer> usersIds = usersProfileService.getUsersByProfileId(usersProfileId);

		// Reset user profile
		for (Integer userId : usersIds) {
			this.resetUserProfile(userDao.getUser(userId));
		}

		getTransactionTemplate().execute(
				new TransactionCallbackWithoutResult() {
					@SuppressWarnings("synthetic-access")
					@Override
					protected void doInTransactionWithoutResult(
							TransactionStatus status) {
						Object[] args = new Object[] { usersProfileId };
						// Delete relational data
						// ejt.update(
						// "delete from dataSourceUsersProfiles where userProfileId=?",
						// args);
						// ejt.update(
						// "delete from dataPointUsersProfiles where userProfileId=?",
						// args);
						// ejt.update(
						// "delete from watchlistUsersProfiles where userProfileId=?",
						// args);
						// ejt.update(
						// "delete from viewUsersProfiles where userProfileId=?",
						// args);
						// Delete the profile
						usersProfileService.removeProfile(usersProfileId);
					}
				});
		currentProfileList.removeIf(a -> a.getId() == usersProfileId);
	}

	private static UsersProfileVO getUsersProfile(Predicate<UsersProfileVO> filter) {
		return currentProfileList.stream()
                .peek(a -> LOG.debug(a.getName() + ' ' + a.getXid() + ' ' + a.getId()))
				.filter(filter)
				.findFirst()
				.orElseGet(() -> {
					LOG.warn("Profile not Found!");
					return null;
				});
	}

    private UsersProfileVO populateUsersProfile(UsersProfileVO profile) {
        populateUserProfilePermissions(profile);
        return profile;
    }
}
