package org.scada_lts.dao;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface UsersProfileDaoCachable extends GenerateXid {

    @Cacheable(cacheNames = "profile_by_userid")
    List<UsersProfileVO> selectUserProfileByUserId(int userId);
    @Cacheable(cacheNames = "profile_offset_limit")
    List<UsersProfileVO> selectProfiles(int offset, int limit);
    @Cacheable(cacheNames = "userids_by_profileid")
    List<Integer> selectUsersByProfileId(int profileId);

    @Cacheable(cacheNames = "profile_by_id")
    Optional<UsersProfileVO> selectProfileById(int profileId);
    @Cacheable(cacheNames = "profile_by_xid")
    Optional<UsersProfileVO> selectProfileByXid(String profileXid);

    @Caching(evict = {
            @CacheEvict(cacheNames = "profile_by_userid", key = "#p0"),
            @CacheEvict(cacheNames = "userids_by_profileid", allEntries = true)
    })
    int deleteUserProfileByUserId(int userId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "profile_by_userid", key = "#p0"),
            @CacheEvict(cacheNames = "userids_by_profileid", key = "#p1")
    })
    int insertUserProfile(int userId, int profileId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "profile_by_xid", allEntries = true),
            @CacheEvict(cacheNames = "profile_by_userid", allEntries = true),
            @CacheEvict(cacheNames = "profile_by_id", key = "#p1"),
            @CacheEvict(cacheNames = "profile_offset_limit", allEntries = true)
    })
    int updateProfileName(String profileName, int profileId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "userids_by_profileid", allEntries = true),
            @CacheEvict(cacheNames = "profile_by_xid", allEntries = true),
            @CacheEvict(cacheNames = "profile_by_userid", allEntries = true),
            @CacheEvict(cacheNames = "profile_by_id", allEntries = true),
            @CacheEvict(cacheNames = "profile_offset_limit", allEntries = true)
    })
    int insertProfile(String profileXid, String profileName);

    @Caching(evict = {
            @CacheEvict(cacheNames = "profile_by_userid", allEntries = true),
            @CacheEvict(cacheNames = "userids_by_profileid", key = "#p0"),
            @CacheEvict(cacheNames = "profile_by_id", key = "#p0"),
            @CacheEvict(cacheNames = "permissions_profile_watchlist", key = "#p0"),
            @CacheEvict(cacheNames = "permissions_profile_view", key = "#p0"),
            @CacheEvict(cacheNames = "permissions_profile_datasource", key = "#p0"),
            @CacheEvict(cacheNames = "permissions_profile_datapoint", key = "#p0"),
            @CacheEvict(cacheNames = "profile_offset_limit", allEntries = true)
    })
    int deleteProfile(int profileId);

    @Cacheable(cacheNames = "permissions_profile_watchlist")
    List<WatchListAccess> selectWatchListPermissionsByProfileId(int profileId);
    @CacheEvict(cacheNames = "permissions_profile_watchlist", key = "#p0")
    int[] insertWatchListUsersProfile(int profileId, List<WatchListAccess> toInsert);
    @CacheEvict(cacheNames = "permissions_profile_watchlist", key = "#p0")
    int[] deleteWatchListUsersProfile(int profileId, List<WatchListAccess> toDelete);

    @Cacheable(cacheNames = "permissions_profile_view")
    List<ViewAccess> selectViewPermissionsByProfileId(int profileId);
    @CacheEvict(cacheNames = "permissions_profile_view", key = "#p0")
    int[] insertViewUsersProfile(int profileId, List<ViewAccess> toInsert);
    @CacheEvict(cacheNames = "permissions_profile_view", key = "#p0")
    int[] deleteViewUsersProfile(int profileId, List<ViewAccess> toDelete);

    @Cacheable(cacheNames = "permissions_profile_datasource")
    List<Integer> selectDataSourcePermissionsByProfileId(int profileId);
    @CacheEvict(cacheNames = "permissions_profile_datasource", key = "#p0")
    int[] insertDataSourceUsersProfile(int profileId, List<Integer> toInsert);
    @CacheEvict(cacheNames = "permissions_profile_datasource", key = "#p0")
    int[] deleteDataSourceUsersProfile(int profileId, List<Integer> toDelete);

    @Cacheable(cacheNames = "permissions_profile_datapoint")
    List<DataPointAccess> selectDataPointPermissionsByProfileId(int profileId);
    @CacheEvict(cacheNames = "permissions_profile_datapoint", key = "#p0")
    int[] insertDataPointUsersProfile(int profileId, List<DataPointAccess> toInsert);
    @CacheEvict(cacheNames = "permissions_profile_datapoint", key = "#p0")
    int[] deleteDataPointUsersProfile(int profileId, List<DataPointAccess> toDelete);

    @Caching(evict = {
            @CacheEvict(cacheNames = "profile_by_userid", allEntries = true),
            @CacheEvict(cacheNames = "profile_by_xid", allEntries = true),
            @CacheEvict(cacheNames = "profile_by_id", allEntries = true),
            @CacheEvict(cacheNames = "userids_by_profileid", allEntries = true),
            @CacheEvict(cacheNames = "permissions_profile_datapoint", allEntries = true),
            @CacheEvict(cacheNames = "profile_offset_limit", allEntries = true)
    })
    default void resetDataPointPermissions() {}

    @Caching(evict = {
            @CacheEvict(cacheNames = "profile_by_userid", allEntries = true),
            @CacheEvict(cacheNames = "profile_by_xid", allEntries = true),
            @CacheEvict(cacheNames = "profile_by_id", allEntries = true),
            @CacheEvict(cacheNames = "userids_by_profileid", allEntries = true),
            @CacheEvict(cacheNames = "permissions_profile_datasource", allEntries = true),
            @CacheEvict(cacheNames = "profile_offset_limit", allEntries = true)
    })
    default void resetDataSourcePermissions() {}

    @Caching(evict = {
            @CacheEvict(cacheNames = "profile_by_userid", allEntries = true),
            @CacheEvict(cacheNames = "profile_by_xid", allEntries = true),
            @CacheEvict(cacheNames = "profile_by_id", allEntries = true),
            @CacheEvict(cacheNames = "userids_by_profileid", allEntries = true),
            @CacheEvict(cacheNames = "permissions_profile_view", allEntries = true),
            @CacheEvict(cacheNames = "profile_offset_limit", allEntries = true)
    })
    default void resetViewPermissions() {}

    @Caching(evict = {
            @CacheEvict(cacheNames = "profile_by_userid", allEntries = true),
            @CacheEvict(cacheNames = "profile_by_xid", allEntries = true),
            @CacheEvict(cacheNames = "profile_by_id", allEntries = true),
            @CacheEvict(cacheNames = "userids_by_profileid", allEntries = true),
            @CacheEvict(cacheNames = "permissions_profile_watchlist", allEntries = true),
            @CacheEvict(cacheNames = "profile_offset_limit", allEntries = true)
    })
    default void resetWatchListPermissions() {}
}
