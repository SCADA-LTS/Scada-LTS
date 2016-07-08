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
package com.serotonin.mango.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.DatabaseAccess;
import com.serotonin.mango.rt.EventManager;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataSource.http.HttpReceiverMulticaster;
import com.serotonin.mango.rt.maint.BackgroundProcessing;
import com.serotonin.mango.util.DocumentationManifest;
import com.serotonin.mango.view.DynamicImage;
import com.serotonin.mango.view.ImageSet;

import freemarker.template.Configuration;

public class ContextWrapper {
	private final ServletContext ctx;

	public ContextWrapper(ServletContext ctx) {
		this.ctx = ctx;
	}

	public ContextWrapper(HttpServletRequest request) {
		ctx = request.getSession().getServletContext();
	}

	public DatabaseAccess getDatabaseAccess() {
		return (DatabaseAccess) ctx
				.getAttribute(Common.ContextKeys.DATABASE_ACCESS);
	}

	@SuppressWarnings("unchecked")
	public List<ImageSet> getImageSets() {
		return (List<ImageSet>) ctx.getAttribute(Common.ContextKeys.IMAGE_SETS);
	}

	public List<String> getImageSetIds() {
		List<String> result = new ArrayList<String>();
		for (ImageSet s : getImageSets())
			result.add(s.getId());
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<DynamicImage> getDynamicImages() {
		return (List<DynamicImage>) ctx
				.getAttribute(Common.ContextKeys.DYNAMIC_IMAGES);
	}

	public List<String> getDynamicImageIds() {
		List<String> result = new ArrayList<String>();
		for (DynamicImage d : getDynamicImages())
			result.add(d.getId());
		return result;
	}

	public ImageSet getImageSet(String id) {
		List<ImageSet> imageSets = getImageSets();
		for (ImageSet imageSet : imageSets) {
			if (imageSet.getId().equals(id))
				return imageSet;
		}
		return null;
	}

	public DynamicImage getDynamicImage(String id) {
		List<DynamicImage> dynamicImages = getDynamicImages();
		for (DynamicImage dynamicImage : dynamicImages) {
			if (dynamicImage.getId().equals(id))
				return dynamicImage;
		}
		return null;
	}

	public RuntimeManager getRuntimeManager() {
		return (RuntimeManager) ctx
				.getAttribute(Common.ContextKeys.RUNTIME_MANAGER);
	}

	public EventManager getEventManager() {
		return (EventManager) ctx
				.getAttribute(Common.ContextKeys.EVENT_MANAGER);
	}

	public Configuration getFreemarkerConfig() {
		return (Configuration) ctx
				.getAttribute(Common.ContextKeys.FREEMARKER_CONFIG);
	}

	public BackgroundProcessing getBackgroundProcessing() {
		return (BackgroundProcessing) ctx
				.getAttribute(Common.ContextKeys.BACKGROUND_PROCESSING);
	}

	public HttpReceiverMulticaster getHttpReceiverMulticaster() {
		return (HttpReceiverMulticaster) ctx
				.getAttribute(Common.ContextKeys.HTTP_RECEIVER_MULTICASTER);
	}

	public Integer getDataPointByName(String dataPointQualifiedName) {
		Map<String, Integer> mapping = (Map<String, Integer>) ctx
				.getAttribute(Common.ContextKeys.DATA_POINTS_NAME_ID_MAPPING);
		Integer dataPointId = mapping.get(dataPointQualifiedName);
		if (dataPointId == null)
			return -1;
		else
			return dataPointId;
	}

	public ServletContext getServletContext() {
		return ctx;
	}

	public DocumentationManifest getDocumentationManifest() {
		DocumentationManifest dm = (DocumentationManifest) ctx
				.getAttribute(Common.ContextKeys.DOCUMENTATION_MANIFEST);

		if (dm == null) {
			try {
				dm = new DocumentationManifest();
			} catch (Exception e) {
				throw new ShouldNeverHappenException(e);
			}
			ctx.setAttribute(Common.ContextKeys.DOCUMENTATION_MANIFEST, dm);
		}

		return dm;
	}
}
