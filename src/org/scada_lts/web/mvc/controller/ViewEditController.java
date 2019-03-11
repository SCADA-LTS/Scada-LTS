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

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.mango.ScriptSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.form.ViewEditForm;
import org.scada_lts.web.mvc.validator.ViewEditValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.ViewDao;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;



@Controller
public class ViewEditController {
    private static final Log LOG = LogFactory.getLog(ViewEditController.class);

    private static final String SUBMIT_UPLOAD = "upload";
    private static final String SUBMIT_CLEAR_IMAGE = "clearImage";

    private static final String FORM_VIEW = "viewEdit";
    private static final String FORM_OBJECT_NAME = "form";
    private static final String IMAGE_SETS_ATTRIBUTE = "imageSets";
    private static final String DYNAMIC_IMAGES_ATTRIBUTE = "dynamicImages";

    private int nextImageId = -1;

    @Autowired
    ViewEditValidator validator;

    @RequestMapping(value = "/view_edit.shtm", method = RequestMethod.GET)
    protected ModelAndView showForm(HttpServletRequest request,@RequestParam(value="viewId", required=false) String viewIdStr) throws Exception {
        View view=null;
        User user = Common.getUser(request);

        if (viewIdStr != null) {
            // An existing view.
            view = new ViewDao().getView(Integer.parseInt(viewIdStr));
            Permissions.ensureViewEditPermission(user, view);
        } else {
            // A new view.
            view = new View();
            view.setId(Common.NEW_ID);
            view.setUserId(user.getId());
            view.setXid(new ViewDao().generateUniqueXid());
            //TODO view.setHeight(?) and view.setWidth(?)
        }
        user.setView(view);
        view.validateViewComponents(false);

        ViewEditForm form = new ViewEditForm();
        form.setView(view);
        Map<String, Object> map =fillMap(form);
        return new ModelAndView(FORM_VIEW, map);
    }

    @RequestMapping(value = "/view_edit.shtm", method = RequestMethod.POST)
    protected ModelAndView handleImage(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(FORM_OBJECT_NAME) ViewEditForm form)
    throws Exception{
        LOG.debug("ViewEdocument.getElementById('dwrScriptSessionid').value,ditController:showForm");
        if (WebUtils.hasSubmitParameter(request, SUBMIT_CLEAR_IMAGE)) {
            User user = Common.getUser(request);
            View view = user.getView();

            form.setView(view);
            view.setBackgroundFilename(null);
        }

        if (WebUtils.hasSubmitParameter(request, SUBMIT_UPLOAD)) {
            User user = Common.getUser(request);
            View view = user.getView();

            form.setView(view);
            uploadFile(request, form);
        }

        return new ModelAndView(FORM_VIEW, fillMap(form));
    }

    @RequestMapping(value = "/view_edit.shtm", method = RequestMethod.POST, params = { FinalValuesForControllers.SUBMIT_SAVE })
    protected ModelAndView save(HttpServletRequest request, @ModelAttribute(FORM_OBJECT_NAME) ViewEditForm form, BindingResult result) {
        LOG.debug("ViewEditController:save");
        User user = Common.getUser(request);

        user.setView(getViewFromContextAndRemoveViewFromContext(request));

        View view = user.getView();
        copyViewProperties(view, form.getView());
        form.setView(view);

        form.getView().getViewComponents().stream().forEach(viewComponent -> {
            if(viewComponent.getY()<0) {
                form.getView().removeViewComponent(viewComponent);
            }
        });

        validator.validate(form, result);
        if(result.hasErrors())
        {
            LOG.debug("ViewEditController:save: HAS ERRORS.");
            return new ModelAndView(FORM_VIEW, fillMap(form));
        }

        view.setUserId(user.getId());
        new ViewDao().saveView(view);

        return getSuccessRedirectView("viewId=" + form.getView().getId());
    }

    @RequestMapping(value = "/view_edit.shtm", method = RequestMethod.POST, params = { FinalValuesForControllers.SUBMIT_CANCEL })
    protected ModelAndView cancel(HttpServletRequest request, @ModelAttribute(FORM_OBJECT_NAME) ViewEditForm form) {
        LOG.debug("ViewEditController:cancel");
        User user = Common.getUser(request);
        user.setView(getViewFromContextAndRemoveViewFromContext(request));
        View view = user.getView();
        form.setView(view);

        return getSuccessRedirectView("viewId=" + form.getView().getId());
    }

