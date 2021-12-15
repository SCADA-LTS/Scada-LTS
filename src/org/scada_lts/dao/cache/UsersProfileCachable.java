package org.scada_lts.dao.cache;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.List;

public interface UsersProfileCachable {

    String CACHE_ENABLED_KEY = "usersprofile.cache.enabled";

    @Cacheable(cacheNames = "profile_list_by_userid", key = "#p0")
    List<UsersProfileVO> selectUserProfileByUserId(int userId);
    @Cacheable(cacheNames = "profile_list_offset_limit", key = "#offset + ':' + #limit")
    List<UsersProfileVO> selectProfiles(int offset, int limit);
    @Cacheable(cacheNames = "userid_list_by_profileid", key = "#p0")
    List<Integer> selectUsersByProfileId(int profileId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "profile_list_by_userid", key = "#p0"),
            @CacheEvict(cacheNames = "userid_list_by_profileid", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_watchlist", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true)
    })
    int deleteUserProfileByUserId(int userId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "profile_list_by_userid", key = "#p0"),
            @CacheEvict(cacheNames = "userid_list_by_profileid", key = "#p1"),
            @CacheEvict(cacheNames = "share_user_list_by_watchlist", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true)
    })
    int insertUserProfile(int userId, int profileId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "profile_list_by_userid", allEntries = true),
            @CacheEvict(cacheNames = "profile_list_offset_limit", allEntries = true)
    })
    int updateProfileName(String profileName, int profileId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "profile_list_by_userid", allEntries = true),
            @CacheEvict(cacheNames = "profile_list_offset_limit", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_watchlist", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true)
    })
    int insertProfile(String profileXid, String profileName);

    @Caching(evict = {
            @CacheEvict(cacheNames = "userid_list_by_profileid", key = "#p0"),
            @CacheEvict(cacheNames = "profile_list_offset_limit", allEntries = true),
            @CacheEvict(cacheNames = "profile_list_by_userid", allEntries = true),
            @CacheEvict(cacheNames = "permission_datasource_list_by_profile", key = "#p0"),
            @CacheEvict(cacheNames = "permission_datapoint_list_by_profile", key = "#p0"),
            @CacheEvict(cacheNames = "permission_watchlist_list_by_profile", key = "#p0"),
            @CacheEvict(cacheNames = "permission_view_list_by_profile", key = "#p0"),
            @CacheEvict(cacheNames = "share_user_list_by_watchlist", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true)

    })
    int deleteProfile(int profileId);


    @Cacheable(cacheNames = "permission_datasource_list_by_profile", key = "#p0")
    List<Integer> selectDataSourcePermissionsByProfileId(int profileId);

    @Caching(evict = {@CacheEvict(cacheNames = "permission_datasource_list_by_profile", key = "#p0")})
    int[] insertDataSourceUsersProfile(int profileId, List<Integer> toInsert);

    @Caching(evict = {@CacheEvict(cacheNames = "permission_datasource_list_by_profile", key = "#p0")})
    int[] deleteDataSourceUsersProfile(int profileId, List<Integer> toDelete);


    @Cacheable(cacheNames = "permission_datapoint_list_by_profile", key = "#p0")
    List<DataPointAccess> selectDataPointPermissionsByProfileId(int profileId);

    @Caching(evict = {@CacheEvict(cacheNames = "permission_datapoint_list_by_profile", key = "#p0")})
    int[] insertDataPointUsersProfile(int profileId, List<DataPointAccess> toInsert);

    @Caching(evict = {@CacheEvict(cacheNames = "permission_datapoint_list_by_profile", key = "#p0")})
    int[] deleteDataPointUsersProfile(int profileId, List<DataPointAccess> toDelete);


    @Cacheable(cacheNames = "permission_view_list_by_profile", key = "#p0")
    List<ViewAccess> selectViewPermissionsByProfileId(int profileId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "permission_view_list_by_profile", key = "#p0"),
            @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true)
    })
    int[] insertViewUsersProfile(int profileId, List<ViewAccess> toInsert);

    @Caching(evict = {
            @CacheEvict(cacheNames = "permission_view_list_by_profile", key = "#p0"),
            @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true)
    })
    int[] deleteViewUsersProfile(int profileId, List<ViewAccess> toDelete);


    @Cacheable(cacheNames = "permission_watchlist_list_by_profile", key = "#p0")
    List<WatchListAccess> selectWatchListPermissionsByProfileId(int profileId);

    @Caching(evict = {
            @CacheEvict(cacheNames = "permission_watchlist_list_by_profile", key = "#p0"),
            @CacheEvict(cacheNames = "share_user_list_by_watchlist", allEntries = true)
    })
    int[] insertWatchListUsersProfile(int profileId, List<WatchListAccess> toInsert);

    @Caching(evict = {
            @CacheEvict(cacheNames = "permission_watchlist_list_by_profile", key = "#p0"),
            @CacheEvict(cacheNames = "share_user_list_by_watchlist", allEntries = true)
    })
    int[] deleteWatchListUsersProfile(int profileId, List<WatchListAccess> toDelete);

    @Caching(evict = {
            @CacheEvict(cacheNames = "userid_list_by_profileid", allEntries = true),
            @CacheEvict(cacheNames = "profile_list_offset_limit", allEntries = true),
            @CacheEvict(cacheNames = "profile_list_by_userid", allEntries = true),
            @CacheEvict(cacheNames = "permission_datasource_list_by_profile", allEntries = true),
            @CacheEvict(cacheNames = "permission_datasource_list_by_user", allEntries = true)
    })
    default void resetCacheDataSourcePermissions() {}

    @Caching(evict = {
            @CacheEvict(cacheNames = "userid_list_by_profileid", allEntries = true),
            @CacheEvict(cacheNames = "profile_list_offset_limit", allEntries = true),
            @CacheEvict(cacheNames = "profile_list_by_userid", allEntries = true),
            @CacheEvict(cacheNames = "permission_datapoint_list_by_profile", allEntries = true),
            @CacheEvict(cacheNames = "permission_datapoint_list_by_user", allEntries = true)
    })
    default void resetCacheDataPointPermissions() {}

    @Caching(evict = {
            @CacheEvict(cacheNames = "userid_list_by_profileid", allEntries = true),
            @CacheEvict(cacheNames = "profile_list_offset_limit", allEntries = true),
            @CacheEvict(cacheNames = "profile_list_by_userid", allEntries = true),
            @CacheEvict(cacheNames = "permission_view_list_by_profile", allEntries = true),
            @CacheEvict(cacheNames = "permission_view_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_view", allEntries = true)
    })
    default void resetCacheViewPermissions() {}

    @Caching(evict = {
            @CacheEvict(cacheNames = "userid_list_by_profileid", allEntries = true),
            @CacheEvict(cacheNames = "profile_list_offset_limit", allEntries = true),
            @CacheEvict(cacheNames = "profile_list_by_userid", allEntries = true),
            @CacheEvict(cacheNames = "permission_watchlist_list_by_profile", allEntries = true),
            @CacheEvict(cacheNames = "permission_watchlist_list_by_user", allEntries = true),
            @CacheEvict(cacheNames = "share_user_list_by_watchlist", allEntries = true)
    })
    default void resetCacheWatchListPermissions() {}
}
