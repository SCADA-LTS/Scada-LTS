package br.org.scadabr.vo.usersProfiles;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import com.serotonin.json.*;
import com.serotonin.mango.Common;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.web.dwr.DwrResponseI18n;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.serorepl.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class DeserializeUsersProfileUtils {

    private static final Log LOG = LogFactory.getLog(DeserializeUsersProfileUtils.class);

    private DeserializeUsersProfileUtils() {}

    public static List<Integer> getDataSourcePermissions(JsonObject profileJson, DataSourceService dataSourceService)
            throws JsonException {
        List<Integer> dataSourceIds = getDataSourcePermissionsById(profileJson, dataSourceService);
        if (dataSourceIds.isEmpty())
            dataSourceIds = getDataSourcePermissionsByXid(profileJson, dataSourceService);
        return dataSourceIds;
    }

    public static List<DataPointAccess> getDataPointPermissions(
            JsonObject profileJson, JsonReader reader) throws JsonException {
        JsonArray jsonPoints = profileJson.getJsonArray("dataPointPermissions");
        List<DataPointAccess> dataPointPermissions = new ArrayList<>();
        List<Integer> permittedPoints = new ArrayList<>();

        for (JsonValue jv : jsonPoints.getElements()) {
            try {
                DataPointAccess access = reader.readPropertyValue(jv, DataPointAccess.class, null);
                if (access.getDataPointId() == Common.NEW_ID) {
                }
                if (!permittedPoints.contains(access.getDataPointId()) && access.getDataPointId() != Common.NEW_ID) {
                    dataPointPermissions.add(access);
                    permittedPoints.add(access.getDataPointId());
                }
            } catch (LocalizableJsonException ex) {
                LOG.warn(ex.getMsg().getLocalizedMessage(Common.getBundle()));
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
        return dataPointPermissions;
    }

    public static List<ViewAccess> getViewPermissions(JsonObject profileJson,
                                                      JsonReader reader) throws JsonException {
        List<ViewAccess> viewPermissions = new ArrayList<ViewAccess>();
        List<Integer> permittedViews = new ArrayList<Integer>();

        JsonArray viewsJson = profileJson.getJsonArray("viewPermissions");

        for (JsonValue jv : viewsJson.getElements()) {
            try {
                ViewAccess access = reader.readPropertyValue(jv, ViewAccess.class, null);
                if (!permittedViews.contains(access.getId())) {
                    viewPermissions.add(access);
                    permittedViews.add(access.getId());
                }
            } catch (LocalizableJsonException ex) {
                LOG.warn(ex.getMsg().getLocalizedMessage(Common.getBundle()));
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        return viewPermissions;
    }

    public static List<WatchListAccess> getWatchlistPermissions(
            JsonObject profileJson, JsonReader reader) throws JsonException {

        List<WatchListAccess> watchlistPermissions = new ArrayList<WatchListAccess>();
        List<Integer> permittedWatchlist = new ArrayList<Integer>();

        JsonArray viewsJson = profileJson.getJsonArray("watchlistPermissions");

        for (JsonValue jv : viewsJson.getElements()) {
            try {
                WatchListAccess access = reader.readPropertyValue(jv,
                        WatchListAccess.class, null);
                if (!permittedWatchlist.contains(access.getId())) {
                    watchlistPermissions.add(access);
                    permittedWatchlist.add(access.getId());
                }
            } catch (LocalizableJsonException ex) {
                LOG.warn(ex.getMsg().getLocalizedMessage(Common.getBundle()));
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        return watchlistPermissions;
    }

    public static List<User> getUsersOnProfile(JsonObject profileJson) throws JsonException {
        List<User> users = new ArrayList<>();
        JsonArray jsonUsersIds = profileJson.getJsonArray("usersIds");

        for (JsonValue jv : jsonUsersIds.getElements()) {
            int userid = jv.toJsonNumber().getIntValue();
            User user = new UserService().getUser(userid);
            users.add(user);
        }

        return users;
    }

    public static List<User> getUsersOnProfile(List<JsonValue> usersJson, UsersProfileVO profileJson,
                                               UserService userService, DwrResponseI18n response) throws JsonException {
        List<User> users = new ArrayList<>();
        int profileId = profileJson.getId();
        String profileXid = profileJson.getXid();
        for (JsonValue jv : usersJson) {
            if (profileById(profileId, jv.toJsonObject()) || profileByXid(profileXid, jv.toJsonObject())) {
                String username = jv.toJsonObject().getString("username");
                User user = userService.getUser(username);
                if (user == null) {
                    response.addGenericMessage("emport.user.prefix", username, "user does not exist");
                } else {
                    users.add(user);
                }
            }
        }
        return users;
    }

    private static boolean profileById(int id, JsonObject jsonObject) {
        if(isNewId(id)) {
            return false;
        }
        int userProfileId = jsonObject.getInt("userProfile");
        if(isNewId(userProfileId)) {
            return false;
        }
        return id == userProfileId;
    }

    private static boolean isNewId(int userProfileId) {
        return userProfileId == 0 || userProfileId == Common.NEW_ID;
    }

    private static boolean profileByXid(String xid, JsonObject jsonObject) {
        if(StringUtils.isEmpty(xid)) {
            return false;
        }
        String userProfileXid = jsonObject.getString("userProfileXid");
        if(StringUtils.isEmpty(userProfileXid)) {
            return false;
        }
        return xid.equals(userProfileXid);
    }

    private static List<Integer> reduce(List<String> objects, DataSourceService verify) {
        return objects.stream()
                .distinct()
                .map(a -> getDataSourceByXid(verify, a))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(DataSourceVO::getId)
                .collect(Collectors.toList());
    }

    private static List<Integer> reduceInt(List<Integer> objects, DataSourceService verify) {
        return objects.stream()
                .distinct()
                .map(a -> getDataSourceById(verify, a))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(DataSourceVO::getId)
                .collect(Collectors.toList());
    }

    private static Optional<DataSourceVO<?>> getDataSourceByXid(DataSourceService dataSourceService, String xid) {
        try {
            DataSourceVO<?> object = dataSourceService.getDataSource(xid);
            if(object == null) {
                LOG.info("datasource: " +  xid + ", msg: does not exist");
                return Optional.empty();
            }
            return Optional.of(object);
        } catch (Exception ex) {
            LOG.warn("datasource: " + xid + ", msg: " + (ex.getMessage() == null ? "no message" : ex.getMessage()));
            return Optional.empty();
        }
    }

    private static Optional<DataSourceVO<?>> getDataSourceById(DataSourceService dataSourceService, int id) {
        try {
            DataSourceVO<?> object = dataSourceService.getDataSource(id);
            if(object == null) {
                LOG.info("datasource: " +  id + ", msg: does not exist");
                return Optional.empty();
            }
            return Optional.of(object);
        } catch (Exception ex) {
            LOG.warn("datasource: " + id + ", msg: " + (ex.getMessage() == null ? "no message" : ex.getMessage()));
            return Optional.empty();
        }
    }

    private static List<Integer> getDataSourcePermissionsById(JsonObject profileJson, DataSourceService dataSourceService) throws JsonException {
        List<Integer> dataSourcePermissions = new ArrayList<>();

        JsonArray jsonDataSources = profileJson
                .getJsonArray("dataSourcePermissions");

        if (jsonDataSources != null) {
            for (JsonValue jv : jsonDataSources.getElements()) {
                int id = toNumber(jv);
                if(id != Common.NEW_ID) {
                    dataSourcePermissions.add(id);
                }
            }
        }
        return reduceInt(dataSourcePermissions, dataSourceService);
    }

    private static List<Integer> getDataSourcePermissionsByXid(JsonObject profileJson, DataSourceService dataSourceService)
            throws JsonException {
        List<String> dataSourcePermissions = new ArrayList<>();

        JsonArray jsonDataSources = profileJson
                .getJsonArray("dataSourcePermissionsXid");

        if (jsonDataSources != null) {
            for (JsonValue jv : jsonDataSources.getElements()) {
                String xid = toString(jv);
                if(!xid.isEmpty())
                    dataSourcePermissions.add(xid);
            }
        }
        return reduce(dataSourcePermissions, dataSourceService);
    }

    private static int toNumber(JsonValue jv) {
        try {
            return jv.toJsonNumber().getIntValue();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return Common.NEW_ID;
        }
    }

    private static String toString(JsonValue jv) {
        try {
            return jv.toJsonString().getValue();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return "";
        }
    }
}
