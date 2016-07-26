/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
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

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/** 
 * Controller to set language.
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
@Controller
public class ViewUtilController {
	
	private static final Log LOG = LogFactory.getLog(ViewUtilController.class);
	
	@RequestMapping(value = "/viewutil/{locale}", method = RequestMethod.POST)
	public @ResponseBody String setLocale(@PathVariable("locale") String locale, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("/viewutil/{locale} locale:" + locale );
		LocaleResolver localeResolver = new SessionLocaleResolver();
		LocaleEditor localeEditor = new LocaleEditor();
		localeEditor.setAsText(locale);
		localeResolver.setLocale(request, response,  (Locale) localeEditor.getValue());
		
		return "true";
	}
	
	@RequestMapping(value = "/viewutil/pathToLogo", method = RequestMethod.GET)
	public @ResponseBody String getPathToCustomLogo(HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("/viewutil/customlogo");
		String pathToLogo;
		try {
			pathToLogo=ScadaConfig.getInstance().getProperty(ScadaConfig.PATH_TO_CUSTOM_LOGO);
		} catch (IOException e) {
			return ScadaConfig.VALUE_DEFAULT_PATH_TO_LOGO;
		}
		if (pathToLogo != null) {
			return pathToLogo;
		} else {
			return ScadaConfig.VALUE_DEFAULT_PATH_TO_LOGO;
		}
	}
	
	@RequestMapping(value = "/viewutil/pathToCommonsCSS", method = RequestMethod.GET)
	public @ResponseBody String getPathToCustomCSS(HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("/viewutil/customlogo");
		String pathToCommonsCSS;
		try {
			pathToCommonsCSS=ScadaConfig.getInstance().getProperty(ScadaConfig.PATH_TO_CUSTOM_CSS);
		} catch (IOException e) {
			return ScadaConfig.VALUE_DEFAULT_PATH_TO_CSS;
		}
		if (pathToCommonsCSS != null) {
			return pathToCommonsCSS;
		} else {
			return ScadaConfig.VALUE_DEFAULT_PATH_TO_CSS;
		}
	}
	
	@RequestMapping(value = "/viewutil/pathToCommonsCSSForNewViews", method = RequestMethod.GET)
	public @ResponseBody String getPathToCustomCSSForNew(HttpServletRequest request, HttpServletResponse response) {
		String pathToCommonsCSSForNewViews;
		LOG.debug("/viewutil/pathToCommonsCSSForNewViews");
		try {
			pathToCommonsCSSForNewViews=ScadaConfig.getInstance().getProperty(ScadaConfig.PATH_TO_CUSTOM_CSS_FOR_NEW_VIEWS);
		} catch (IOException e) {
			return ScadaConfig.VALUE_DEFAULT_PATH_TO_CSS_FOR_NEW_VIEWS;
		}
		if (pathToCommonsCSSForNewViews != null) {
			return pathToCommonsCSSForNewViews;
		} else {
			return ScadaConfig.VALUE_DEFAULT_PATH_TO_CSS_FOR_NEW_VIEWS;
		}
	}

}
