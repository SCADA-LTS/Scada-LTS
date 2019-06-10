package br.org.scadabr.vo.exporter;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import br.org.scadabr.vo.exporter.util.FileToPack;
import br.org.scadabr.vo.exporter.util.FileUtil;
import br.org.scadabr.web.mvc.controller.ProjectExporterController;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.dwr.EmportDwr;

public class ZIPProjectManager {
	private static final String JSON_FILE_NAME = "json_project.txt";
	private static final String PROJECT_DESCRIPTION_FILE_NAME = "project_description.txt";

	private static final String FILE_SEPARATOR = System
			.getProperty("file.separator");

	private static final String uploadsFolder = "uploads" + FILE_SEPARATOR;
	private static final String graphicsFolder = "graphics" + FILE_SEPARATOR;

	private ZipFile zipFile;

	private String projectName;
	private String projectDescription;
	private boolean includePointValues;
	private int maxPointValues;
	private boolean includeUploadsFolder;
	private boolean includeGraphicsFolder;

	public void exportProject(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		extractExportParametersFromRequest(request);

		response.setHeader("Content-Disposition", "attachment; filename="
				+ projectName.replaceAll(" ", "") + ".zip");

		List<FileToPack> filesToZip = new ArrayList<FileToPack>();

		filesToZip.add(buildProjectDescriptionFile(projectName,
				projectDescription));

		filesToZip.add(buildJSONFile(JSON_FILE_NAME, includePointValues));

		if (includeUploadsFolder)
			filesToZip.addAll(getUploadsFolderFiles());

		if (includeGraphicsFolder)
			filesToZip.addAll(getGraphicsFolderFiles());

		ServletOutputStream out = response.getOutputStream();
		FileUtil.compactFiles(out,
				filesToZip.toArray(new FileToPack[filesToZip.size()]));

	}

	public ModelAndView setupToImportProject(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		List<String> errorList = new ArrayList<String>();
		model.put("errorMessages", errorList);

		try {
			extractImportParametersFromRequest(request);
		} catch (Exception e) {
			e.printStackTrace();
			errorList.add(Common.getMessage("emport.uploadError",
					e.getMessage()));
			return new ModelAndView("import_result", model);
		}

		try {
			getProjectDescription(zipFile, model);
		} catch (Exception e) {
			e.printStackTrace();
			errorList.add(Common.getMessage("emport.invalidFile",
					e.getMessage()));
			return new ModelAndView("import_result", model);
		}

		// TODO atualizar sistema de upgrade mango -> scadabr
		// String version = (String) model.get("projectServerVersion");
		// if (DBUpgrade.isUpgradeNeeded(version)) {
		// errorList.add(Common.getMessage("emport.versionError", version,
		// Common.getVersion()));
		// return new ModelAndView("import_result", model);
		// }

		User user = Common.getUser(request);
		user.setUploadedProject(this);
		return new ModelAndView("import_result", model);

	}

	public void importProject() throws Exception {

		List<ZipEntry> graphicsFiles = getGraphicsFiles();
		restoreFiles(graphicsFiles);

		List<ZipEntry> uploadFiles = getUploadFiles();
		restoreFiles(uploadFiles);

		String jsonContent = getJsonContent();

		EmportDwr.importDataImpl(jsonContent, Common.getBundle(),
				Common.getUser());

	}

