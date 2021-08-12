package utils;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.dao.UsersProfileDAO;
import org.scada_lts.utils.XidUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class UsersProfileDAOMemory extends UsersProfileDAO {

    private final Map<Integer, UsersProfileVO> profiles;
    private final Map<Integer, UsersProfileVO> userProfiles;

    private final static AtomicInteger id = new AtomicInteger();

    public UsersProfileDAOMemory(Map<Integer, UsersProfileVO> profiles,
                                 Map<Integer, UsersProfileVO> userProfiles) {
        super(null);
        this.profiles = profiles;
        this.userProfiles = userProfiles;
    }

    @Override
    public int insertProfile(String usersProfileXid, String usersProfileName) {
        UsersProfileVO profile = new UsersProfileVO();
        profile.setId(id.incrementAndGet());
        profile.setXid(usersProfileXid);
        profile.setName(usersProfileName);
        profiles.put(profile.getId(), profile);
        return profile.getId();
    }

    @Override
    public int insertUserProfile(int userId, int userProfileId) {
        userProfiles.put(userId, profiles.entrySet().stream()
                .filter(a -> a.getValue().getId() == userProfileId)
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(null));
        return userProfileId;
    }

    @Override
    public List<UsersProfileVO> selectProfiles(int offset, int limit) {
        return profiles.entrySet().stream()
                .limit(limit)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsersProfileVO> selectUserProfileByUserId(int userId) {
        return userProfiles.entrySet().stream()
                .filter(a -> a.getKey() == userId)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public int deleteUserProfileByUserId(int userId) {
        return userProfiles.remove(userId).getId();
    }

    @Override
    public List<Integer> selectUsersByProfileId(int usersProfileId) {
        return userProfiles.entrySet().stream()
                .filter(a -> a.getValue().getId() == usersProfileId)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public int updateProfileName(String name, int usersProfileId) {
        UsersProfileVO profile = profiles.get(usersProfileId);
        profile.setName(name);
        return 1;
    }

    @Override
    public int[] insertDataPointUsersProfile(int profileId, List<DataPointAccess> toInsert) {
        Set<DataPointAccess> dataPointAccesses = new HashSet<>();
        dataPointAccesses.addAll(toInsert);
        dataPointAccesses.addAll(profiles.get(profileId).getDataPointPermissions());
        profiles.get(profileId).setDataPointPermissions(new ArrayList<>(dataPointAccesses));
        return new int[]{};
    }

    @Override
    public int[] insertDataSourceUsersProfile(int profileId, List<Integer> toInsert) {
        Set<Integer> dataPointAccesses = new HashSet<>();
        dataPointAccesses.addAll(toInsert);
        dataPointAccesses.addAll(profiles.get(profileId).getDataSourcePermissions());
        profiles.get(profileId).setDataSourcePermissions(new ArrayList<>(dataPointAccesses));
        return new int[]{};
    }

    @Override
    public int deleteProfile(int profileId) {
        profiles.entrySet().removeIf(a -> a.getValue().getId() == profileId);
        return 0;
    }

    @Override
    public List<Integer> selectDataSourcePermissionsByProfileId(int profileId) {
        UsersProfileVO profile = selectProfiles(0, Integer.MAX_VALUE).stream().filter(a -> a.getId() == profileId).findAny().orElse(null);
        return profile == null ? Collections.emptyList() : profile.getDataSourcePermissions();
    }

    @Override
    public List<DataPointAccess> selectDataPointPermissionsByProfileId(int profileId) {
        UsersProfileVO profile = selectProfiles(0, Integer.MAX_VALUE).stream().filter(a -> a.getId() == profileId).findAny().orElse(null);
        return profile == null ? Collections.emptyList() : profile.getDataPointPermissions();
    }

    @Override
    public String generateUniqueXid(String prefix) {
        return XidUtils.generateXid(prefix);
    }
}
