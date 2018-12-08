package com.serotonin.mango.view;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ViewUnlock implements States {

    private static final Log LOG = LogFactory.getLog(ViewUnlock.class);

    private final boolean VIEW_IS_UNLOCKED =false;

    private static ViewUnlock instance = new ViewUnlock();

    private ViewUnlock() {}

    public static States instance() {
        return instance;
    }

    @Override
    public boolean isBlocked() {
        return VIEW_IS_UNLOCKED;
    }

    @Override
    public void nextState(View view) {
        view.changeState(ViewLock.instance());
        LOG.info(" Change state for "+view.toString()+" to Unlock");
    }
}
