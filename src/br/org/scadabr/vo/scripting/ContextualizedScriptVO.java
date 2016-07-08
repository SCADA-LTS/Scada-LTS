package br.org.scadabr.vo.scripting;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.org.scadabr.rt.scripting.ContextualizedScriptRT;
import br.org.scadabr.rt.scripting.ScriptRT;
import br.org.scadabr.rt.scripting.context.ScriptContextObject;

import com.serotonin.db.IntValuePair;
import com.serotonin.json.JsonArray;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonValue;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.util.ChangeComparable;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class ContextualizedScriptVO extends ScriptVO<ContextualizedScriptVO>
		implements ChangeComparable<ContextualizedScriptVO> {
	public static final Type TYPE = Type.CONTEXTUALIZED_SCRIPT;

	@Override
	public br.org.scadabr.vo.scripting.ScriptVO.Type getType() {
		return TYPE;
	}

	private List<IntValuePair> pointsOnContext = new ArrayList<IntValuePair>();
	private List<IntValuePair> objectsOnContext = new ArrayList<IntValuePair>();

	//
	// /
	// / Serialization
	// /
	//
	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		out.writeObject(pointsOnContext);
		out.writeObject(objectsOnContext);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();
		// Switch on the version of the class so that version changes can be
		if (ver == 1) {
			pointsOnContext = (List<IntValuePair>) in.readObject();
			objectsOnContext = (List<IntValuePair>) in.readObject();
		}
	}

	@Override
	public ScriptRT createScriptRT() {
		return new ContextualizedScriptRT(this);
	}

	public List<IntValuePair> getPointsOnContext() {
		return pointsOnContext;
	}

	public void setPointsOnContext(List<IntValuePair> pointsOnContext) {
		this.pointsOnContext = pointsOnContext;
	}

	public void setObjectsOnContext(List<IntValuePair> objectsOnContext) {
		this.objectsOnContext = objectsOnContext;
	}

	public List<IntValuePair> getObjectsOnContext() {
		return objectsOnContext;
	}

	@Override
	public String getTypeKey() {
		return "event.audit.scripts";
	}

	@Override
	public void addPropertyChanges(List<LocalizableMessage> list,
			ContextualizedScriptVO from) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addProperties(List<LocalizableMessage> list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {

		super.jsonDeserialize(reader, json);
		JsonArray jsonContext = json.getJsonArray("pointsOnContext");
		if (jsonContext != null) {
			pointsOnContext.clear();
			DataPointDao dataPointDao = new DataPointDao();

			for (JsonValue jv : jsonContext.getElements()) {
				JsonObject jo = jv.toJsonObject();
				String xid = jo.getString("dataPointXid");
				if (xid == null)
					throw new LocalizableJsonException(
							"emport.error.meta.missing", "dataPointXid");

				DataPointVO dp = dataPointDao.getDataPoint(xid);
				if (dp == null)
					throw new LocalizableJsonException(
							"emport.error.missingPoint", xid);

				String var = jo.getString("varName");
				if (var == null)
					throw new LocalizableJsonException(
							"emport.error.meta.missing", "varName");

				pointsOnContext.add(new IntValuePair(dp.getId(), var));
			}
		}

		jsonContext = json.getJsonArray("objectsOnContext");
		if (jsonContext != null) {
			objectsOnContext.clear();

			for (JsonValue jv : jsonContext.getElements()) {
				JsonObject jo = jv.toJsonObject();
				int key = jo.getInt("objectId");

				ScriptContextObject.Type objectType = ScriptContextObject.Type
						.valueOf(key);

				if (objectType == null)
					throw new LocalizableJsonException(
							"emport.error.missingPoint", key);

				String var = jo.getString("varName");
				if (var == null)
					throw new LocalizableJsonException(
							"emport.error.meta.missing", "varName");

				objectsOnContext.add(new IntValuePair(key, var));
			}
		}

	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		super.jsonSerialize(map);
		List<Map<String, Object>> pointList = new ArrayList<Map<String, Object>>();
		for (IntValuePair p : pointsOnContext) {
			DataPointVO dp = new DataPointDao().getDataPoint(p.getKey());
			if (dp != null) {
				Map<String, Object> point = new HashMap<String, Object>();
				pointList.add(point);
				point.put("varName", p.getValue());
				point.put("dataPointXid", dp.getXid());
			}
		}
		map.put("pointsOnContext", pointList);

		List<Map<String, Object>> objectsList = new ArrayList<Map<String, Object>>();
		for (IntValuePair p : objectsOnContext) {
			Map<String, Object> point = new HashMap<String, Object>();
			objectsList.add(point);
			point.put("varName", p.getValue());
			point.put("objectId", p.getKey());
		}

		map.put("objectsOnContext", objectsList);

	}

}
