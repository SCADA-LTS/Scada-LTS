package br.org.scadabr.vo.eventDetectorTemplate;

import java.util.List;
import java.util.Map;

import br.org.scadabr.rt.EventDetectorTemplateRT;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

public class EventDetectorTemplateVO implements Cloneable, JsonSerializable {

	private String name;
	private int id;
	private List<PointEventDetectorVO> pointEventDetectors;

	public String getName() {
		return name;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void jsonDeserialize(JsonReader arg0, JsonObject arg1)
			throws JsonException {
		// TODO Auto-generated method stub
	}

	@Override
	public void jsonSerialize(Map<String, Object> arg0) {
		// TODO Auto-generated method stub
	}

	public void setEventDetectors(List<PointEventDetectorVO> pointEventDetectors) {
		this.pointEventDetectors = pointEventDetectors;
	}

	public List<PointEventDetectorVO> getEventDetectors() {
		return this.pointEventDetectors;
	}

	public EventDetectorTemplateRT createRuntime() {
		return new EventDetectorTemplateRT(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pointEventDetectors == null) ? 0 : pointEventDetectors.hashCode());
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
		EventDetectorTemplateVO other = (EventDetectorTemplateVO) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pointEventDetectors == null) {
			if (other.pointEventDetectors != null)
				return false;
		} else if (!pointEventDetectors.equals(other.pointEventDetectors))
			return false;
		return true;
	}

}
