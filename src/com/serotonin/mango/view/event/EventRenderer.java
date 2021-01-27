package com.serotonin.mango.view.event;

import com.serotonin.mango.view.ImplDefinition;

import java.io.Serializable;

public interface EventRenderer extends Serializable {

    public String getText();

    public String getTypeName();

    public ImplDefinition getDef();
}
