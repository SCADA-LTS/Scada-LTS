package com.serotonin.mango;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.Container;
import org.directwebremoting.ServerContextFactory;
import org.directwebremoting.extend.ScriptSessionManager;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * ScriptSessionAndUsers responsible for managing  User copies specific given browser "tab" (DWR Script Session)
 * User copies are stored in {@value #SCRIPTSESSION_USER} attribute of DWR script session
 *
 * @author Mateusz Hyski mateusz.hyski@softq.pl
 */
public class ScriptSessionAndUsers {

    private static final Log LOG = LogFactory.getLog(ScriptSessionAndUsers.class);
    /**
     * {@value #SCRIPTSESSION_USER} is the attribute name under session
     */
    private static final String SCRIPTSESSION_USER = "dwrscriptsessionuser";

    private static ScriptSessionManager getScriptSessionManager(HttpServletRequest request){
        ScriptSessionManager manager = null;
        try {
            Container container = ServerContextFactory.get(request.getServletContext()).getContainer();
            manager = (ScriptSessionManager) container.getBean(ScriptSessionManager.class.getName());
        }catch (Exception e){
            LOG.warn("Could not retrieve user for dwr script session");
        }
        return manager;
    }
    /**
     * Returns correct User from Script Session
     *
     * @param scriptSessionId
     * @param request
     * @return User
     */
    public static User getUserFromScriptSessionManagerSavedUnderDWRSCRIPTSESSIONUSER(String scriptSessionId, HttpServletRequest request){
        User user = null;
        try {
            user = (User) getScriptSessionManager(request).getScriptSession(scriptSessionId).getAttribute(SCRIPTSESSION_USER);
        }catch (Exception e){
            LOG.warn("Could not retrieve user for dwr script session");
        }
        return (user!=null) ? user
                            : Common.getUser(request);
    }
    public synchronized static boolean addNewEditedObjectForScriptSession(Object objectEegPointWatchListAndSoOn,String sessionId,  String scriptSessionId){
        boolean result = Boolean.FALSE;
        if(sessionId !=null && !sessionId.isEmpty() &&
            objectEegPointWatchListAndSoOn !=null &&
            scriptSessionId !=null && !scriptSessionId.isEmpty()
        ) {
            if (Common.getServletContext().getAttribute(sessionId) != null) {
                ((Map<String, Object>) Common.getServletContext().getAttribute(sessionId)).put(scriptSessionId, objectEegPointWatchListAndSoOn);
            } else {
                Map<String, Object> scriptSessionVSBusinessObject = new HashMap<String, Object>();
                scriptSessionVSBusinessObject.put(scriptSessionId, objectEegPointWatchListAndSoOn);
                Common.getServletContext().setAttribute(sessionId, scriptSessionVSBusinessObject);
            }
            result= Boolean.TRUE;
        }
        else
        {
            LOG.error("Any of parameters (sessionId,objectEegPointWatchListAndSoOn,scriptSessionId) cannot be empty");
        }
        return result;
    }
    public synchronized static Object getObjectForScriptSession(String sessionId, String scriptSessionId){

        if(sessionId !=null && !sessionId.isEmpty() &&
            scriptSessionId !=null && !scriptSessionId.isEmpty()
        ) {
            if (Common.getServletContext().getAttribute(sessionId)!=null) {
                if(((Map<String, Object>) Common.getServletContext().getAttribute(sessionId)).get(scriptSessionId)!=null){
                    return ((Map<String, Object>) Common.getServletContext().getAttribute(sessionId)).get(scriptSessionId);
                }
                else
                    return null;

            }
            else
                return null;
        }
        else
            {
            LOG.error("Any of parameters (sessionId,objectEegPointWatchListAndSoOn,scriptSessionId) cannot be empty");
            }
        return null;
    }
    public synchronized static boolean removeScriptSessionVsObject(String sessionId){
        boolean result = Boolean.FALSE;
        if(sessionId!=null && !sessionId.isEmpty()) {
            if (Common.getServletContext().getAttribute(sessionId) != null) {
                Common.getServletContext().removeAttribute(sessionId);
                result = Boolean.TRUE;
            }
        }
        else
        {
            LOG.error("Parameter sessionId cannot be null or empty");
        }
        return  result;
    }
}
