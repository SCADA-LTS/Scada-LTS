package org.scada_lts.web.mvc.controller;

import com.serotonin.mango.Common;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class VueAppController {

    private static String VIEW_NAME = "app";

    @GetMapping(value = "/app.shtm")
    public ModelAndView loadNewUI(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        Common.getUser(request).setHideHeader(true);
        return new ModelAndView(VIEW_NAME, model);
    }
}
