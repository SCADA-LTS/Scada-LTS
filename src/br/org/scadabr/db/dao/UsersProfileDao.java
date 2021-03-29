package br.org.scadabr.db.dao;


import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.UsersProfileService;

import br.org.scadabr.api.exception.DAOException;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;

import com.serotonin.mango.db.dao.BaseDao;
import com.serotonin.mango.vo.User;

import javax.sql.DataSource;

public class UsersProfileDao extends BaseDao {
	public static final Log LOG = LogFactory.getLog(UsersProfileDao.class);

	private UsersProfileService usersProfileService;

    public UsersProfileDao() {
		this.usersProfileService = new UsersProfileService();
	}

	public UsersProfileDao(DataSource dataSource,
						   UsersProfileService usersProfileService) {
		super(dataSource);
		this.usersProfileService = usersProfileService;
	}

	public List<UsersProfileVO> getUsersProfiles() {
		return usersProfileService.getUsersProfiles();
	}

	public List<UsersProfileVO> getUsersProfiles(Comparator<UsersProfileVO> comparator) {
		return usersProfileService.getUsersProfiles(comparator);
	}

	public UsersProfileVO getUserProfileByName(String name) {
		return usersProfileService.getUserProfileByName(name);
    }

	public UsersProfileVO getUserProfileById(int id) {
		return usersProfileService.getUserProfileById(id);
	}

	public UsersProfileVO getUserProfileByXid(String xid) {
		return usersProfileService.getUserProfileByXid(xid);
	}

	public void saveUsersProfile(UsersProfileVO profile) throws DAOException {
        usersProfileService.saveUsersProfile(profile);
	}

	public void saveUsersProfileWithoutNameConstraint(UsersProfileVO profile)
			throws DAOException {
		usersProfileService.saveUsersProfileWithoutNameConstraint(profile);
	}

	public void updatePermissions() {
		usersProfileService.updatePermissions();
	}

	public void updateWatchlistPermissions(){
        usersProfileService.updateWatchlistPermissions();
	}

	public void updateViewPermissions() {
        usersProfileService.updateViewPermissions();
	}

	public void updateDataPointPermissions() {
        usersProfileService.updateDataPointPermissions();
	}

	public void updateDataSourcePermissions() {
        usersProfileService.updateDataSourcePermissions();
	}

	public void updateProfile(UsersProfileVO profile) {
        usersProfileService.updateProfile(profile);
	}

	public void updateUsersProfile(User user, UsersProfileVO profile) {
        usersProfileService.updateUsersProfile(user, profile);
	}

    public UsersProfileVO getUserProfileByUserId(int userid) {
		return usersProfileService.getUserProfileByUserId(userid);
	}

	public void grantUserAdminProfile(User user) {
		usersProfileService.grantUserAdminProfile(user);
	}

	public void resetUserProfile(User user) {
		usersProfileService.resetUserProfile(user);
	}

	public boolean userProfileExists(String xid) {
        return usersProfileService.userProfileExists(xid);
	}

	public void deleteUserProfile(int usersProfileId) {
        usersProfileService.deleteUserProfile(usersProfileId);
	}
}
