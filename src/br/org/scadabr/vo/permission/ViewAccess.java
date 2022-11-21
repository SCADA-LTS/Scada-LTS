package br.org.scadabr.vo.permission;

import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import org.scada_lts.mango.service.ViewService;

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
		map.put("viewXid", new ViewService().getView(id).getXid());
		map.put("permission", ACCESS_CODES.getCode(permission));
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		ViewService viewService = new ViewService();
		DeserializePermissionUtils
				.updatePermission(this, viewService::getViewByXid, View::getId,
						json, "viewXid", "view: {0}");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ViewAccess)) return false;
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return "ViewAccess{" +
				"id=" + id +
				", permission=" + permission +
				'}';
	}
}
