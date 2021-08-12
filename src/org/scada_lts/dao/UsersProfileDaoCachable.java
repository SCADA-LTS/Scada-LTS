package org.scada_lts.dao;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.List;
import java.util.Optional;

public interface UsersProfileDaoCachable extends GenerateXid {

    @Cacheable(cacheNames = "usersProfilesByUserId")
    List<UsersProfileVO> selectUserProfileByUserId(int userId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "usersProfilesByUserId", key = "#p0"),
            @CacheEvict(cacheNames = "usersProfilesByXid", allEntries = true),
            @CacheEvict(cacheNames = "userIdsByProfileId", allEntries = true),
            @CacheEvict(cacheNames = "usersProfilesById", allEntries = true),
            @CacheEvict(cacheNames = "watchListProfilePermissions", allEntries = true),
            @CacheEvict(cacheNames = "viewProfilePermissions", allEntries = true),
            @CacheEvict(cacheNames = "dataSourceProfilePermissions", allEntries = true),
            @CacheEvict(cacheNames = "dataPointProfilePermissions", allEntries = true)
    })
    int deleteUserProfileByUserId(int userId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "usersProfilesByUserId", key = "#p0"),
            @CacheEvict(cacheNames = "usersProfilesByXid", allEntries = true),
            @CacheEvict(cacheNames = "userIdsByProfileId", key = "#p1"),
            @CacheEvict(cacheNames = "usersProfilesById", key = "#p1"),
            @CacheEvict(cacheNames = "watchListProfilePermissions", key = "#p1"),
            @CacheEvict(cacheNames = "viewProfilePermissions", key = "#p1"),
            @CacheEvict(cacheNames = "dataSourceProfilePermissions", key = "#p1"),
            @CacheEvict(cacheNames = "dataPointProfilePermissions", key = "#p1")
    })
    int insertUserProfile(int userId, int profileId);

    @Cacheable(cacheNames = "userIdsByProfileId")
    List<Integer> selectUsersByProfileId(int profileId);
    @Cacheable(cacheNames = "usersProfilesById")
    Optional<UsersProfileVO> selectProfileById(int profileId);
    @Cacheable(cacheNames = "usersProfilesByXid")
    Optional<UsersProfileVO> selectProfileByXid(String profileXid);
    @Cacheable(cacheNames = "usersProfilesById")
    List<UsersProfileVO> selectProfiles(int offset, int limit);

    @Caching(evict = {
            @CacheEvict(cacheNames = "userIdsByProfileId", key = "#p1"),
            @CacheEvict(cacheNames = "usersProfilesByXid", allEntries = true),
            @CacheEvict(cacheNames = "usersProfilesByUserId", allEntries = true),
            @CacheEvict(cacheNames = "usersProfilesById", key = "#p1")
    })
    int updateProfileName(String profileName, int profileId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "userIdsByProfileId", allEntries = true),
            @CacheEvict(cacheNames = "usersProfilesByXid", key = "#p0"),
            @CacheEvict(cacheNames = "usersProfilesByUserId", allEntries = true),
            @CacheEvict(cacheNames = "usersProfilesById", allEntries = true)
    })
    int insertProfile(String profileXid, String profileName);

    @Caching(evict = {
            @CacheEvict(cacheNames = "usersProfilesByUserId", allEntries = true),
            @CacheEvict(cacheNames = "userIdsByProfileId", key = "#p0"),
            @CacheEvict(cacheNames = "usersProfilesById", key = "#p0"),
            @CacheEvict(cacheNames = "watchListProfilePermissions", key = "#p0"),
            @CacheEvict(cacheNames = "viewProfilePermissions", key = "#p0"),
            @CacheEvict(cacheNames = "dataSourceProfilePermissions", key = "#p0"),
            @CacheEvict(cacheNames = "dataPointProfilePermissions", key = "#p0")
    })
    int deleteProfile(int profileId);


    List<ShareUser> selectDataSourceShareUsers(int dataSourceId);
    List<ShareUser> selectViewShareUsers(int viewId);
    List<ShareUser> selectWatchListShareUsers(int watchListId);
    List<ShareUser> selectDataPointShareUsers(int dataPointId);

    @Cacheable(cacheNames = "watchListProfilePermissions")
    List<WatchListAccess> selectWatchListPermissionsByProfileId(int profileId);
    @CacheEvict(cacheNames = "watchListProfilePermissions", key = "#p0")
    int[] insertWatchListUsersProfile(int profileId, List<WatchListAccess> toInsert);
    @CacheEvict(cacheNames = "watchListProfilePermissions", key = "#p0")
    int[] deleteWatchListUsersProfile(int profileId, List<WatchListAccess> toDelete);

    @Cacheable(cacheNames = "viewProfilePermissions")
    List<ViewAccess> selectViewPermissionsByProfileId(int profileId);
    @CacheEvict(cacheNames = "viewProfilePermissions", key = "#p0")
    int[] insertViewUsersProfile(int profileId, List<ViewAccess> toInsert);
    @CacheEvict(cacheNames = "viewProfilePermissions", key = "#p0")
    int[] deleteViewUsersProfile(int profileId, List<ViewAccess> toDelete);

    @Cacheable(cacheNames = "dataSourceProfilePermissions")
    List<Integer> selectDataSourcePermissionsByProfileId(int profileId);
    @CacheEvict(cacheNames = "dataSourceProfilePermissions", key = "#p0")
    int[] insertDataSourceUsersProfile(int profileId, List<Integer> toInsert);
    @CacheEvict(cacheNames = "dataSourceProfilePermissions", key = "#p0")
    int[] deleteDataSourceUsersProfile(int profileId, List<Integer> toDelete);

    @Cacheable(cacheNames = "dataPointProfilePermissions")
    List<DataPointAccess> selectDataPointPermissionsByProfileId(int profileId);
    @CacheEvict(cacheNames = "dataPointProfilePermissions", key = "#p0")
    int[] insertDataPointUsersProfile(int profileId, List<DataPointAccess> toInsert);
    @CacheEvict(cacheNames = "dataPointProfilePermissions", key = "#p0")
    int[] deleteDataPointUsersProfile(int profileId, List<DataPointAccess> toDelete);

    @Caching(evict = {
            @CacheEvict(cacheNames = "usersProfilesByUserId", allEntries = true),
            @CacheEvict(cacheNames = "usersProfilesByXid", allEntries = true),
            @CacheEvict(cacheNames = "usersProfilesById", allEntries = true),
            @CacheEvict(cacheNames = "userIdsByProfileId", allEntries = true),
            @CacheEvict(cacheNames = "dataPointProfilePermissions", allEntries = true)
    })
    default void resetDataPointPermissions() {}

    @Caching(evict = {
            @CacheEvict(cacheNames = "usersProfilesByUserId", allEntries = true),
            @CacheEvict(cacheNames = "usersProfilesByXid", allEntries = true),
            @CacheEvict(cacheNames = "usersProfilesById", allEntries = true),
            @CacheEvict(cacheNames = "userIdsByProfileId", allEntries = true),
            @CacheEvict(cacheNames = "dataSourceProfilePermissions", allEntries = true)
    })
    default void resetDataSourcePermissions() {}

    @Caching(evict = {
            @CacheEvict(cacheNames = "usersProfilesByUserId", allEntries = true),
            @CacheEvict(cacheNames = "usersProfilesByXid", allEntries = true),
            @CacheEvict(cacheNames = "usersProfilesById", allEntries = true),
            @CacheEvict(cacheNames = "userIdsByProfileId", allEntries = true),
            @CacheEvict(cacheNames = "viewProfilePermissions", allEntries = true)
    })
    default void resetViewPermissions() {}

    @Caching(evict = {
            @CacheEvict(cacheNames = "usersProfilesByUserId", allEntries = true),
            @CacheEvict(cacheNames = "usersProfilesByXid", allEntries = true),
            @CacheEvict(cacheNames = "usersProfilesById", allEntries = true),
            @CacheEvict(cacheNames = "userIdsByProfileId", allEntries = true),
            @CacheEvict(cacheNames = "watchListProfilePermissions", allEntries = true)
    })
    default void resetWatchListPermissions() {}
}
