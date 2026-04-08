package br.org.scadabr.vo.exporter.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonSerializable;
import org.scada_lts.dao.ISystemSettingsDAO;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.web.beans.ApplicationBeans;

@JsonRemoteEntity
public class SystemSettingsJSONWrapper implements JsonSerializable {

	public SystemSettingsJSONWrapper() {
	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {

		Set<Entry<String, Object>> defaultValues = SystemSettingsDAO.DEFAULT_VALUES
				.entrySet();

		for (Entry<String, Object> entry : defaultValues) {
			if (entry.getValue() instanceof Integer) {
				map.put(entry.getKey(), SystemSettingsDAO.getIntValue(entry.getKey()));
			} else if (entry.getValue() instanceof Boolean) {
				map.put(entry.getKey(), SystemSettingsDAO.getBooleanValue(entry.getKey()));
			} else if (entry.getValue() instanceof String) {
				map.put(entry.getKey(), SystemSettingsDAO.getValue(entry.getKey()));
			}
		}

	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {

		Set<Entry<String, Object>> defaultValues = SystemSettingsDAO.DEFAULT_VALUES
				.entrySet();
		ISystemSettingsDAO systemSettingsDAO = ApplicationBeans.getSystemSettingsDaoBean();

		for (Entry<String, Object> entry : defaultValues) {
			String key = entry.getKey();

			if (entry.getValue() instanceof Integer) {
				systemSettingsDAO.setIntValue(key, json.getInt(key));
			} else if (entry.getValue() instanceof Boolean) {
				systemSettingsDAO.setBooleanValue(key, json.getBoolean(key));
			} else if (entry.getValue() instanceof String) {
				systemSettingsDAO.setValue(key, json.getString(key));
			}
		}
	}

}
