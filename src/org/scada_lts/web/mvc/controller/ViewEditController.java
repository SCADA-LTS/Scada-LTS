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
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.UsersProfileService;
import org.scada_lts.mango.service.ViewService;
import org.scada_lts.utils.HttpParameterUtils;
import org.scada_lts.web.beans.ApplicationBeans;
import org.scada_lts.web.mvc.form.ViewEditForm;
import org.scada_lts.web.mvc.validator.ViewEditValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import com.serotonin.mango.Common;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;

import static com.serotonin.mango.web.dwr.util.HttpViewUtils.getView;
import static org.scada_lts.utils.PathSecureUtils.toSecurePath;
import static org.scada_lts.utils.UploadFileUtils.isToUploads;


@Controller
public class ViewEditController {
    private static final Log LOG = LogFactory.getLog(ViewEditController.class);

    private static final String SUBMIT_UPLOAD = "upload";
    private static final String SUBMIT_CLEAR_IMAGE = "clearImage";
    private static final String SUBMIT_SAVE = "save";
    private static final String SUBMIT_DELETE = "delete";
    private static final String SUBMIT_CANCEL = "cancel";

    private static final String FORM_VIEW = "viewEdit";
    private static final String FORM_OBJECT_NAME = "form";
    private static final String IMAGE_SETS_ATTRIBUTE = "imageSets";
    private static final String DYNAMIC_IMAGES_ATTRIBUTE = "dynamicImages";


    // TODO: these two shall be injected by Spring
    private String uploadDirectory= "uploads/";
    private String successUrl = "views.shtm";

    private int nextImageId = -1;

    private final ViewService viewService;

    @Autowired
    private ViewEditValidator validator;

