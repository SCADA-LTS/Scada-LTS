/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2009 Serotonin Software Technologies Inc.
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
package br.org.scadabr.web.mvc.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class HelpController extends ParameterizableViewController {
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();

		model.put("versionNumber", retrieveVersionNumber());
		model.put("buildNumber", retrieveBuildNumber());

		return new ModelAndView(getViewName(), model);
	}

	private String retrieveVersionNumber() {
		String versionNumber = "";

		try {
			ServletContext sCon = getServletContext();
			Properties prop = new Properties();
			prop.load(sCon.getResourceAsStream("/META-INF/MANIFEST.MF"));

			versionNumber = prop.getProperty("Version-Number");

		} catch (IOException ex) {
			versionNumber = "No version number available.";
		}
		return versionNumber;
	}

	private String retrieveBuildNumber() {
		String buildNumber = "";

		try {
			ServletContext sCon = getServletContext();
			Properties prop = new Properties();
			prop.load(sCon.getResourceAsStream("/META-INF/MANIFEST.MF"));

			buildNumber = prop.getProperty("Build-Number");

		} catch (IOException ex) {
			buildNumber = "No build number available.";
		}
		return buildNumber;
	}
}