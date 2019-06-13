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

abstract public class ViewGraphic {
    private final String id;
    private final String name;
    private final int width;
    private final int height;
    private final int textX;
    private final int textY;

    ViewGraphic(String id, String name, int width, int height, int textX, int textY) {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.textX = textX;
        this.textY = textY;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTextX() {
        return textX;
    }

    public int getTextY() {
        return textY;
    }

    public boolean isImageSet() {
        return false;
    }

    public boolean isDynamicImage() {
        return false;
    }
}
