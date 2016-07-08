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
package com.serotonin.mango.web.mvc.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.ViewDao;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.web.mvc.SimpleFormRedirectController;
import com.serotonin.mango.web.mvc.form.ViewEditForm;
import com.serotonin.util.ValidationUtils;
import com.serotonin.web.dwr.DwrResponseI18n;

public class ViewEditController extends SimpleFormRedirectController {
	private static final String SUBMIT_UPLOAD = "upload";
	private static final String SUBMIT_CLEAR_IMAGE = "clearImage";
	private static final String SUBMIT_SAVE = "save";
	private static final String SUBMIT_DELETE = "delete";
	private static final String SUBMIT_CANCEL = "cancel";

	private String uploadDirectory;
	private int nextImageId = -1;

	public void setUploadDirectory(String uploadDirectory) {
		this.uploadDirectory = uploadDirectory;
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request) {
		View view;
		User user = Common.getUser(request);

		if (!isFormSubmission(request)) {
			// Fresh hit. Get the id.
			String viewIdStr = request.getParameter("viewId");
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
			}
			user.setView(view);
			view.validateViewComponents(false);
		} else
			view = user.getView();

		ViewEditForm form = new ViewEditForm();
		form.setView(view);
		return form;
	}

	@Override
	protected Map referenceData(HttpServletRequest request, Object command,
			Errors errors) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("imageSets", Common.ctx.getImageSets());
		model.put("dynamicImages", Common.ctx.getDynamicImages());
		return model;
	}

	@Override
	protected void onBindAndValidate(HttpServletRequest request,
			Object command, BindException errors) throws Exception {

		ViewEditForm form = (ViewEditForm) command;

		if (hasSubmitParameter(request, SUBMIT_UPLOAD)) {
			if (form.getBackgroundImageMP() != null) {
				byte[] bytes = form.getBackgroundImageMP().getBytes();
				if (bytes != null && bytes.length > 0) {
					// Create the path to the upload directory.
					String path = request.getSession().getServletContext()
							.getRealPath(uploadDirectory);

					// Make sure the directory exists.
					File dir = new File(path);
					dir.mkdirs();

					// Get an image id.
					int imageId = getNextImageId(dir);

					// Create the image file name.
					String filename = Integer.toString(imageId);
					int dot = form.getBackgroundImageMP().getOriginalFilename()
							.lastIndexOf('.');
					if (dot != -1)
						filename += form.getBackgroundImageMP()
								.getOriginalFilename().substring(dot);

					// Save the file.
					FileOutputStream fos = new FileOutputStream(new File(dir,
							filename));
					fos.write(bytes);
					fos.close();

					form.getView().setBackgroundFilename(
							uploadDirectory + filename);
				}
			}
		}

		if (hasSubmitParameter(request, SUBMIT_CLEAR_IMAGE))
			form.getView().setBackgroundFilename(null);

		if (hasSubmitParameter(request, SUBMIT_SAVE)) {
			DwrResponseI18n response = new DwrResponseI18n();
			form.getView().validate(response);
			ValidationUtils.reject(errors, "view.", response);
		}
	}

	@Override
	protected boolean isFormChangeRequest(HttpServletRequest request) {
		return hasSubmitParameter(request, SUBMIT_UPLOAD)
				|| hasSubmitParameter(request, SUBMIT_CLEAR_IMAGE);
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		ViewEditForm form = (ViewEditForm) command;
		ViewDao viewDao = new ViewDao();

		if (hasSubmitParameter(request, SUBMIT_CANCEL))
			return getSuccessRedirectView("viewId=" + form.getView().getId());

		if (hasSubmitParameter(request, SUBMIT_DELETE)) {
			viewDao.removeView(form.getView().getId());
			return getSuccessRedirectView();
		}

		if (hasSubmitParameter(request, SUBMIT_SAVE)) {
			View view = form.getView();

			view.setUserId(Common.getUser(request).getId());
			viewDao.saveView(view);
			return getSuccessRedirectView("viewId=" + form.getView().getId());
		}

		throw new ShouldNeverHappenException("Invalid submit parameter");
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
}
