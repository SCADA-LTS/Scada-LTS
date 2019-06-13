/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.view.component;

import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class CompoundChild {
    private final String id;
    private final LocalizableMessage description;
    private final ViewComponent viewComponent;
    private final int[] dataTypesOverride;

    public CompoundChild(String id, LocalizableMessage description, ViewComponent viewComponent, int[] dataTypesOverride) {
        this.id = id;
        this.description = description;
        this.viewComponent = viewComponent;
        this.dataTypesOverride = dataTypesOverride;
    }

    public String getId() {
        return id;
    }

    public LocalizableMessage getDescription() {
        return description;
    }

    public ViewComponent getViewComponent() {
        return viewComponent;
    }

    public int[] getDataTypes() {
        if (dataTypesOverride != null)
            return dataTypesOverride;
        if (viewComponent.isPointComponent())
            return ((PointComponent) viewComponent).getSupportedDataTypes();
        return null;
    }
}
