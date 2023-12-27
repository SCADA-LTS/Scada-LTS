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
package com.serotonin.mango.web.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.view.custom.CustomView;
import com.serotonin.mango.vo.User;

/**
 * @author Matthew Lohbihler
 */
public class CustomViewInitTag extends TagSupport {
    private static final long serialVersionUID = -1;

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int doStartTag() throws JspException {
        // Check the user id.
        User user = new UserDao().getUser(username);
        if (user == null)
            throw new JspException("Username '" + username + "' not found");
        if (user.isDisabled())
            throw new JspException("Username '" + username + "' is disabled");

        Common.setCustomView((HttpServletRequest) pageContext.getRequest(), new CustomView(user));

        return EVAL_BODY_INCLUDE;
    }

    @Override
    public void release() {
        super.release();
        username = null;
    }
}