    @RequestMapping(value = "/view_edit.shtm", method = RequestMethod.POST, params = { FinalValuesForControllers.SUBMIT_DELETE })
    protected ModelAndView delete(HttpServletRequest request, @ModelAttribute(FORM_OBJECT_NAME) ViewEditForm form) {
        LOG.debug("ViewEditController:delete");
        User user = Common.getUser(request);
        user.setView(getViewFromContextAndRemoveViewFromContext(request));
        View view = user.getView();
        form.setView(view);

        new ViewDao().removeView(form.getView().getId());
        return getSuccessRedirectView(null);
    }
    private View getViewFromContextAndRemoveViewFromContext(HttpServletRequest request){
        View view = (View) ScriptSession.getObjectForScriptSession(
                request.getSession().getId(),
                request.getParameter(FinalValuesForControllers.DWR_SCRIPT_SESSION_ID));
        ScriptSession.removeScriptSessionForObjectBySessionIdAndScriptSessionId(request.getSession().getId(),
                request.getParameter(FinalValuesForControllers.DWR_SCRIPT_SESSION_ID));
        LOG.debug("ViewEditController:getViewFromContextAndRemoveViewFromContext: View has been removed from Context.");
        return view;

    }
    private Map<String, Object> fillMap(ViewEditForm form){
        Map<String, Object> model = new HashMap<String, Object>();
        model.put(FORM_OBJECT_NAME, form);
        model.put(IMAGE_SETS_ATTRIBUTE, Common.ctx.getImageSets());
        model.put(DYNAMIC_IMAGES_ATTRIBUTE, Common.ctx.getDynamicImages());
        return model;
    }
    private void uploadFile(HttpServletRequest request, ViewEditForm form)  throws Exception  {
        if (WebUtils.hasSubmitParameter(request, SUBMIT_UPLOAD)) {
            if (form.getBackgroundImageMP() != null) {
                byte[] bytes = form.getBackgroundImageMP().getBytes();
                if (bytes != null && bytes.length > 0) {
                    String uploadDirectory= "uploads/";
                    // Create the path to the upload directory.
                    String path = request.getSession().getServletContext().getRealPath(uploadDirectory);
                    LOG.info("ViewEditController:uploadFile: realpath="+path);

                    // Make sure the directory exists.
                    File dir = new File(path);
                    dir.mkdirs();
                    // Get an image id.
                    int imageId = getNextImageId(dir);

                    // Create the image file name.
                    String filename = Integer.toString(imageId);
                    int dot = form.getBackgroundImageMP().getOriginalFilename().lastIndexOf('.');
                    if (dot != -1)
                        filename += form.getBackgroundImageMP().getOriginalFilename().substring(dot);

                    // Save the file.
                    FileOutputStream fos = new FileOutputStream(new File(dir, filename));
                    fos.write(bytes);
                    fos.close();

                    form.getView().setBackgroundFilename(uploadDirectory + filename);
                }
            }
        }
    }

    private int getNextImageId(File uploadDir) {
        if (nextImageId == -1) {
            // Synchronize
            synchronized (this) {
                if (nextImageId == -1) {
                    // Initialize the next image id field.
                    nextImageId = 1;

                    String[] names = uploadDir.list();
                    int index, dot;
                    for (int i = 0; i < names.length; i++) {
                        dot = names[i].lastIndexOf('.');
                        try {
                            if (dot == -1)
                                index = Integer.parseInt(names[i]);
                            else
                                index = Integer.parseInt(names[i].substring(0,
                                        dot));
                            if (index >= nextImageId)
                                nextImageId = index + 1;
                        } catch (NumberFormatException e) { /* no op */
                        }
                    }
                }
            }
        }
        return nextImageId++;
    }

    protected  ModelAndView getSuccessRedirectView(String queryString) {
        String url = "views.shtm";
        if (queryString != null && queryString.trim().length() > 0) {
            if (queryString.charAt(0) != '?')
                url += '?' + queryString;
            else
                url += queryString;
        }
        RedirectView redirectView = new RedirectView(url, true);
        return new ModelAndView(redirectView);
    }

    private void copyViewProperties(View targetView, View sourceView) {
        targetView.setId(sourceView.getId());
        targetView.setName(sourceView.getName());
        targetView.setXid(sourceView.getXid());
        targetView.setResolution(sourceView.getResolution());
        targetView.setAnonymousAccess(sourceView.getAnonymousAccess());
        targetView.setUserId(sourceView.getUserId());
    }
}
