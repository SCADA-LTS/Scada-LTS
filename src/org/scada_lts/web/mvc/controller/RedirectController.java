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

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.adapter.MangoUser;
import org.scada_lts.mango.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Controller for login system
 * Based on LoginController from Mango by Matthew Lohbihler
 * 
 * @author Marcin Gołda
 */
@Controller
@RequestMapping("/login_success")
public class RedirectController {
    private static final Log LOG= LogFactory.getLog(RedirectController.class);
    
	@RequestMapping(method = RequestMethod.GET)
	protected ModelAndView createForm(HttpServletRequest request) throws Exception {
		LOG.trace("/login_success");
		User user = Common.getUser(request);
		if(user == null) {
			Principal principal = request.getUserPrincipal();
			MangoUser mangoUser = new UserService();
			if(principal != null && principal.getName() != null)
				user = mangoUser.getUser(principal.getName());
			else if(request.getRemoteUser() != null)
				user = mangoUser.getUser(request.getRemoteUser());
			else
				throw new IllegalStateException();
			Common.setUser(request, user);
		}
		if (!StringUtils.isEmpty(user.getHomeUrl()))
			return new ModelAndView(new RedirectView(user.getHomeUrl()));
		else
			return new ModelAndView(new RedirectView("watch_list.shtm"));
	}
}
