package br.org.scadabr.vo.permission;

import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.db.dao.ViewDao;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;

@JsonRemoteEntity
public class ViewAccess extends Permission implements JsonSerializable {

	public ViewAccess() {
		super();
	}

	public ViewAccess(int id, int permission) {
		super(id, permission);
	}

	public static ViewAccess none(int viewId) {
		return new ViewAccess(viewId, ShareUser.ACCESS_NONE);
	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		map.put("viewXid", new ViewDao().getView(id).getXid());
		map.put("permission", ACCESS_CODES.getCode(permission));
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		String xid = json.getString("viewXid");
		int ImportedPermission = ACCESS_CODES.getId(json
				.getString("permission"));
		View view = new ViewDao().getViewByXid(xid);
		int importedId = view.getId();
		setId(importedId);
		setPermission(ImportedPermission);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ViewAccess)) return false;
		return super.equals(o);
	}
}
