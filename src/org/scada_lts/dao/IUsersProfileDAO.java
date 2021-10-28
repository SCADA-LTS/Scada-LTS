package org.scada_lts.dao;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface IUsersProfileDAO extends GenerateXid {

    Optional<UsersProfileVO> selectProfileById(int profileId);
    Optional<UsersProfileVO> selectProfileByXid(String profileXid);

    List<UsersProfileVO> selectUserProfileByUserId(int userId);
    List<UsersProfileVO> selectProfiles(int offset, int limit);
    List<Integer> selectUsersByProfileId(int profileId);

    int deleteUserProfileByUserId(int userId);
    int insertUserProfile(int userId, int profileId);
    int updateProfileName(String profileName, int profileId);
    int insertProfile(String profileXid, String profileName);
    int deleteProfile(int profileId);

    List<Integer> selectDataSourcePermissionsByProfileId(int profileId);
    int[] insertDataSourceUsersProfile(int profileId, List<Integer> toInsert);
    int[] deleteDataSourceUsersProfile(int profileId, List<Integer> toDelete);

    List<DataPointAccess> selectDataPointPermissionsByProfileId(int profileId);
    int[] insertDataPointUsersProfile(int profileId, List<DataPointAccess> toInsert);
    int[] deleteDataPointUsersProfile(int profileId, List<DataPointAccess> toDelete);

    List<ViewAccess> selectViewPermissionsByProfileId(int profileId);
    int[] insertViewUsersProfile(int profileId, List<ViewAccess> toInsert);
    int[] deleteViewUsersProfile(int profileId, List<ViewAccess> toDelete);

    List<WatchListAccess> selectWatchListPermissionsByProfileId(int profileId);
    int[] insertWatchListUsersProfile(int profileId, List<WatchListAccess> toInsert);
    int[] deleteWatchListUsersProfile(int profileId, List<WatchListAccess> toDelete);

    default void resetDataSourcePermissions(){}
    default void resetDataPointPermissions(){}
    default void resetViewPermissions(){}
    default void resetWatchListPermissions(){}
}
