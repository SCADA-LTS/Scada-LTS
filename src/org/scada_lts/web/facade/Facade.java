package org.scada_lts.web.facade;

import com.serotonin.mango.web.AvailableUnavailableViews;
import com.serotonin.mango.web.LockedViews;
import org.springframework.stereotype.Component;

@Component
public class Facade {

    public Facade(){}

    public String checkAvailabibityView(String xidName){

        String NOT_AVAILABLE = "1";
        String AVAILABLE = "0";

        return AvailableUnavailableViews.checkAvailabibityView(xidName)?NOT_AVAILABLE:AVAILABLE;
    }

    public String getOwnerOfEditView(String xidName){
        LockedViews lockedView = AvailableUnavailableViews.getOwnerOfEditView(xidName);
        return lockedView.toString();
    }

    public void breakEditActionForUser(String xidName) {
        AvailableUnavailableViews.breakEditActionForUser(xidName);
    }
}
