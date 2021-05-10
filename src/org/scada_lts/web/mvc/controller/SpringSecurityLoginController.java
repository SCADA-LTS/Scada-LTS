package org.scada_lts.web.mvc.controller;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.vo.User;
import com.serotonin.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/springLogin.htm")
public class SpringSecurityLoginController {
    private static final Log LOG= LogFactory.getLog(SpringSecurityLoginController.class);

    @RequestMapping(method = RequestMethod.GET)
    protected ModelAndView performLogin(HttpServletRequest request) throws Exception {
        String username = request.getUserPrincipal().getName();
        // Check if the user is already logged in.
        User user = Common.getUser(request);
        if (user != null && user.getUsername().equals(username)) {
            // The user is already logged in. Nothing to do.
            if (LOG.isDebugEnabled())
                LOG.debug("User is already logged in, not relogging in");
        }
        else {
            UserDao userDao = new UserDao();
            user = userDao.getUser(username);

            // Update the last login time.
            userDao.recordLogin(user.getId());

            // Add the user object to the session. This indicates to the rest
            // of the application whether the user is logged in or not.
            Common.setUser(request, user);
            if (LOG.isDebugEnabled())
                LOG.debug("User object added to session");
        }

////    TODO: There is problem when log into new, empty db schema ("page not found") - check HelpController?
//        if (user.isFirstLogin())
//            return new ModelAndView("help.shtm");
//        else
        if (!StringUtils.isEmpty(user.getHomeUrl()))
            return new ModelAndView(new RedirectView(user.getHomeUrl()));
        else
            return new ModelAndView(new RedirectView("watch_list.shtm"));
    }
}


