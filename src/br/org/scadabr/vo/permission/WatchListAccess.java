package br.org.scadabr.vo.permission;

import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.db.dao.WatchListDao;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.WatchList;

@JsonRemoteEntity
public class WatchListAccess extends Permission implements JsonSerializable {

	public WatchListAccess() {
		super();
	}

	public WatchListAccess(int id, int permission) {
		super(id, permission);
	}

	public static WatchListAccess none(int watchListId) {
		return new WatchListAccess(watchListId, ShareUser.ACCESS_NONE);
	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		map.put("watchlistXid", new WatchListDao().getWatchList(id).getXid());
		map.put("permission", ACCESS_CODES.getCode(permission));
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		String xid = json.getString("watchlistXid");
		int ImportedPermission = ACCESS_CODES.getId(json
				.getString("permission"));
		WatchList watchlist = new WatchListDao().getWatchList(xid);
		int importedId = watchlist.getId();
		setId(importedId);
		setPermission(ImportedPermission);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof WatchListAccess)) return false;
		return super.equals(o);
	}

	@Override
	public String toString() {
		return "WatchListAccess{" +
				"id=" + id +
				", permission=" + permission +
				'}';
	}
}
