package br.org.scadabr.vo.permission;

import com.serotonin.json.*;
import com.serotonin.mango.dao_cache.DaoInstances;
import com.serotonin.mango.vo.WatchList;

import java.util.Map;

@JsonRemoteEntity
public class WatchListAccess extends Permission implements JsonSerializable {

	public WatchListAccess() {
		super();
	}

	public WatchListAccess(int id, int permission) {
		super(id, permission);
	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		map.put("watchlistXid", DaoInstances.WatchListDao.getWatchList(id).getXid());
		map.put("permission", ACCESS_CODES.getCode(permission));
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		String xid = json.getString("watchlistXid");
		int ImportedPermission = ACCESS_CODES.getId(json
				.getString("permission"));
		WatchList watchlist = DaoInstances.WatchListDao.getWatchList(xid);
		int importedId = watchlist.getId();
		setId(importedId);
		setPermission(ImportedPermission);
	}
}