	private void restoreFiles(List<ZipEntry> uploadFiles) {
		String appPath = Common.ctx.getServletContext().getRealPath(
				FILE_SEPARATOR);

		byte[] buf = new byte[1024];
		try {
			for (ZipEntry zipEntry : uploadFiles) {
				InputStream zipinputstream;

				zipinputstream = this.zipFile.getInputStream(zipEntry);

				String entryName = zipEntry.getName();

				int n;

				String fileName = zipEntry.getName();

				File f = new File(appPath + fileName);

				File newFile = new File(entryName);

				String directory = newFile.getParent();

				if (directory != null) {
					if (newFile.isDirectory()) {
						break;
					}
					File dirFile = new File(appPath + directory);
					dirFile.mkdir();
				}

				FileOutputStream out = new FileOutputStream(f);

				while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
					out.write(buf, 0, n);

				out.close();
				zipinputstream.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<ZipEntry> getUploadFiles() {
		return filterZipFiles(uploadsFolder);
	}

	private List<ZipEntry> getGraphicsFiles() {
		return filterZipFiles(graphicsFolder);
	}

	private List<ZipEntry> filterZipFiles(String startsWith) {
		List<ZipEntry> result = new ArrayList<ZipEntry>();

		Enumeration entries = zipFile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();

			if (entry.getName().startsWith(startsWith)) {
				result.add(entry);
			}
		}

		return result;
	}

	private FileToPack buildJSONFile(String packAs, boolean includePointValues) {
		String jsonToExport = EmportDwr.createExportJSON(3, true, true, true,
				true, true, true, true, true, true, true, true, true, true,
				true, includePointValues, maxPointValues, true, true);
		FileToPack file = new FileToPack(packAs, FileUtil.createTxtTempFile(
				packAs, jsonToExport));
		return file;
	}

	private List<FileToPack> getUploadsFolderFiles() {
		String uploadFolder = Common.ctx.getServletContext().getRealPath(
				FILE_SEPARATOR)
				+ "uploads";

		List<File> files = FileUtil.getFilesOnDirectory(uploadFolder);

		List<FileToPack> pack = new ArrayList<FileToPack>();
		for (File file : files) {

			String filePartialPath = uploadsFolder + file.getName();

			pack.add(new FileToPack(filePartialPath.substring(0, 7)
					+ FILE_SEPARATOR + filePartialPath.substring(8), file));
		}
		return pack;
	}

	private List<FileToPack> getGraphicsFolderFiles() {
		String graphicFolder = Common.ctx.getServletContext().getRealPath(
				FILE_SEPARATOR)
				+ "graphics";

		List<File> files = FileUtil.getFilesOnDirectory(graphicFolder);

		List<FileToPack> pack = new ArrayList<FileToPack>();

		for (File file : files) {

			String[] pathDivided = null;

			pathDivided = file.getAbsolutePath().split("graphics");

			String filePartialPath = "graphics" + pathDivided[1];

			pack.add(new FileToPack(filePartialPath, file));
		}

		return pack;
	}

	private void getProjectDescription(ZipFile zipFile,
			Map<String, Object> model) throws IOException {
		ZipEntry jsonFile = zipFile
				.getEntry(ProjectExporterController.PROJECT_DESCRIPTION_FILE_NAME);

		DataInputStream in = new DataInputStream(
				zipFile.getInputStream(jsonFile));

		model.put("projectName", in.readLine());
		model.put("projectDescription", in.readLine());
		model.put("projectServerVersion", in.readLine());
		model.put("exportDate", in.readLine());

		in.close();
	}

	private void extractExportParametersFromRequest(HttpServletRequest request) {
		this.projectName = request.getParameter("projectName");
		this.projectDescription = request.getParameter("projectDescription");
		this.includePointValues = Boolean.parseBoolean(request
				.getParameter("includePointValues"));
		this.maxPointValues = Integer.parseInt(request
				.getParameter("pointValuesMaxZip"));

		System.out.println(this.maxPointValues);
		this.includeUploadsFolder = Boolean.parseBoolean(request
				.getParameter("includeUploadsFolder"));
		this.includeGraphicsFolder = Boolean.parseBoolean(request
				.getParameter("includeGraphicsFolder"));

	}

	private void extractImportParametersFromRequest(HttpServletRequest request)
			throws Exception {
		MultipartHttpServletRequest mpRequest = (MultipartHttpServletRequest) request;

		MultipartFile multipartFile = mpRequest.getFile("importFile");

		File projectFile = File.createTempFile("temp", "");
		FileOutputStream fos = new FileOutputStream(projectFile);
		fos.write(multipartFile.getBytes());
		fos.close();
		projectFile.deleteOnExit();

		this.zipFile = toZipFile(projectFile);
	}

	private ZipFile toZipFile(File file) throws Exception {
		ZipFile zipFile = new ZipFile(file);
		return zipFile;
	}

	private FileToPack buildProjectDescriptionFile(String projectName,
			String projectDescription) {
		projectName = projectName + "\n";

		projectName += projectDescription + "\n";
		projectName += Common.getVersion() + "\n";
		projectName += new Date().toLocaleString();

		File file = FileUtil.createTxtTempFile("tempprojectdescription",
				projectName);

		return new FileToPack(PROJECT_DESCRIPTION_FILE_NAME, file);
	}

	private String getJsonContent() throws Exception {
		ZipEntry jsonFile = zipFile
				.getEntry(ProjectExporterController.JSON_FILE_NAME);
		zipFile.getInputStream(jsonFile);

		return convertContentToString(zipFile.getInputStream(jsonFile));
	}

	private String convertContentToString(InputStream inputStream)
			throws Exception {
		DataInputStream in = new DataInputStream(inputStream);
		String strLine;
		String file = "";

		while ((strLine = in.readLine()) != null) {
			file += strLine + "\n";
		}
		in.close();
		return file;
	}
}