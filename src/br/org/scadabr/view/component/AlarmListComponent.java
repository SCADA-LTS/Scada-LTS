package br.org.scadabr.view.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.serotonin.mango.view.component.ViewComponent;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.web.dwr.BaseDwr;
import org.scada_lts.mango.service.EventService;

@JsonRemoteEntity
public class AlarmListComponent extends CustomComponent {

	public static ImplDefinition DEFINITION = new ImplDefinition("alarmlist",
			"ALARMLIST", "graphic.alarmlist", new int[] {});

	@JsonRemoteProperty
	private int minAlarmLevel = 1;
	@JsonRemoteProperty
	private int maxListSize = 5;
	@JsonRemoteProperty
	private int width = 500;

	private boolean hideIdColumn = true;
	private boolean hideAlarmLevelColumn = false;
	private boolean hideTimestampColumn = false;
	private boolean hideInactivityColumn = true;
	private boolean hideAckColumn = false;

	public AlarmListComponent() {}

	private AlarmListComponent(AlarmListComponent alarmListComponent) {
		super(alarmListComponent);
		this.minAlarmLevel = alarmListComponent.getMinAlarmLevel();
		this.maxListSize = alarmListComponent.getMaxListSize();
		this.width = alarmListComponent.getWidth();
		this.hideIdColumn = alarmListComponent.isHideIdColumn();
		this.hideAlarmLevelColumn = alarmListComponent.isHideAlarmLevelColumn();
		this.hideTimestampColumn = alarmListComponent.isHideTimestampColumn();
		this.hideInactivityColumn = alarmListComponent.isHideInactivityColumn();
		this.hideAckColumn = alarmListComponent.isHideAckColumn();
	}

	@Override
	public String generateContent() {
		Map<String, Object> model = new HashMap<String, Object>();
		WebContext webContext = WebContextFactory.get();
		HttpServletRequest request = webContext.getHttpServletRequest();
		List<EventInstance> toViewEvents = new EventService().getPendingEventsAlarmLevelMin(Common
				.getUser().getId(), minAlarmLevel, maxListSize, true);

		model.put("nome", "marlon");
		model.put("events",toViewEvents);
		model.put("width", width > 0 ? width : 500);
		model.put("hideIdColumn", hideIdColumn);
		model.put("hideAlarmLevelColumn", hideAlarmLevelColumn);
		model.put("hideTimestampColumn", hideTimestampColumn);
		model.put("hideInactivityColumn", hideInactivityColumn);
		model.put("hideAckColumn", hideAckColumn);

		String content = BaseDwr.generateContent(request, "alarmList.jsp",
				model);
		return content;
	}

	@Override
	public boolean containsValidVisibleDataPoint(int dataPointId) {
		return false;
	}

	@Override
	public ImplDefinition definition() {
		return DEFINITION;
	}

	@Override
	public String generateInfoContent() {
		return "<b> info content</b>";
	}

	public int getMaxListSize() {
		return maxListSize;
	}

	public void setMaxListSize(int maxListSize) {
		this.maxListSize = maxListSize;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isHideIdColumn() {
		return hideIdColumn;
	}

	public void setHideIdColumn(boolean hideIdColumn) {
		this.hideIdColumn = hideIdColumn;
	}

	public boolean isHideTimestampColumn() {
		return hideTimestampColumn;
	}

	public void setHideTimestampColumn(boolean hideTimestampColumn) {
		this.hideTimestampColumn = hideTimestampColumn;
	}

	public boolean isHideAlarmLevelColumn() {
		return hideAlarmLevelColumn;
	}

	public void setHideAlarmLevelColumn(boolean hideAlarmLevelColumn) {
		this.hideAlarmLevelColumn = hideAlarmLevelColumn;
	}

	public boolean isHideInactivityColumn() {
		return hideInactivityColumn;
	}

	public void setHideInactivityColumn(boolean hideInactivityColumn) {
		this.hideInactivityColumn = hideInactivityColumn;
	}

	@Override
	public ViewComponent copy() {
		return new AlarmListComponent(this);
	}

	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		out.writeInt(minAlarmLevel);
		out.writeInt(maxListSize);
		out.writeInt(width);
		out.writeBoolean(hideIdColumn);
		out.writeBoolean(hideAlarmLevelColumn);
		out.writeBoolean(hideTimestampColumn);
		out.writeBoolean(hideInactivityColumn);
		out.writeBoolean(hideAckColumn);

	}

	private void readObject(ObjectInputStream in) throws IOException {
		int ver = in.readInt();
		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == 1) {
			minAlarmLevel = in.readInt();
			maxListSize = in.readInt();
			width = in.readInt();
			hideIdColumn = in.readBoolean();
			hideAlarmLevelColumn = in.readBoolean();
			hideTimestampColumn = in.readBoolean();
			hideInactivityColumn = in.readBoolean();
			hideAckColumn = in.readBoolean();
		}

	}

	public void setHideAckColumn(boolean hideAckColumn) {
		this.hideAckColumn = hideAckColumn;
	}

	public boolean isHideAckColumn() {
		return hideAckColumn;
	}

	public void setMinAlarmLevel(int minAlarmLevel) {
		this.minAlarmLevel = minAlarmLevel;
	}

	public int getMinAlarmLevel() {
		return minAlarmLevel;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AlarmListComponent)) return false;
		if (!super.equals(o)) return false;
		AlarmListComponent that = (AlarmListComponent) o;
		return getMinAlarmLevel() == that.getMinAlarmLevel() && getMaxListSize() == that.getMaxListSize() && getWidth() == that.getWidth() && isHideIdColumn() == that.isHideIdColumn() && isHideAlarmLevelColumn() == that.isHideAlarmLevelColumn() && isHideTimestampColumn() == that.isHideTimestampColumn() && isHideInactivityColumn() == that.isHideInactivityColumn() && isHideAckColumn() == that.isHideAckColumn();
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getMinAlarmLevel(), getMaxListSize(), getWidth(), isHideIdColumn(), isHideAlarmLevelColumn(), isHideTimestampColumn(), isHideInactivityColumn(), isHideAckColumn());
	}


	@Override
	public String toString() {
		return "AlarmListComponent{" +
				"minAlarmLevel=" + minAlarmLevel +
				", maxListSize=" + maxListSize +
				", width=" + width +
				", hideIdColumn=" + hideIdColumn +
				", hideAlarmLevelColumn=" + hideAlarmLevelColumn +
				", hideTimestampColumn=" + hideTimestampColumn +
				", hideInactivityColumn=" + hideInactivityColumn +
				", hideAckColumn=" + hideAckColumn +
				"} " + super.toString();
	}
}
