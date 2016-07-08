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
package com.serotonin.mango.view;

import com.serotonin.mango.view.graphic.GraphicRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.util.StringUtils;

@Deprecated
// use ViewComponent instead
public class PointView {
    // Design time attributes.
    private int id;
    private int viewId;
    private String nameOverride;
    private boolean settableOverride;
    private String bkgdColorOverride;
    private boolean displayControls;
    private int x;
    private int y;
    private GraphicRenderer graphicRenderer;
    private DataPointVO dataPoint;

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String getName() {
        if (!StringUtils.isEmpty(nameOverride))
            return nameOverride;
        return dataPoint.getName();
    }

    public boolean isSettable() {
        if (!dataPoint.getPointLocator().isSettable())
            return false;
        return settableOverride;
    }

    public int getDataType() {
        return dataPoint.getPointLocator().getDataTypeId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GraphicRenderer getGraphicRenderer() {
        return graphicRenderer;
    }

    public void setGraphicRenderer(GraphicRenderer graphicRenderer) {
        this.graphicRenderer = graphicRenderer;
    }

    public int getDataPointId() {
        return dataPoint.getId();
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getNameOverride() {
        return nameOverride;
    }

    public void setNameOverride(String nameOverride) {
        this.nameOverride = nameOverride;
    }

    public boolean isSettableOverride() {
        return settableOverride;
    }

    public void setSettableOverride(boolean settableOverride) {
        this.settableOverride = settableOverride;
    }

    public DataPointVO getDataPoint() {
        return dataPoint;
    }

    public void setDataPoint(DataPointVO dataPoint) {
        this.dataPoint = dataPoint;
    }

    public String getBkgdColorOverride() {
        return bkgdColorOverride;
    }

    public void setBkgdColorOverride(String bkgdColorOverride) {
        this.bkgdColorOverride = bkgdColorOverride;
    }

    public boolean isDisplayControls() {
        return displayControls;
    }

    public void setDisplayControls(boolean displayControls) {
        this.displayControls = displayControls;
    }
}
