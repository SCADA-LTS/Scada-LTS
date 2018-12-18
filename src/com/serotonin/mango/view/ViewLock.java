package com.serotonin.mango.view;

import com.serotonin.mango.viewInterfaces.ViewState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ViewLock depends on State interface and is responsible only to give information about
 * available/unavailable state to edit. To change state of view we use ViewUnlock interface in viewInterfaces package
 *
 * @author Mateusz Hyski {@link "mailto:mateusz.hyski@softq.pl;hyski.mateusz@gmail.com","ScadaLTS"}
 */
public class ViewLock  implements ViewState {

    private static final Log LOG = LogFactory.getLog(ViewLock.class);

    /**
     * default state of this is unlock state, this state gives blocks view possibility to edit by user.
     * This VIEW_IS_LOCKED variable can be initialized also in (point 1) block
     */
    private final boolean VIEW_IS_LOCKED =Boolean.TRUE;

    //point 1
    {
        //that is example
        //VIEW_IS_LOCKED = connection to other business logic
    }

    private static ViewLock instance = new ViewLock();

    private ViewLock() { }

    public static ViewState instance() {
        return instance;
    }

    /**
     * status about state, true when some element is locked,otherwise false
     *
     * @return boolean
     */
    @Override
    public boolean isBlocked() {
        return instance.VIEW_IS_LOCKED;
    }


    public String toString(){
        return new StringBuilder()
                .append("View is locked").toString();
    }
}
