package org.scada_lts.dao.cache;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.dao.IUsersProfileDAO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UsersProfileDaoWithCache implements IUsersProfileDAO {

    private final UsersProfileCachable usersProfileCache;
    private final IUsersProfileDAO usersProfileDAO;

    public UsersProfileDaoWithCache(UsersProfileCachable usersProfileCache, IUsersProfileDAO usersProfileDAO) {
        this.usersProfileCache = usersProfileCache;
        this.usersProfileDAO = usersProfileDAO;
    }

    @Override
    public Optional<UsersProfileVO> selectProfileById(int profileId) {
        return selectProfiles(0, Integer.MAX_VALUE).stream().filter(a -> a.getId() == profileId).map(UsersProfileVO::new).findAny();
    }

    @Override
    public Optional<UsersProfileVO> selectProfileByXid(String profileXid) {
        return selectProfiles(0, Integer.MAX_VALUE).stream().filter(a -> a.getXid().equals(profileXid)).map(UsersProfileVO::new).findAny();
    }

    @Override
    public List<UsersProfileVO> selectUserProfileByUserId(int userId) {
        return usersProfileCache.selectUserProfileByUserId(userId).stream()
                .map(UsersProfileVO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsersProfileVO> selectProfiles(int offset, int limit) {
        return usersProfileCache.selectProfiles(offset, limit);
    }

    @Override
    public List<Integer> selectUsersByProfileId(int profileId) {
        return usersProfileCache.selectUsersByProfileId(profileId);
    }

    @Override
    public int deleteUserProfileByUserId(int userId) {
        return usersProfileCache.deleteUserProfileByUserId(userId);
    }

    @Override
    public int insertUserProfile(int userId, int profileId) {
        return usersProfileCache.insertUserProfile(userId, profileId);
    }

    @Override
    public int updateProfileName(String profileName, int profileId) {
        return usersProfileCache.updateProfileName(profileName, profileId);
    }

    @Override
    public int insertProfile(String profileXid, String profileName) {
        return usersProfileCache.insertProfile(profileXid, profileName);
    }

    @Override
    public int deleteProfile(int profileId) {
        return usersProfileCache.deleteProfile(profileId);
    }


    @Override
    public List<Integer> selectDataSourcePermissionsByProfileId(int profileId) {
        return usersProfileCache.selectDataSourcePermissionsByProfileId(profileId);
    }

    @Override
    public int[] insertDataSourceUsersProfile(int profileId, List<Integer> toInsert) {
        return usersProfileCache.insertDataSourceUsersProfile(profileId, toInsert);
    }

    @Override
    public int[] deleteDataSourceUsersProfile(int profileId, List<Integer> toDelete) {
        return usersProfileCache.deleteDataSourceUsersProfile(profileId, toDelete);
    }


    @Override
    public List<DataPointAccess> selectDataPointPermissionsByProfileId(int profileId) {
        return usersProfileCache.selectDataPointPermissionsByProfileId(profileId);
    }

    @Override
    public int[] insertDataPointUsersProfile(int profileId, List<DataPointAccess> toInsert) {
        return usersProfileCache.insertDataPointUsersProfile(profileId, toInsert);
    }

    @Override
    public int[] deleteDataPointUsersProfile(int profileId, List<DataPointAccess> toDelete) {
        return usersProfileCache.deleteDataPointUsersProfile(profileId, toDelete);
    }

    @Override
    public List<ViewAccess> selectViewPermissionsByProfileId(int profileId) {
        return usersProfileCache.selectViewPermissionsByProfileId(profileId);
    }

    @Override
    public int[] insertViewUsersProfile(int profileId, List<ViewAccess> toInsert) {
        return usersProfileCache.insertViewUsersProfile(profileId, toInsert);
    }

    @Override
    public int[] deleteViewUsersProfile(int profileId, List<ViewAccess> toDelete) {
        return usersProfileCache.deleteViewUsersProfile(profileId, toDelete);
    }


    @Override
    public List<WatchListAccess> selectWatchListPermissionsByProfileId(int profileId) {
        return usersProfileCache.selectWatchListPermissionsByProfileId(profileId);
    }

    @Override
    public int[] insertWatchListUsersProfile(int profileId, List<WatchListAccess> toInsert) {
        return usersProfileCache.insertWatchListUsersProfile(profileId, toInsert);
    }

    @Override
    public int[] deleteWatchListUsersProfile(int profileId, List<WatchListAccess> toDelete) {
        return usersProfileCache.deleteWatchListUsersProfile(profileId, toDelete);
    }

    @Override
    public void resetDataSourcePermissions() {
        usersProfileCache.resetCacheDataSourcePermissions();
    }

    @Override
    public void resetDataPointPermissions() {
        usersProfileCache.resetCacheDataPointPermissions();
    }

    @Override
    public void resetViewPermissions() {
        usersProfileCache.resetCacheViewPermissions();
    }

    @Override
    public void resetWatchListPermissions() {
        usersProfileCache.resetCacheWatchListPermissions();
    }

    @Override
    public String generateUniqueXid(String prefix) {
        return usersProfileDAO.generateUniqueXid(prefix);
    }
}
