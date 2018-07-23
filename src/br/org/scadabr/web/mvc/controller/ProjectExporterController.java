package br.org.scadabr.web.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import br.org.scadabr.vo.exporter.ZIPProjectManager;

public class ProjectExporterController extends AbstractController {

	public static final String JSON_FILE_NAME = "json_project.txt";
	public static final String PROJECT_DESCRIPTION_FILE_NAME = "project_description.txt";

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ZIPProjectManager exporter = new ZIPProjectManager();

		exporter.exportProject(request, response);

		return null;
	}

}
