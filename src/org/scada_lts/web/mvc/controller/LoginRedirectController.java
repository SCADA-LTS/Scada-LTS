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
@RequestMapping("/loginRedirect")
public class LoginRedirectController {

    private static final Log LOG = LogFactory.getLog(LoginRedirectController.class);

    @RequestMapping(method = RequestMethod.GET)
    protected ModelAndView performLogin(HttpServletRequest request){
        String username = request.getUserPrincipal().getName();
        User user = Common.getUser(request);
        if (user != null && user.getUsername().equals(username)) {
            if (LOG.isDebugEnabled())
                LOG.debug("User is already logged in, not relogging in");
        }
        else {
            UserDao userDao = new UserDao();
            user = userDao.getUser(username);

            userDao.recordLogin(user.getId());

            Common.setUser(request, user);
            if (LOG.isDebugEnabled())
                LOG.debug("User object added to session");
        }

        if (!StringUtils.isEmpty(user.getHomeUrl()))
            return new ModelAndView(new RedirectView(user.getHomeUrl()));
        else
            return new ModelAndView(new RedirectView("watch_list.shtm"));
    }
}


