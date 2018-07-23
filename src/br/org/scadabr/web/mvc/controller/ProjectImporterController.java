package br.org.scadabr.web.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import br.org.scadabr.vo.exporter.ZIPProjectManager;

public class ProjectImporterController extends AbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ZIPProjectManager importer = new ZIPProjectManager();

		ModelAndView modelAndView = importer.setupToImportProject(request,
				response);

		return modelAndView;
	}

}
