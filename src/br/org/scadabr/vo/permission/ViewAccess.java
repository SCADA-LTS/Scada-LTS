package br.org.scadabr.vo.permission;

import com.serotonin.json.*;
import com.serotonin.mango.dao_cache.DaoInstances;
import com.serotonin.mango.view.View;

import java.util.Map;

@JsonRemoteEntity
public class ViewAccess extends Permission implements JsonSerializable {

	public ViewAccess() {
		super();
	}

	public ViewAccess(int id, int permission) {
		super(id, permission);
	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		map.put("viewXid", DaoInstances.getViewDao().getView(id).getXid());
		map.put("permission", ACCESS_CODES.getCode(permission));
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		String xid = json.getString("viewXid");
		int ImportedPermission = ACCESS_CODES.getId(json
				.getString("permission"));
		View view = DaoInstances.getViewDao().getViewByXid(xid);
		int importedId = view.getId();
		setId(importedId);
		setPermission(ImportedPermission);
	}

}
