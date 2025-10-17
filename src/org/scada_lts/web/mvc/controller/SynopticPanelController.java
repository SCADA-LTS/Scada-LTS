package org.scada_lts.web.mvc.controller;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SynopticPanelController {

    @RequestMapping(value = "/synoptic_panel", method = RequestMethod.GET)
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
        User user = Common.getUser(request);
        if (user == null) {
            return new ModelAndView("redirect:/login.htm");
        } else {
            Map<String, Object> model = new HashMap<>();
            model.put("appName", request.getContextPath());
            model.put("appPort", request.getLocalPort());
            return new ModelAndView("synopticPanel", model);
        }
    }
}