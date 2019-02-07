package com.serotonin.mango;

import com.rits.cloning.Cloner;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.*;
import org.directwebremoting.extend.RealScriptSession;
import org.directwebremoting.extend.ScriptSessionManager;
import org.directwebremoting.impl.DefaultScriptSession;
import org.scada_lts.web.mvc.controller.FinalVariablesForControllers;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Iterator;

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
    public static final String SCRIPTSESSION_USER = "dwrscriptsessionuser";

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
    public static User getUserFromScriptSessionManagerByScriptSessionId(HttpServletRequest request, String scriptsessionid) {
        ScriptSessionManager manager = null;
        User user = null;
        try {
            RealScriptSession rss = getScriptSessionManager(request).getScriptSession(scriptsessionid);
            user = (User) rss.getAttribute(scriptsessionid);
            int a = 0;
        } catch (Exception e) {
            LOG.warn("Could not retrieve user for dwr script session");
        }
        return user;
    }
    /**
     * Gets user from map what is attribute of Script Session
     *
     * @param user
     * @param webContext
     * @return User
     */
    public static User findOrAddScriptSessionUserIntoScriptSessionUnderDWRSCRIPTSESSIONUSER(User user, WebContext webContext){

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
                scriptSession.setAttribute(scriptSession.getId(), scriptSessionUser);
                scriptSession.setAttribute(webContext.getSession().getId(), scriptSession.getId());
                webContext.getSession().setAttribute(webContext.getSession().getId(), scriptSession.getId());
                webContext.getSession().setAttribute(scriptSession.getId(),scriptSessionUser);
                return scriptSessionUser;
            }else{
                return (User) scriptSession.getAttribute(SCRIPTSESSION_USER);
            }

        }
    }
    public static User findScriptSessionUserInScriptSessionManagerCollection(HttpServletRequest request){
            User userDwr = null;

            Collection<DefaultScriptSession> col  = (Collection<DefaultScriptSession>) getScriptSessionManager(request).getAllScriptSessions();//getScriptSessionsByPage("/ScadaBR/data_point_edit.shtm");
            Iterator dss = col.iterator();
            while(dss.hasNext()){
                DefaultScriptSession dss2 = (DefaultScriptSession) dss.next();
                userDwr=(User) dss2.getAttribute((String)request.getSession().getId());//getParameter(FinalVariablesForControllers.DWR_SCRIPT_SESSION_ID));

                if(userDwr!=null)
                    return userDwr;
            }

            return userDwr;
        }


}
