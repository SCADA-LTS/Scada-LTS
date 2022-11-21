package org.scada_lts.dao.cache;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.dao.UsersProfileDAO;

import java.util.List;

public class UsersProfileCache implements UsersProfileCachable {

    private final UsersProfileDAO usersProfileDAO;

    public UsersProfileCache(UsersProfileDAO usersProfileDAO) {
        this.usersProfileDAO = usersProfileDAO;
    }

    public void resetDataSourcePermissions() {
        usersProfileDAO.resetDataSourcePermissions();
    }

    public void resetDataPointPermissions() {
        usersProfileDAO.resetDataPointPermissions();
    }

    public void resetViewPermissions() {
        usersProfileDAO.resetViewPermissions();
    }

    public void resetWatchListPermissions() {
        usersProfileDAO.resetWatchListPermissions();
    }

    @Override
    public List<UsersProfileVO> selectUserProfileByUserId(int userId) {
        return usersProfileDAO.selectUserProfileByUserId(userId);
    }

    @Override
    public List<UsersProfileVO> selectProfiles(int offset, int limit) {
        return usersProfileDAO.selectProfiles(offset, limit);
    }

    @Override
    public List<Integer> selectUsersByProfileId(int usersProfileId) {
        return usersProfileDAO.selectUsersByProfileId(usersProfileId);
    }

    @Override
    public int updateProfileName(String name, int usersProfileId) {
        return usersProfileDAO.updateProfileName(name, usersProfileId);
    }

    @Override
    public int deleteUserProfileByUserId(int userId) {
        return usersProfileDAO.deleteUserProfileByUserId(userId);
    }

    @Override
    public int deleteProfile(int profileId) {
        return usersProfileDAO.deleteProfile(profileId);
    }

    @Override
    public int insertUserProfile(int userId, int userProfileId) {
        return usersProfileDAO.insertUserProfile(userId, userProfileId);
    }

    @Override
    public int insertProfile(String usersProfileXid, String usersProfileName) {
        return usersProfileDAO.insertProfile(usersProfileXid, usersProfileName);
    }

    @Override
    public int[] insertDataPointUsersProfile(int profileId, List<DataPointAccess> toInsert) {
        return usersProfileDAO.insertDataPointUsersProfile(profileId, toInsert);
    }

    @Override
    public int[] deleteDataPointUsersProfile(int profileId, List<DataPointAccess> toDelete) {
        return usersProfileDAO.deleteDataPointUsersProfile(profileId, toDelete);
    }

    @Override
    public int[] insertDataSourceUsersProfile(int profileId, List<Integer> toInsert) {
        return usersProfileDAO.insertDataSourceUsersProfile(profileId, toInsert);
    }

    @Override
    public int[] deleteDataSourceUsersProfile(int profileId, List<Integer> toDelete) {
        return usersProfileDAO.deleteDataSourceUsersProfile(profileId, toDelete);
    }

    @Override
    public int[] insertViewUsersProfile(int profileId, List<ViewAccess> toInsert) {
        return usersProfileDAO.insertViewUsersProfile(profileId, toInsert);
    }

    @Override
    public int[] deleteViewUsersProfile(int profileId, List<ViewAccess> toDelete) {
        return usersProfileDAO.deleteViewUsersProfile(profileId, toDelete);
    }

    @Override
    public int[] insertWatchListUsersProfile(int profileId, List<WatchListAccess> toInsert) {
        return usersProfileDAO.insertWatchListUsersProfile(profileId, toInsert);
    }

    @Override
    public int[] deleteWatchListUsersProfile(int profileId, List<WatchListAccess> toDelete) {
        return usersProfileDAO.deleteWatchListUsersProfile(profileId, toDelete);
    }

    @Override
    public List<WatchListAccess> selectWatchListPermissionsByProfileId(int usersProfileId) {
        return usersProfileDAO.selectWatchListPermissionsByProfileId(usersProfileId);
    }

    @Override
    public List<ViewAccess> selectViewPermissionsByProfileId(int usersProfileId) {
        return usersProfileDAO.selectViewPermissionsByProfileId(usersProfileId);
    }

    @Override
    public List<Integer> selectDataSourcePermissionsByProfileId(int profileId) {
        return usersProfileDAO.selectDataSourcePermissionsByProfileId(profileId);
    }

    @Override
    public List<DataPointAccess> selectDataPointPermissionsByProfileId(int profileId) {
        return usersProfileDAO.selectDataPointPermissionsByProfileId(profileId);
    }
}