    public ViewEditController() {
        this.viewService = new ViewService();
        //this.validator = ApplicationBeans.getBean("viewEditValidator", ViewEditValidator.class);
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public void setUploadDirectory(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    @RequestMapping(value = "/view_edit.shtm", method = RequestMethod.GET)
    protected ModelAndView showForm(HttpServletRequest request) throws Exception {
        User user = Common.getUser(request);
        View view = getView(viewService, request);

        if (view != null) {
            // An existing view.
            Permissions.ensureViewEditPermission(user, view);
        } else {
            // A new view.
            view = new View();
            view.setId(Common.NEW_ID);
            view.setUserId(user.getId());
            view.setXid(new ViewService().generateUniqueXid());
            request.getSession().setAttribute("emptyView", view);
            //TODO view.setHeight(?) and view.setWidth(?)
        }
        //user.setView(view);
        view.validateViewComponents(false);

        ViewEditForm form = new ViewEditForm();
        form.setView(view);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put(FORM_OBJECT_NAME, form);
        model.put(IMAGE_SETS_ATTRIBUTE, Common.ctx.getImageSets());
        model.put(DYNAMIC_IMAGES_ATTRIBUTE, Common.ctx.getDynamicImages());
        return new ModelAndView(FORM_VIEW, model);
    }


    @RequestMapping(value = "/view_edit.shtm", method = RequestMethod.POST)
    protected ModelAndView handleImage(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(FORM_OBJECT_NAME) ViewEditForm form)
    throws Exception{
        LOG.debug("ViewEditController:showForm");
        Map<String, Object> model = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        if (WebUtils.hasSubmitParameter(request, SUBMIT_CLEAR_IMAGE)) {
            User user = Common.getUser(request);
            View view = getView(viewService, request);
            Permissions.ensureViewPermission(user, view);

            form.setView(view);
            view.setBackgroundFilename(null);
        }

        if (WebUtils.hasSubmitParameter(request, SUBMIT_UPLOAD)) {
            User user = Common.getUser(request);
            View view = getView(viewService, request);
            Permissions.ensureViewPermission(user, view);

            form.setView(view);
            uploadFile(request, form, errors);
        }

        model.put("status", errors);
        model.put(FORM_OBJECT_NAME, form);
        model.put(IMAGE_SETS_ATTRIBUTE, Common.ctx.getImageSets());
        model.put(DYNAMIC_IMAGES_ATTRIBUTE, Common.ctx.getDynamicImages());
        return new ModelAndView(FORM_VIEW, model);
    }

    @RequestMapping(value = "/view_edit.shtm", method = RequestMethod.POST, params = { SUBMIT_SAVE })
    protected ModelAndView save(HttpServletRequest request, @ModelAttribute(FORM_OBJECT_NAME) ViewEditForm form, BindingResult result) {
        LOG.debug("ViewEditController:save");
        User user = Common.getUser();
        View view = getView(viewService, request);
        Permissions.ensureViewPermission(user, view);
        copyViewProperties(view, form.getView());
        form.setView(view);

        validator.validate(form, result);
        if(result.hasErrors())
        {
            LOG.debug("ViewEditController:save: HAS ERRORS.");
            Map<String, Object> model = new HashMap<String, Object>();
            model.put(FORM_OBJECT_NAME, form);
            model.put(IMAGE_SETS_ATTRIBUTE, Common.ctx.getImageSets());
            model.put(DYNAMIC_IMAGES_ATTRIBUTE, Common.ctx.getDynamicImages());
            return new ModelAndView(FORM_VIEW, model);
        }

        view.setUserId(Common.getUser(request).getId());
        new ViewService().saveView(view);
        return getSuccessRedirectView("viewId=" + view.getId());
    }

    @RequestMapping(value = "/view_edit.shtm", method = RequestMethod.POST, params = { SUBMIT_CANCEL })
    protected ModelAndView cancel(HttpServletRequest request, @ModelAttribute(FORM_OBJECT_NAME) ViewEditForm form) {
        LOG.debug("ViewEditController:cancel");
        User user = Common.getUser(request);
        View view = getView(viewService, request);
        Permissions.ensureViewPermission(user, view);
        form.setView(view);

        return getSuccessRedirectView("viewId=" + view.getId());
    }

    @RequestMapping(value = "/view_edit.shtm", method = RequestMethod.POST, params = { SUBMIT_DELETE })
    protected ModelAndView delete(HttpServletRequest request, @ModelAttribute(FORM_OBJECT_NAME) ViewEditForm form) {
        LOG.debug("ViewEditController:delete");
        User user = Common.getUser(request);
        View view = getView(viewService, request);
        Permissions.ensureViewPermission(user, view);
        form.setView(view);

        new ViewService().removeView(form.getView().getId());

        UsersProfileService usersProfileService = new UsersProfileService();
        usersProfileService.updateViewPermissions();
        return getSuccessRedirectView(null);
    }

    private void uploadFile(HttpServletRequest request, ViewEditForm form, Map<String, String> errors)  throws Exception  {
        if (WebUtils.hasSubmitParameter(request, SUBMIT_UPLOAD)) {
            MultipartFile file = form.getBackgroundImageMP();
            String errorMessage = LocalizableMessage.getMessage(Common.getBundle(),"viewEdit.upload.failed");
            if (file != null) {
                if(isToUploads(file))
                    upload(request, form, file);
                else {
                    LOG.warn("Image file is invalid.");
                    errors.put("errorMessage", errorMessage);
                }
            } else {
                LOG.warn("Image file is not attached.");
                errors.put("errorMessage", errorMessage);
            }
        }
    }

    private void upload(HttpServletRequest request, ViewEditForm form, MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        if (bytes != null && bytes.length > 0) {
            // Create the path to the upload directory.
            String path = request.getSession().getServletContext().getRealPath(uploadDirectory);
            LOG.info("ViewEditController:uploadFile: realpath="+path);

            // Make sure the directory exists.
            File dir = new File(path);
            dir.mkdirs();

            String fileName = file.getOriginalFilename();
            if(fileName != null) {
                saveFile(form, bytes, dir, fileName.toLowerCase());
            } else {
                LOG.warn("Image file is damaged!");
            }
        }
    }

    private void saveFile(ViewEditForm form, byte[] bytes,
                          File dir, String originalFileName) {
        int imageId = getNextImageId(dir); // Get an image id.
        String fileName = createFileName(imageId, originalFileName); // Create the image file name.
        toSecurePath(Paths.get(dir + File.separator + fileName))
                .flatMap(dest -> writeFile(bytes, dest))
                .ifPresent(image -> {
                    form.getView().setBackgroundFilename(uploadDirectory + fileName);
                    LOG.info("Image file has been successfully uploaded: " + image.getName());
                });
    }

    private static Optional<File> writeFile(byte[] bytes, File dest) {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(bytes);
            return Optional.of(dest);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return Optional.empty();
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
        String url = successUrl;
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
        targetView.setName(sourceView.getName());
        targetView.setXid(sourceView.getXid());
        targetView.setResolution(sourceView.getResolution());
        targetView.setAnonymousAccess(sourceView.getAnonymousAccess());
        targetView.setUserId(sourceView.getUserId());
    }

    private String createFileName(int imageId, String fileName) {
        return imageId + "." + FilenameUtils.getExtension(fileName);
    }
}
