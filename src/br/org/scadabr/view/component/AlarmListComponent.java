package br.org.scadabr.view.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import com.serotonin.util.SerializationHelper;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.EventDao;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.web.dwr.BaseDwr;

@JsonRemoteEntity
public class AlarmListComponent extends CustomComponent {

	public static ImplDefinition DEFINITION = new ImplDefinition("alarmlist",
			"ALARMLIST", "graphic.alarmlist", new int[] {});

	@JsonRemoteProperty
	private int minAlarmLevel = 1;
	@JsonRemoteProperty
	private String messageContent = "";
	@JsonRemoteProperty
	private int maxListSize = 5;
	@JsonRemoteProperty
	private int width = 500;

	private boolean hideIdColumn = true;
	private boolean hideAlarmLevelColumn = false;
	private boolean hideTimestampColumn = false;
	private boolean hideInactivityColumn = true;
	private boolean hideAckColumn = false;
	private boolean hideCriteriaHeader = false;

	@Override
	public String generateContent() {
		Map<String, Object> model = new HashMap<String, Object>();
		WebContext webContext = WebContextFactory.get();
		HttpServletRequest request = webContext.getHttpServletRequest();
		List<EventInstance> events = new EventDao().getPendingEvents(Common
				.getUser().getId());

		filterByAlarmLevel(events, minAlarmLevel);
		filterByMessageContent(events, messageContent);

		int max = events.size() > maxListSize ? maxListSize : events.size();

		model.put("nome", "marlon");
		model.put("events", events.subList(0, max));
		model.put("width", width > 0 ? width : 500);
		model.put("hideIdColumn", hideIdColumn);
		model.put("hideAlarmLevelColumn", hideAlarmLevelColumn);
		model.put("hideTimestampColumn", hideTimestampColumn);
		model.put("hideInactivityColumn", hideInactivityColumn);
		model.put("hideAckColumn", hideAckColumn);
		model.put("hideCriteriaHeader", hideCriteriaHeader);

		model.put("maxListSize", maxListSize);
		model.put("minAlarmLevel", AlarmLevels.getAlarmLevelMessage(minAlarmLevel).getLocalizedMessage(getResourceBundle()));
		model.put("messageContent", messageContent);

		String content = BaseDwr.generateContent(request, "alarmList.jsp",
				model);
		return content;
	}

	private void filterByAlarmLevel(List<EventInstance> list, int alarmLevel) {

		if (AlarmLevels.INFORMATION == alarmLevel) {
			removeAlarmLevel(list, AlarmLevels.NONE);
		}
		if (AlarmLevels.URGENT == alarmLevel) {
			removeAlarmLevel(list, AlarmLevels.NONE);
			removeAlarmLevel(list, AlarmLevels.INFORMATION);
		}
		if (AlarmLevels.CRITICAL == alarmLevel) {
			removeAlarmLevel(list, AlarmLevels.NONE);
			removeAlarmLevel(list, AlarmLevels.INFORMATION);
			removeAlarmLevel(list, AlarmLevels.URGENT);
		}
		if (AlarmLevels.LIFE_SAFETY == alarmLevel) {
			removeAlarmLevel(list, AlarmLevels.NONE);
			removeAlarmLevel(list, AlarmLevels.INFORMATION);
			removeAlarmLevel(list, AlarmLevels.URGENT);
			removeAlarmLevel(list, AlarmLevels.CRITICAL);
		}

	}

	private void filterByMessageContent(List<EventInstance> list, String keywords) {
        List<EventInstance> copy = new ArrayList<EventInstance>();

        list.stream().forEach(eventInstance -> {
            if(!eventInstance.getMessage().getLocalizedMessage(getResourceBundle()).contains(keywords)) {
                copy.add(eventInstance);
            }
        });

        list.removeAll(copy);
    }

	private void removeAlarmLevel(List<EventInstance> source, int alarmLevel) {
		List<EventInstance> copy = new ArrayList<EventInstance>();

		for (EventInstance eventInstance : source) {
			if (eventInstance.getAlarmLevel() == alarmLevel)
				copy.add(eventInstance);
		}

		source.removeAll(copy);
	}

	private ResourceBundle getResourceBundle() {
		WebContext webContext = WebContextFactory.get();
		LocalizationContext localizationContext = (LocalizationContext) Config.get(webContext.getHttpServletRequest(),
				Config.FMT_LOCALIZATION_CONTEXT);
		return localizationContext.getResourceBundle();
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

    public boolean isHideCriteriaHeader() {
        return hideCriteriaHeader;
    }

    public void setHideCriteriaHeader(boolean hideCriteriaHeader) {
        this.hideCriteriaHeader = hideCriteriaHeader;
    }

    private static final long serialVersionUID = -1;
	private static final int version_1 = 1;
	private static final int version_2 = 2;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version_2);
		out.writeInt(minAlarmLevel);
		SerializationHelper.writeSafeUTF(out, messageContent);
		out.writeInt(maxListSize);
		out.writeInt(width);
		out.writeBoolean(hideIdColumn);
		out.writeBoolean(hideAlarmLevelColumn);
		out.writeBoolean(hideTimestampColumn);
		out.writeBoolean(hideInactivityColumn);
		out.writeBoolean(hideAckColumn);
		out.writeBoolean(hideCriteriaHeader);

	}

	private void readObject(ObjectInputStream in) throws IOException {
		int ver = in.readInt();
		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == version_1) {
			minAlarmLevel = in.readInt();
			maxListSize = in.readInt();
			width = in.readInt();
			hideIdColumn = in.readBoolean();
			hideAlarmLevelColumn = in.readBoolean();
			hideTimestampColumn = in.readBoolean();
			hideInactivityColumn = in.readBoolean();
			hideAckColumn = in.readBoolean();
		} else if (ver == version_2) {
			minAlarmLevel = in.readInt();
			messageContent = SerializationHelper.readSafeUTF(in);
			maxListSize = in.readInt();
			width = in.readInt();
			hideIdColumn = in.readBoolean();
			hideAlarmLevelColumn = in.readBoolean();
			hideTimestampColumn = in.readBoolean();
			hideInactivityColumn = in.readBoolean();
			hideAckColumn = in.readBoolean();
			hideCriteriaHeader = in.readBoolean();
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

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
}
