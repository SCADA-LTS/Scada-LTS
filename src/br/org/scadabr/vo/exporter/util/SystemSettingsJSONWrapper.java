package br.org.scadabr.vo.exporter.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.db.dao.SystemSettingsDao;

@JsonRemoteEntity
public class SystemSettingsJSONWrapper implements JsonSerializable {

	public SystemSettingsJSONWrapper() {
	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {

		Set<Entry<String, Object>> defaultValues = SystemSettingsDao.DEFAULT_VALUES
				.entrySet();
		SystemSettingsDao dao = new SystemSettingsDao();

		for (Entry<String, Object> entry : defaultValues) {
			if (entry.getValue() instanceof Integer) {
				map.put(entry.getKey(), dao.getIntValue(entry.getKey()));
			} else if (entry.getValue() instanceof Boolean) {
				map.put(entry.getKey(), dao.getBooleanValue(entry.getKey()));
			} else if (entry.getValue() instanceof String) {
				map.put(entry.getKey(), dao.getValue(entry.getKey()));
			}
		}

	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {

		Set<Entry<String, Object>> defaultValues = SystemSettingsDao.DEFAULT_VALUES
				.entrySet();
		SystemSettingsDao dao = new SystemSettingsDao();

		for (Entry<String, Object> entry : defaultValues) {
			String key = entry.getKey();

			if (entry.getValue() instanceof Integer) {
				dao.setIntValue(key, json.getInt(key));
			} else if (entry.getValue() instanceof Boolean) {
				dao.setBooleanValue(key, json.getBoolean(key));
			} else if (entry.getValue() instanceof String) {
				dao.setValue(key, json.getString(key));
			}
		}
	}

}