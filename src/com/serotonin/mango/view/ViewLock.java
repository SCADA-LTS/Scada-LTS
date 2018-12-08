package com.serotonin.mango.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ViewLock implements States {

    private static final Log LOG = LogFactory.getLog(ViewLock.class);

    private final boolean VIEW_IS_LOCKED =true;

    private static ViewLock instance = new ViewLock();

    private ViewLock() { }

    public static States instance() {
        return instance;
    }

    @Override
    public boolean isBlocked() {
        return VIEW_IS_LOCKED;
    }


    @Override
    public void nextState(View view) {
        view.changeState(ViewUnlock.instance());
        LOG.info(" Change state for "+view.toString()+" to Lock");
    }
}
