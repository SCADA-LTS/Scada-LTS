package com.serotonin.mango;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for collect collection eeg.:
 * <b>key</b>(scriptSessionId)-<b>value</b>(BusinessObject) under <b>key</b>sessionId in context.
 *
 * @author Mateusz Hyski mateusz.hyski@softq.pl hyski.mateusz@gmail.com
 */
public class ScriptSession {

    private static final Log LOG = LogFactory.getLog(ScriptSession.class);

    /**
     * put new dependency eeg: <b>key</b>scriptSessionId and <b>value</b>businessObject like Point,View
     * under sessionId in context
     *
     * @param objectEegPointWatchListAndSoOn Object
     * @param sessionId                      String
     * @param scriptSessionId                String
     * @return boolean
     */
    public synchronized static boolean addNewEditedObjectForScriptSession(Object objectEegPointWatchListAndSoOn, String sessionId, String scriptSessionId) {
        boolean result = Boolean.FALSE;
        if (validate(Arrays.asList(sessionId, scriptSessionId)) &&
                objectEegPointWatchListAndSoOn != null) {
            if (Common.getServletContext().getAttribute(sessionId) != null) {
                Map<String, Object> scriptSessionForBusinessObject = (Map<String, Object>) Common.getServletContext().getAttribute(sessionId);
                scriptSessionForBusinessObject.put(scriptSessionId, objectEegPointWatchListAndSoOn);
                Object object = new Object();
                synchronized (object) {
                    Common.getServletContext().removeAttribute(sessionId);
                    Common.getServletContext().setAttribute(sessionId, scriptSessionForBusinessObject);
                }
            } else {
                Map<String, Object> scriptSessionVSBusinessObject = new HashMap<String, Object>();
                scriptSessionVSBusinessObject.put(scriptSessionId, objectEegPointWatchListAndSoOn);
                Common.getServletContext().setAttribute(sessionId, scriptSessionVSBusinessObject);
            }
            result = Boolean.TRUE;
        } else {
            LOG.error("Any of parameters (sessionId,objectEegPointWatchListAndSoOn,scriptSessionId) cannot be empty");
        }
        return result;
    }

    /**
     * Returns business object like eventdetector which is collect under concrete scriptsessionid
     * in map. Mentioned map is saved under sessionid in context.
     *
     * @param sessionId       String
     * @param scriptSessionId String
     * @return Object eeg. View,EventDetector....
     */
    public synchronized static Object getObjectForScriptSession(String sessionId, String scriptSessionId) {

        if (validate(Arrays.asList(sessionId, scriptSessionId))) {
            if (Common.getServletContext().getAttribute(sessionId) != null) {
                if (((Map<String, Object>) Common.getServletContext().getAttribute(sessionId)).get(scriptSessionId) != null) {
                    return ((Map<String, Object>) Common.getServletContext().getAttribute(sessionId)).get(scriptSessionId);
                } else
                    return null;

            } else
                return null;
        } else {
            LOG.error("Any of parameters (sessionId,objectEegPointWatchListAndSoOn,scriptSessionId) cannot be empty");
        }
        return null;
    }

    /**
     * remove dependency, it means <b>key</b>sessionId - <b>value</b> ->map(scriptsession - business object) which exists in context
     *
     * @param sessionId String
     * @return boolean
     */
    public synchronized static boolean clearSessionIdFromContext(String sessionId) {
        boolean result = Boolean.FALSE;
        if (validate(Arrays.asList(sessionId))) {
            if (Common.getServletContext().getAttribute(sessionId) != null) {
                Common.getServletContext().removeAttribute(sessionId);
                result = Boolean.TRUE;
            }
        } else {
            LOG.error("Parameter sessionId cannot be null or empty");
        }
        return result;
    }

    /**
     * remove ONLY existing key scriptSessionId from map which exist under key sessionId in context
     *
     * @param sessionId       String
     * @param scriptSessionId String
     * @return boolean
     */
    public synchronized static boolean removeScriptSessionForObjectBySessionIdAndScriptSessionId(String sessionId, String scriptSessionId) {

        boolean result = Boolean.FALSE;

        if (validate(Arrays.asList(sessionId, scriptSessionId))) {
            if (Common.getServletContext().getAttribute(sessionId) != null) {
                if (((Map<String, Object>) Common.getServletContext().getAttribute(sessionId)).containsKey(scriptSessionId)) {
                    ((Map<String, Object>) Common.getServletContext().getAttribute(sessionId)).remove(scriptSessionId);
                    result = Boolean.TRUE;
                }
            }
        } else {
            LOG.error("Parameter sessionId and scriptSessionId cannot be null or empty");
        }

        return result;

    }

    private static boolean validate(List<String> values) {
        boolean result = Boolean.TRUE;
        for (String valueFromList : values) {
            if (valueFromList == null || valueFromList.isEmpty()) {
                result = Boolean.FALSE;
                break;
            }
        }
        return result;
    }
}
