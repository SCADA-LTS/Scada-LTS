/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.scada_lts.web.mvc.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.mvc.controller.ScadaLocaleUtils;
import com.serotonin.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import static com.serotonin.mango.web.mvc.controller.ControllerUtils.getHomeUrl;

/**
 * Controller for login system
 * Based on LoginController from Mango by Matthew Lohbihler
 *
 * @author Marcin Gołda
 */
@Controller
@RequestMapping("/login.htm")
public class LoginController {
    private static final Log LOG= LogFactory.getLog(LoginController.class);

    @RequestMapping(method = RequestMethod.GET)
    protected ModelAndView createForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.trace("/login.htm");
        User user = Common.getUser(request);
        if(user != null) {
            return new ModelAndView("redirect:" + getHomeUrl(user));
        } else {
            request.setAttribute("toYear", DateTime.now().getYear());
            ScadaLocaleUtils.setLocaleInSession(request, response);
            return new ModelAndView("login");
        }
    }
}
