package org.scada_lts.ds.state.change;

import org.scada_lts.ds.state.IStateDs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Grzegorz Bylica on behalf of Abil'I.T. (code owner) email: sdt@abilit.eu
 */
public class ChangeStatus {

    private static final String STATE_PROPERTY = "state_property";

    List<PropertyChangeListener> listener;

    protected ChangeStatus() {
        listener = new ArrayList<>();
    }

    protected void notifyListeners(Object object, IStateDs oldValue, IStateDs newValue) {
        for (PropertyChangeListener state : listener) {
            state.propertyChange(new PropertyChangeEvent(object, STATE_PROPERTY, oldValue, newValue));
        }
    }

    public void addChangeListener(PropertyChangeListener newListener) {
        listener.add(newListener);
    }

    protected void resetListeners() {
        this.listener = new ArrayList<>();
    }
}
