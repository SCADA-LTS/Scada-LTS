package br.org.scadabr.vo.scripting;

import br.org.scadabr.rt.scripting.ContextualizedScriptRT;
import br.org.scadabr.rt.scripting.ScriptRT;
import br.org.scadabr.rt.scripting.context.ScriptContextObject;
import com.serotonin.json.*;
import com.serotonin.mango.util.ChangeComparable;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.lang3.tuple.MutablePair;
import org.scada_lts.mango.service.DataPointService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonRemoteEntity
public class ContextualizedScriptVO extends ScriptVO<ContextualizedScriptVO>
		implements ChangeComparable<ContextualizedScriptVO> {
	public static final Type TYPE = Type.CONTEXTUALIZED_SCRIPT;

	@Override
	public br.org.scadabr.vo.scripting.ScriptVO.Type getType() {
		return TYPE;
	}

//	private List<IntValuePair> pointsOnContext = new ArrayList<IntValuePair>();
//	private List<IntValuePair> objectsOnContext = new ArrayList<IntValuePair>();

	private List<MutablePair<Integer, String>> pointsOnContext2 = new ArrayList<>();
	private List<MutablePair<Integer, String>> objectsOnContext2 = new ArrayList<>();
	//
	// /
	// / Serialization
	// /
	//
	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
//		out.writeObject(pointsOnContext);
//		out.writeObject(objectsOnContext);
		out.writeObject(pointsOnContext2);
		out.writeObject(objectsOnContext2);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();
		// Switch on the version of the class so that version changes can be
		if (ver == 1) {
//			pointsOnContext = (List<IntValuePair>) in.readObject();
//			objectsOnContext = (List<IntValuePair>) in.readObject();
			pointsOnContext2 = (List<MutablePair<Integer,String>>) in.readObject();
			objectsOnContext2 = (List<MutablePair<Integer,String>>) in.readObject();
		}
	}

	@Override
	public ScriptRT createScriptRT() {
		return new ContextualizedScriptRT(this);
	}

	public  List<MutablePair<Integer, String>>  getPointsOnContext() {
		return pointsOnContext2;
	}

	public void setPointsOnContext( List<MutablePair<Integer, String>>  pointsOnContext) {
		this.pointsOnContext2 = pointsOnContext;
	}

	public void setObjectsOnContext( List<MutablePair<Integer, String>>  objectsOnContext) {
		this.objectsOnContext2 = objectsOnContext;
	}

	public List<MutablePair<Integer, String>> getObjectsOnContext() {
		return objectsOnContext2;
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
//			pointsOnContext.clear();
			pointsOnContext2.clear();
			DataPointService dataPointService = new DataPointService();

			for (JsonValue jv : jsonContext.getElements()) {
				JsonObject jo = jv.toJsonObject();
				String xid = jo.getString("dataPointXid");
				if (xid == null)
					throw new LocalizableJsonException(
							"emport.error.meta.missing", "dataPointXid");

				DataPointVO dp = dataPointService.getDataPoint(xid);
				if (dp == null)
					throw new LocalizableJsonException(
							"emport.error.missingPoint", xid);

				String var = jo.getString("varName");
				if (var == null)
					throw new LocalizableJsonException(
							"emport.error.meta.missing", "varName");

//				pointsOnContext.add(new IntValuePair(dp.getId(), var));
				pointsOnContext2.add(new MutablePair<>(dp.getId(), var));
			}
		}

		jsonContext = json.getJsonArray("objectsOnContext");
		if (jsonContext != null) {
//			objectsOnContext.clear();
			objectsOnContext2.clear();

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

//				objectsOnContext.add(new IntValuePair(key, var));
				objectsOnContext2.add(new MutablePair<>(key, var));
			}
		}

	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		super.jsonSerialize(map);
		List<Map<String, Object>> pointList = new ArrayList<Map<String, Object>>();
		for (MutablePair p : pointsOnContext2) {
			DataPointVO dp = new DataPointService().getDataPoint((int)p.getKey());
			if (dp != null) {
				Map<String, Object> point = new HashMap<String, Object>();
				pointList.add(point);
				point.put("varName", p.getValue());
				point.put("dataPointXid", dp.getXid());
			}
		}
		map.put("pointsOnContext", pointList);

		List<Map<String, Object>> objectsList = new ArrayList<Map<String, Object>>();
		for (MutablePair p : objectsOnContext2) {
			Map<String, Object> point = new HashMap<String, Object>();
			objectsList.add(point);
			point.put("varName", p.getValue().toString());
			point.put("objectId", p.getKey());
		}

		map.put("objectsOnContext", objectsList);

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((objectsOnContext2 == null) ? 0 : objectsOnContext2.hashCode());
		result = prime * result + ((pointsOnContext2 == null) ? 0 : pointsOnContext2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContextualizedScriptVO other = (ContextualizedScriptVO) obj;
		if (objectsOnContext2 == null) {
			if (other.objectsOnContext2 != null)
				return false;
		} else if (!objectsOnContext2.equals(other.objectsOnContext2))
			return false;
		if (pointsOnContext2 == null) {
			if (other.pointsOnContext2 != null)
				return false;
		} else if (!pointsOnContext2.equals(other.pointsOnContext2))
			return false;
		return true;
	}
	
	

}
