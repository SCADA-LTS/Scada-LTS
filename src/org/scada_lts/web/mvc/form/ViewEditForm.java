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
package org.scada_lts.web.mvc.form;

import org.springframework.web.multipart.MultipartFile;

import com.serotonin.mango.view.View;

public class ViewEditForm {
    private View view;
    private MultipartFile backgroundImageMP;

    public MultipartFile getBackgroundImageMP() {
        return backgroundImageMP;
    }

    public void setBackgroundImageMP(MultipartFile backgroundImageMP) {
        this.backgroundImageMP = backgroundImageMP;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
