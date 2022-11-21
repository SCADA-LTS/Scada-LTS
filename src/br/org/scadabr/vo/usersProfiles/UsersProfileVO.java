package br.org.scadabr.vo.usersProfiles;

import java.util.*;
import java.util.stream.Collectors;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;

import com.serotonin.json.*;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.mango.service.DataSourceService;

@JsonRemoteEntity
public class UsersProfileVO implements Cloneable, JsonSerializable {

	public static final String XID_PREFIX = "UP_";

	@JsonRemoteProperty
	private String name;

	@JsonRemoteProperty
	private int id = Common.NEW_ID;

	private List<Integer> dataSourcePermissions;

	private List<DataPointAccess> dataPointPermissions;

	private List<WatchListAccess> watchlistPermissions;

	private List<ViewAccess> viewPermissions;

	@JsonRemoteProperty
	private String xid;

	private User lastAppliedUser = null;

	@Deprecated
	private List<WatchList> watchlists;

	@Deprecated
	private List<Integer> usersIds;

	@Deprecated
	private List<View> views;

	public UsersProfileVO(UsersProfileVO usersProfile) {
		this.name = usersProfile.name;
		this.id = usersProfile.id;
		this.dataSourcePermissions = new ArrayList<>(usersProfile.dataSourcePermissions);
		this.dataPointPermissions = new ArrayList<>(usersProfile.dataPointPermissions);
		this.watchlistPermissions = new ArrayList<>(usersProfile.watchlistPermissions);
		this.viewPermissions = new ArrayList<>(usersProfile.viewPermissions);
		this.xid = usersProfile.xid;
		if(usersProfile.lastAppliedUser != null)
			this.lastAppliedUser = new User(usersProfile.lastAppliedUser);
		this.watchlists = new ArrayList<>(usersProfile.watchlists);
		this.usersIds = new ArrayList<>(usersProfile.usersIds);
		this.views = new ArrayList<>(usersProfile.views);
	}

	public UsersProfileVO() {
		name = "";
		dataSourcePermissions = new ArrayList<Integer>();
		dataPointPermissions = new ArrayList<DataPointAccess>();
		watchlistPermissions = new ArrayList<WatchListAccess>();
		viewPermissions = new ArrayList<ViewAccess>();
		watchlists = new ArrayList<WatchList>();
		views = new ArrayList<View>();
		usersIds = new ArrayList<Integer>();
	}

	public String getXid() {
		return xid;
	}

	public void setXid(String xid) {
		this.xid = xid;
	}

	public String getName() {
		return name;
	}

