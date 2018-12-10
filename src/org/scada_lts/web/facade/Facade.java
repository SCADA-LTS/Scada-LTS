package org.scada_lts.web.facade;

import com.serotonin.mango.web.AvailableUnavailableViews;
import com.serotonin.mango.web.LockedViews;
import org.springframework.stereotype.Component;

/**
 * REST API controller and responsibility :
 * -check view state
 * -get details of user who locked view(etc. who work with view at the time when somebody else tries edit view)
 * -break edit action - in other words take control on view with show information for other user about this action
 *
 * @author Mateusz Hyski hyski.mateusz@gmail.com
 */
@Component
public class Facade {

    public Facade(){}


    /**
     * with only xidName we check view availability
     *
     * @param xidName
     * @return String
     */
    public String checkAvailabibityView(String xidName){

        String NOT_AVAILABLE = "1";
        String AVAILABLE = "0";

        return AvailableUnavailableViews.checkAvailabibityView(xidName)?NOT_AVAILABLE:AVAILABLE;
    }

    /**
     * user information downloaded like "xidName"
     *
     * @param xidName
     * @return String
     */
    public String getOwnerOfEditView(String xidName){
        LockedViews lockedView = AvailableUnavailableViews.getOwnerOfEditView(xidName);
        return lockedView.toString();
    }

    /**
     * just break edit operation on view means in other meaning remove control on view
     *
     * @param xidName
     */
    public void breakEditActionForUser(String xidName) {
        AvailableUnavailableViews.breakEditActionForUser(xidName);
    }
}
