package br.org.scadabr.web.dwr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.WebContextFactory;

import br.org.scadabr.api.exception.DAOException;
import br.org.scadabr.db.dao.UsersProfileDao;
import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.db.dao.ViewDao;
import com.serotonin.mango.db.dao.WatchListDao;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.DataPointNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

public class UsersProfilesDwr {

	public Map<String, Object> getInitData() {
		Map<String, Object> initData = new HashMap<String, Object>();

		initData.put("admin", true);

		List<UsersProfileVO> profiles = new UsersProfileDao()
				.getUsersProfiles();
		initData.put("profiles", profiles);

		// Data sources
		List<DataSourceVO<?>> dataSourceVOs = new DataSourceDao()
				.getDataSources();
		List<Map<String, Object>> dataSources = new ArrayList<Map<String, Object>>(
				dataSourceVOs.size());
		Map<String, Object> ds, dp;
		List<Map<String, Object>> points;
		DataPointDao dataPointDao = new DataPointDao();
		for (DataSourceVO<?> dsvo : dataSourceVOs) {
			ds = new HashMap<String, Object>();
			ds.put("id", dsvo.getId());
			ds.put("name", dsvo.getName());
			points = new LinkedList<Map<String, Object>>();
			for (DataPointVO dpvo : dataPointDao.getDataPoints(dsvo.getId(),
					DataPointNameComparator.instance)) {
				dp = new HashMap<String, Object>();
				dp.put("id", dpvo.getId());
				dp.put("name", dpvo.getName());
				dp.put("settable", dpvo.getPointLocator().isSettable());
				points.add(dp);
			}
			ds.put("points", points);
			dataSources.add(ds);
		}

		initData.put("dataSources", dataSources);

		WatchListDao watchlistDao = new WatchListDao();
		List<WatchList> watchlists = watchlistDao.getWatchLists();
		initData.put("watchlists", watchlists);

		ViewDao viewDao = new ViewDao();
		List<View> views = viewDao.getViews();
		initData.put("views", views);

		return initData;
	}

	public UsersProfileVO getUserProfile(int id) {
		if (id == Common.NEW_ID) {
			return new UsersProfileVO();
		}

		return new UsersProfileDao().getUserProfileById(id);
	}

	public DwrResponseI18n saveUserAdmin(int id, String name,
			List<Integer> dataSourcePermissions,
			List<DataPointAccess> dataPointPermissions,
			List<WatchListAccess> watchlistPermissions,
			List<ViewAccess> viewsPermissions) {
		Permissions.ensureAdmin();

		HttpServletRequest request = WebContextFactory.get()
				.getHttpServletRequest();

		UsersProfileDao userDao = new UsersProfileDao();

		UsersProfileVO profile;
		if (id == Common.NEW_ID)
			profile = new UsersProfileVO();
		else
			profile = userDao.getUserProfileById(id);

		profile.setName(name);
		profile.setDataSourcePermissions(dataSourcePermissions);
		profile.setDataPointPermissions(dataPointPermissions);
		profile.setWatchlistPermissions(watchlistPermissions);
		profile.setViewPermissions(viewsPermissions);

		DwrResponseI18n response = new DwrResponseI18n();

		try {
			userDao.saveUsersProfile(profile);
		} catch (DAOException e) {
			response.addMessage(new LocalizableMessage(
					"usersProfiles.validate.nameUnique"));
		}

		if (!response.getHasMessages()) {
			response.addData("userProfileId", profile.getId());
		}

		return response;
	}

	public DwrResponseI18n deleteUsersProfile(int profileId) {
		Permissions.ensureAdmin();
		DwrResponseI18n response = new DwrResponseI18n();
		UsersProfileDao profileDao = new UsersProfileDao();
		try {
			profileDao.deleteUserProfile(profileId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.addMessage(new LocalizableMessage(
					"userProfiles.validate.errorDeleting"));
		}
		response.addMessage(new LocalizableMessage(
				"userProfiles.validate.successDeleting"));

		return response;
	}

}