	public void setName(String username) {
		this.name = username;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Integer> getDataSourcePermissions() {
		return dataSourcePermissions;
	}

	public void setDataSourcePermissions(List<Integer> dataSourcePermissions) {
		this.dataSourcePermissions = dataSourcePermissions;
	}

	public List<DataPointAccess> getDataPointPermissions() {
		return dataPointPermissions;
	}

	public void setDataPointPermissions(
			List<DataPointAccess> dataPointPermissions) {
		this.dataPointPermissions = dataPointPermissions;
	}

	public List<WatchListAccess> getWatchlistPermissions() {
		return watchlistPermissions;
	}

	public void setWatchlistPermissions(
			List<WatchListAccess> watchlistPermissions) {
		this.watchlistPermissions = watchlistPermissions;
	}

	public void setViewPermissions(List<ViewAccess> viewPermissions) {
		this.viewPermissions = viewPermissions;
	}

	public List<ViewAccess> getViewPermissions() {
		return this.viewPermissions;
	}

	// some getters/setters were renamed to retrieve/define to avoid Serotonin
	// JSON writer implementation to find them
	public User retrieveLastAppliedUser() {
		return lastAppliedUser;
	}

    @Deprecated
	public void defineWatchlists(List<WatchList> watchlists) {
		this.watchlists = watchlists;
	}

    @Deprecated
	public List<WatchList> retrieveWatchlists() {
		return this.watchlists;
	}

    @Deprecated
	public void defineViews(List<View> views) {
		this.views = views;
	}

    @Deprecated
	public List<View> retrieveViews() {
		return this.views;
	}

    @Deprecated
	public void defineUsers(List<Integer> users) {
		this.usersIds = users;
	}

	public void apply(User user) {
		user.setDataSourceProfilePermissions(dataSourcePermissions);
		user.setDataPointProfilePermissions(dataPointPermissions);
		user.setWatchListProfilePermissions(watchlistPermissions);
		user.setViewProfilePermissions(viewPermissions);
		user.setUserProfile(this);
		lastAppliedUser = user;
	}

	public void jsonDeserialize(JsonReader reader, JsonObject profileJson)
			throws JsonException {
		// Note: data source permissions are explicitly deserialized by the
		// import/export because the data sources and
		// points need to be certain to exist before we can resolve the xids.
		DataSourceService dataSourceService = new DataSourceService();
		dataSourcePermissions.addAll(DeserializeUsersProfileUtils.getDataSourcePermissions(profileJson, dataSourceService));
		dataPointPermissions.addAll(DeserializeUsersProfileUtils.getDataPointPermissions(profileJson, reader));
		viewPermissions.addAll(DeserializeUsersProfileUtils.getViewPermissions(profileJson, reader));
		watchlistPermissions.addAll(DeserializeUsersProfileUtils.getWatchlistPermissions(profileJson, reader));

		usersIds.addAll(DeserializeUsersProfileUtils.getUsersOnProfile(profileJson).stream()
				.filter(Objects::nonNull)
				.map(User::getId)
				.collect(Collectors.toList()));
	}

	public void jsonSerialize(Map<String, Object> map) {
		map.put("dataSourcePermissions", dataSourcePermissions);
		map.put("dataPointPermissions", dataPointPermissions);
		map.put("viewPermissions", viewPermissions);
		map.put("watchlistPermissions", watchlistPermissions);
		map.put("usersIds", usersIds);

		DataSourceService dataSourceService = new DataSourceService();
		Set<String> datasoureXids = dataSourcePermissions.stream()
				.filter(Objects::nonNull)
				.map(dataSourceService::getDataSource)
				.filter(Objects::nonNull)
				.map(DataSourceVO::getXid)
				.collect(Collectors.toSet());
		map.put("dataSourcePermissionsXid", datasoureXids);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UsersProfileVO)) return false;
		UsersProfileVO that = (UsersProfileVO) o;
		return getId() == that.getId();
	}

	@Override
	public int hashCode() {

		return Objects.hash(getId());
	}

	@Override
	public String toString() {
		return "\nUsersProfileVO{" +
				"name='" + name + '\'' +
				", id=" + id +
				", dataSourcePermissions=" + dataSourcePermissions +
				", dataPointPermissions=" + dataPointPermissions +
				", watchlistPermissions=" + watchlistPermissions +
				", viewPermissions=" + viewPermissions +
				", xid='" + xid + '\'' +
				", lastAppliedUser=" + lastAppliedUserId() +
				", watchlists=" + watchListsIds(watchlists) +
				", usersIds=" + usersIds +
				", views=" + viewsIds(views) +
				"}";
	}

    private Object lastAppliedUserId() {
        return lastAppliedUser == null ?  "null" : lastAppliedUser.getId();
    }

	@Deprecated
    private List<String> watchListsIds(List<WatchList> watchLists) {
		if(watchLists != null && !watchLists.isEmpty()) {
			return watchLists.stream().filter(Objects::nonNull)
                    .map(WatchList::getId)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Deprecated
	private List<String> viewsIds(List<View> views) {
		if(views != null && !views.isEmpty()) {
			return views.stream().filter(Objects::nonNull)
                    .map(View::getId)
					.map(String::valueOf)
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
}
