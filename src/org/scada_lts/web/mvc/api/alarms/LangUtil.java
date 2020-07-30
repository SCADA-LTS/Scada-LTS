package org.scada_lts.web.mvc.api.alarms;

import org.scada_lts.dao.alarms.HistoryAlarm;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LangUtil {

    public static List<HistoryAlarm> translate(List<HistoryAlarm> history, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        for (HistoryAlarm alarm : history) {
            String wordTranslated = resourceBundle.getString(alarm.getDescription());
            alarm.setDescription(wordTranslated);
        }
        return history;
    }
}
