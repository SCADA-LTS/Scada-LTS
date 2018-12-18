package com.serotonin.mango;

import com.rits.cloning.Cloner;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.Container;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ServerContextFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.extend.ScriptSessionManager;

import javax.servlet.http.HttpServletRequest;

/**
 * ScriptSessionAndUsers responsible for managing  User copies specific given browser "tab" (DWR Script Session)
 * User copies are stored in {@value #SCRIPTSESSION_USER} attribute of DWR script session
 *
 * @author Mateusz Hyski {@link "mailto:mateusz.hyski@softq.pl;hyski.mateusz@gmail.com","ScadaLTS"}
 */
public class ScriptSessionAndUsers {

    private static final Log LOG = LogFactory.getLog(ScriptSessionAndUsers.class);

    /**
     * {@value #SCRIPTSESSION_USER} is the attribute name under session
     */
    public static final String SCRIPTSESSION_USER = "dwrscriptsessionuser";

    /**
     * Returns correct User from Script Session
     *
     * @param scriptSessionId
     * @param request
     * @return User
     */
    public static User getUserForScriptSessionId(String scriptSessionId, HttpServletRequest request){
        User user = null;
        try {
            Container container = ServerContextFactory.get(request.getServletContext()).getContainer();
            ScriptSessionManager manager = (ScriptSessionManager) container.getBean(ScriptSessionManager.class.getName());

            user = (User) manager.getScriptSession(scriptSessionId).getAttribute(SCRIPTSESSION_USER);
        }catch (Exception e){
            LOG.warn("Could not retrieve user for dwr script session");
        }
        return (user!=null) ? user
                            : Common.getUser(request);
    }
    /**
     * Gets user from map what is attribute of Script Session
     *
     * @param user
     * @param webContext
     * @return User
     */
    static User findScriptSessionUser(User user, WebContext webContext){

        ScriptSession scriptSession=null;

        try {
            scriptSession = webContext == null ? null : webContext.getScriptSession();
        }catch (Exception e){
            LOG.warn("We have exception when trying to get scriptSession.");
        }

        if  (scriptSession == null) {
            return  null;
        }else{
            if (scriptSession.getAttribute(SCRIPTSESSION_USER) == null){
                User scriptSessionUser = new Cloner().deepClone(user);
                scriptSession.setAttribute(SCRIPTSESSION_USER, scriptSessionUser);
                return scriptSessionUser;
            }else{
                return (User) scriptSession.getAttribute(SCRIPTSESSION_USER);
            }
        }
    }

}
