package utils;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.dao.IUsersProfileDAO;
import org.scada_lts.utils.XidUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class UsersProfileDAOMemory implements IUsersProfileDAO {

    private final Map<Integer, UsersProfileVO> profiles;
    private Map<Integer, UsersProfileVO> userProfiles;
    private final List<User> users;

    private final static AtomicInteger id = new AtomicInteger();

    public UsersProfileDAOMemory(Map<Integer, UsersProfileVO> profiles,
                                 Map<Integer, UsersProfileVO> userProfiles) {
        super();
        this.profiles = profiles;
        this.users = userProfiles.keySet().stream().map(User::onlyId).collect(Collectors.toList());
        this.userProfiles = userProfiles;
    }

    public UsersProfileDAOMemory(Map<Integer, UsersProfileVO> profiles,
                                 List<User> users) {
        super();
        this.profiles = profiles;
        this.users = users;
        this.userProfiles = new HashMap<>();
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
        getUserProfiles().put(userId, profiles.entrySet().stream()
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
    public Optional<UsersProfileVO> selectProfileById(int profileId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<UsersProfileVO> selectProfileByXid(String profileXid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<UsersProfileVO> selectUserProfileByUserId(int userId) {
        return getUserProfiles().entrySet().stream()
                .filter(a -> a.getKey() == userId)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public int deleteUserProfileByUserId(int userId) {
        return getUserProfiles().remove(userId).getId();
    }

    @Override
    public List<Integer> selectUsersByProfileId(int usersProfileId) {
        return getUserProfiles().entrySet().stream()
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
    public int[] deleteDataPointUsersProfile(int profileId, List<DataPointAccess> toDelete) {
        throw new UnsupportedOperationException();
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
    public int[] deleteDataSourceUsersProfile(int profileId, List<Integer> toDelete) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] insertViewUsersProfile(int profileId, List<ViewAccess> toInsert) {
        Set<ViewAccess> viewAccesses = new HashSet<>();
        viewAccesses.addAll(toInsert);
        viewAccesses.addAll(profiles.get(profileId).getViewPermissions());
        profiles.get(profileId).setViewPermissions(new ArrayList<>(viewAccesses));
        return new int[]{};
    }

    @Override
    public int[] deleteViewUsersProfile(int profileId, List<ViewAccess> toDelete) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] insertWatchListUsersProfile(int profileId, List<WatchListAccess> toInsert) {
        Set<WatchListAccess> watchListAccesses = new HashSet<>();
        watchListAccesses.addAll(toInsert);
        watchListAccesses.addAll(profiles.get(profileId).getWatchlistPermissions());
        profiles.get(profileId).setWatchlistPermissions(new ArrayList<>(watchListAccesses));
        return new int[]{};
    }

    @Override
    public int[] deleteWatchListUsersProfile(int profileId, List<WatchListAccess> toDelete) {
        throw new UnsupportedOperationException();
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
    public List<ViewAccess> selectViewPermissionsByProfileId(int profileId) {
        UsersProfileVO profile = selectProfiles(0, Integer.MAX_VALUE).stream().filter(a -> a.getId() == profileId).findAny().orElse(null);
        return profile == null ? Collections.emptyList() : profile.getViewPermissions();
    }

    @Override
    public List<WatchListAccess> selectWatchListPermissionsByProfileId(int profileId) {
        UsersProfileVO profile = selectProfiles(0, Integer.MAX_VALUE).stream().filter(a -> a.getId() == profileId).findAny().orElse(null);
        return profile == null ? Collections.emptyList() : profile.getWatchlistPermissions();
    }

    @Override
    public String generateUniqueXid(String prefix) {
        return XidUtils.generateXid(prefix);
    }

    private Map<Integer, UsersProfileVO> getUserProfiles() {
        if(userProfiles.isEmpty())
            return users.stream().filter(a -> profiles.containsKey(a.getUserProfile())).collect(Collectors.toMap(User::getId, a -> profiles.get(a.getUserProfile())));
        return userProfiles;
    }
}
