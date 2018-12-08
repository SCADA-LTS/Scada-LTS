package com.serotonin.mango.web;


import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

//jsession <<1.login user 2. czas 3.jsession>>
/**
 * Puts and remove in application scope data like viewXid and instance of  ViewLockedBy class
 *
 * @author Mateusz Hyski mateusz.hyski@softq.pl
 */
public class AvailableUnavailableViews {

    private  static Set<LockedViews> lockedViews = new HashSet<LockedViews>();

    /**
     * check that view is available or not to edit state
     *
     * @param viewXid
     * @return boolean
     */
    public static boolean isThisViewIsAvailableToEdit(String viewXid) {
        LockedViews viewLockedBy=(LockedViews) Common.ctx.getCtx().getAttribute(viewXid);
        return viewLockedBy==null;
    }

    /**
     * add to applicationscope data about view which is locked and who is locking view and since when
     *
     * @param viewXid
     * @param userName
     */
    public static void addViewToBlockList(String viewXid, String userName,String sessionId){

        LockedViews viewLockedBy = new LockedViews(userName,viewXid,sessionId);

        lockedViews.add(viewLockedBy);
        Common.ctx.getCtx().setAttribute(viewXid,viewLockedBy);
    }

    /**
     * remove from applicationscope view - in other words unlock view
     *
     * @param viewXid
     */
    public static void removeViewFromBlockList(String viewXid){
        Common.ctx.getCtx().removeAttribute(viewXid);
    }

    /**
     * when user select logout action, remove all views with locked status with present sessionId,
     * in other words when user logout from one web browser
     *
     * @param sessionId
     */
    public static void removeAllLockedViewsWhenUserLoggedOut(String sessionId){
        for (LockedViews view: lockedViews) {
            if(view.getSessionId().equals(sessionId))
                removeViewFromBlockList(view.getViewXid());
        }
        lockedViews.removeIf(item -> item.getSessionId().equals(sessionId));
    }

    public static boolean checkAvailabibityView(String xid){
        for (LockedViews view: lockedViews) {
            if(view.getViewXid().equals(xid)) {
                return true;
            }
        }
        return false;
    }
    public static LockedViews getOwnerOfEditView(String xid){
        for (LockedViews view: lockedViews) {
            if(view.getViewXid().equals(xid)) {
                return view;
            }
        }
        return null;
    }
    public static void breakEditActionForUser(String xid){
        for (LockedViews view: lockedViews) {
            if(view.getViewXid().equals(xid)) {
                removeViewFromBlockList(view.getViewXid());
            }
        }
    }


}
