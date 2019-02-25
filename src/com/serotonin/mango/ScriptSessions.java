package com.serotonin.mango;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.HashMap;
import java.util.Map;


/**
 * ScriptSessions is responsible for cooperate on map collection. This collection have pair like :
 * key  - dwrScriptSessionid,
 * value- business object like Point,View etc.
 *
 * @author Mateusz Hyski mateusz.hyski@softq.pl
 */
public class ScriptSessions {

    private static final Log LOG = LogFactory.getLog(ScriptSessions.class);


    public synchronized static boolean removeScriptSessionVsObjectBySessionIdAndScriptSessionId(String sessionId, String scriptSessionId){

        boolean result = Boolean.FALSE;

            if (Common.getCtx().getAttribute(sessionId) != null) {
                if (((Map<String, Object>) Common.getCtx().getAttribute(sessionId)).containsKey(scriptSessionId)) {
                    ((Map<String, Object>) Common.getCtx().getAttribute(sessionId)).remove(scriptSessionId);
                    result = Boolean.TRUE;
                }
            }

        return result;

    }
    public synchronized static void addNewScriptSessionVsObjectUnderGivenSessionId(String sessionId, Object objectEegPointWatchListAndSoOn, String scriptSessionId){
        if(sessionId !=null && !sessionId.isEmpty() &&
            objectEegPointWatchListAndSoOn !=null &&
            scriptSessionId !=null && !scriptSessionId.isEmpty()
        ) {
            if (Common.getCtx().getAttribute(sessionId) != null) {
                ((Map<String, Object>) Common.getCtx().getAttribute(sessionId)).put(scriptSessionId, objectEegPointWatchListAndSoOn);
            } else {
                Map<String, Object> scriptSessionVSBusinessObject = new HashMap<String, Object>();
                scriptSessionVSBusinessObject.put(scriptSessionId, objectEegPointWatchListAndSoOn);
                Common.getCtx().setAttribute(sessionId, scriptSessionVSBusinessObject);
            }
        }
        else
        {
            try {
                throw new Exception("Any of parameters (sessionId,objectEegPointWatchListAndSoOn,scriptSessionId) cannot be empty");
            } catch (Exception e) {
                LOG.warn(e.getMessage());
            }
        }

    }
    public synchronized static Object getObjectVsScriptSession(String sessionId, String scriptSessionId){

        if(sessionId !=null && !sessionId.isEmpty() &&
            scriptSessionId !=null && !scriptSessionId.isEmpty()
        ) {
            if (Common.getCtx().getAttribute(sessionId)!=null) {
                if(((Map<String, Object>) Common.getCtx().getAttribute(sessionId)).get(scriptSessionId)!=null){
                    return ((Map<String, Object>) Common.getCtx().getAttribute(sessionId)).get(scriptSessionId);
                }
                else
                    return null;
            }
            else
                return null;
        }
        else
            try {
                throw new Exception("Any of parameters (sessionId,scriptSessionId) cannot be empty");
            } catch (Exception e) {
                LOG.warn(e.getMessage());
            }
            return null;

    }

    public synchronized static void removeAllUnderSessionIdAttributeFromCommonContext(String sessionId){
        if(sessionId!=null && !sessionId.isEmpty()) {
            if (Common.getCtx().getAttribute(sessionId) != null) {
                Common.getCtx().removeAttribute(sessionId);
            }
        }
        else
            try {
                throw new Exception("Parameter sessionId cannot be null or empty");
            } catch (Exception e) {
                LOG.warn(e.getMessage());
            }
    }

}
