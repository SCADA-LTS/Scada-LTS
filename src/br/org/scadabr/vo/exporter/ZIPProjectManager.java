package br.org.scadabr.vo.exporter;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import com.serotonin.mango.vo.permission.Permissions;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.utils.HttpParameterUtils;
import org.scada_lts.utils.UploadFileUtils;
import org.scada_lts.web.mvc.api.json.ExportConfig;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import br.org.scadabr.vo.exporter.util.FileToPack;
import br.org.scadabr.vo.exporter.util.FileUtil;
import br.org.scadabr.web.mvc.controller.ProjectExporterController;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.dwr.EmportDwr;

import static org.scada_lts.utils.PathSecureUtils.toSecurePath;
import static org.scada_lts.utils.UploadFileUtils.*;

public class ZIPProjectManager {
	private static final String JSON_FILE_NAME = "json_project.txt";
	private static final String PROJECT_DESCRIPTION_FILE_NAME = "project_description.txt";

	private static final String FILE_SEPARATOR = System
			.getProperty("file.separator");

	private static final String uploadsFolder = "uploads" + FILE_SEPARATOR;
	private static final String graphicsFolder = "graphics" + FILE_SEPARATOR;

	private static final Log LOG = LogFactory.getLog(ZIPProjectManager.class);

	private ZipFile zipFile;

	private String projectName;
	private String projectDescription;
	private boolean includePointValues;
	private int maxPointValues;
	private boolean includeUploadsFolder;
	private boolean includeGraphicsFolder;

	public ZIPProjectManager() {}

	public ZIPProjectManager(ExportConfig config) {
		this.projectName = config.getProjectName();
		this.projectDescription = config.getProjectDescription();
		this.includePointValues = config.isIncludePointValues();
		this.maxPointValues = config.getPointValuesMaxZip();
		this.includeUploadsFolder = config.isIncludeUploadsFolder();
		this.includeGraphicsFolder = config.isIncludeGraphicsFolder();
	}

	public void exportProject(HttpServletRequest request,
							  HttpServletResponse response) throws Exception {

		Permissions.ensureAdmin(Common.getUser(request));
		extractExportParametersFromRequest(request);

		response.setHeader("Content-Disposition", "attachment; filename="
				+ projectName.replaceAll(" ", "") + ".zip");

		List<FileToPack> tempFiles = new ArrayList<>();

		tempFiles.add(buildProjectDescriptionFile(projectName, projectDescription));
		tempFiles.add(buildJSONFile(JSON_FILE_NAME, includePointValues));

		List<FileToPack> filesToZip = new ArrayList<>();
		if (includeUploadsFolder) {
			for(Path path: UploadFileUtils.getUploadsSystemFilePaths())
				filesToZip.addAll(getUploadsFolderFiles(path));
		}
		if (includeGraphicsFolder) {
			for(Path path: UploadFileUtils.getGraphicsSystemFilePaths())
				filesToZip.addAll(getGraphicsFolderFiles(path));
		}
		filesToZip.addAll(tempFiles);

		ServletOutputStream out = response.getOutputStream();
		FileUtil.compactFiles(out,
				filesToZip.toArray(new FileToPack[filesToZip.size()]));

		deleteFiles(tempFiles);
	}

	private void deleteFiles(List<FileToPack> tempFiles) {
		for(FileToPack file: tempFiles) {
			try {
				file.getFile().delete();
			} catch (Exception ex) {
				LOG.warn(ex.getMessage(), ex);
			}
		}
	}

	public ModelAndView setupToImportProject(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		List<String> errorList = new ArrayList<String>();
		model.put("errorMessages", errorList);

		try {
			extractImportParametersFromRequest(request);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			errorList.add(Common.getMessage("emport.uploadError",
					e.getMessage()));
			return new ModelAndView("import_result", model);
		}

		try {
			getProjectDescription(zipFile, model);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
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

		List<ZipEntry> graphicsFiles = getGraphicsFiles(graphicsFolder);
		restoreFiles(graphicsFiles, getGraphicsBaseSystemFilePath(getGraphicsSystemFileToWritePath()));

		List<ZipEntry> uploadFiles = getUploadFiles(uploadsFolder);
		restoreFiles(uploadFiles, getUploadsBaseSystemFilePath(getUploadsSystemFileToWritePath()));

		String jsonContent = getJsonContent();

		EmportDwr.importDataImpl(jsonContent, Common.getBundle(),
				Common.getUser());

	}

	private void restoreFiles(List<ZipEntry> uploadFiles, Path appPath) {
		for (ZipEntry zipEntry : uploadFiles) {
			String entryName = zipEntry.getName();
			if(!entryName.isEmpty()) {
				toSecurePath(Paths.get(appPath + File.separator + entryName))
						.ifPresent(file -> writeToFile(zipEntry, file));
			} else {
				LOG.error("entryName is empty");
			}
		}
	}

	private void writeToFile(ZipEntry zipEntry, File file) {
		try {
			if(!Files.isDirectory(file.toPath())) {
				String ext = FilenameUtils.getExtension(file.getName());
				if (!ext.isEmpty()) {
					if (file.getParent() != null) {
						File dir = new File(file.getParent());
						dir.mkdirs();
					}
					try (FileOutputStream out = new FileOutputStream(file)) {
						try (InputStream zipinputstream = this.zipFile.getInputStream(zipEntry)) {
							int n;
							byte[] buf = new byte[1024];
							while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
								out.write(buf, 0, n);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Write error for file: " + file + ", message: " + e.getMessage(), e);
		}
	}

	private List<ZipEntry> getUploadFiles(String uploadsFolder) {
		return filteringUploadFiles(filterZipFiles(uploadsFolder), zipFile);
	}

	private List<ZipEntry> getGraphicsFiles(String graphicsFolder) {
		return filteringGraphicsFiles(filterZipFiles(graphicsFolder), zipFile);
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
				true, includePointValues, maxPointValues, true, true, true);
		FileToPack file = new FileToPack(packAs,
                FileUtil.createTxtTempFile(jsonToExport));
		return file;
	}

	private List<FileToPack> getUploadsFolderFiles(Path uploadFolder) {
		List<File> files = filteringUploadFiles(FileUtil.getFilesOnDirectory(uploadFolder));

		List<FileToPack> pack = new ArrayList<>();

		for (File file : files) {
			String[] uploadsBasePath = file.getAbsolutePath().split("uploads");
			String filePartialPath = "uploads" + uploadsBasePath[1];
			pack.add(new FileToPack(filePartialPath, file));
		}
		return pack;
	}

	private List<FileToPack> getGraphicsFolderFiles(Path graphicFolder) {
		List<File> files = filteringGraphicsFiles(FileUtil.getFilesOnDirectory(graphicFolder));

		List<FileToPack> pack = new ArrayList<>();

		for (File file : files) {
			String[] uploadsBasePath = file.getAbsolutePath().split("graphics");
			String filePartialPath = "graphics" + uploadsBasePath[1];
			pack.add(new FileToPack(filePartialPath, file));
		}

		return pack;
	}

	private void getProjectDescription(ZipFile zipFile,
			Map<String, Object> model) throws IOException {
		ZipEntry jsonFile = zipFile
				.getEntry(ProjectExporterController.PROJECT_DESCRIPTION_FILE_NAME);

		try(DataInputStream in = new DataInputStream(zipFile.getInputStream(jsonFile))) {
			model.put("projectName", in.readLine());
			model.put("projectDescription", in.readLine());
			model.put("projectServerVersion", in.readLine());
			model.put("exportDate", in.readLine());
		}
	}

	private void extractExportParametersFromRequest(HttpServletRequest request) {

		if(this.projectName == null)
			this.projectName = HttpParameterUtils.getValue("projectName", request, a -> a).orElse("");

		if(this.projectDescription == null)
			this.projectDescription = HttpParameterUtils.getValue("projectDescription", request, a -> a).orElse("");

		if(this.maxPointValues <= 0)
			this.maxPointValues = HttpParameterUtils.getValue("pointValuesMaxZip", request, Integer::valueOf)
				.orElse(100);

		System.out.println(this.maxPointValues);

		if(!this.includePointValues)
			this.includePointValues = HttpParameterUtils.getValue("includePointValues", request, Boolean::valueOf).orElse(false);

		if(!this.includeUploadsFolder)
			this.includeUploadsFolder = HttpParameterUtils.getValue("includeUploadsFolder", request, Boolean::valueOf).orElse(false);

		if(!this.includeGraphicsFolder)
			this.includeGraphicsFolder = HttpParameterUtils.getValue("includeGraphicsFolder", request, Boolean::valueOf).orElse(false);
	}



	private void extractImportParametersFromRequest(HttpServletRequest request)
			throws Exception {
		MultipartHttpServletRequest mpRequest = (MultipartHttpServletRequest) request;

		MultipartFile multipartFile = mpRequest.getFile("importFile");
		if(!isZip(multipartFile)) {
			throw new IllegalArgumentException("Invalid zip file: " + multipartFile.getOriginalFilename());
		}

		File projectFile = File.createTempFile("temp", "");
		projectFile.deleteOnExit();
		multipartFile.transferTo(projectFile);

		Charset[] charsets = {StandardCharsets.UTF_8, StandardCharsets.UTF_16, StandardCharsets.ISO_8859_1,
				StandardCharsets.US_ASCII, Charset.forName("windows-1252"), StandardCharsets.UTF_16BE,
				StandardCharsets.UTF_16LE};

		for (Charset charset: charsets) {
			if((zipFile = toZipFile(projectFile, charset)) != null) {
				LOG.info("charset: " + charset);
				break;
			}
		}

		if (zipFile == null) {
			throw new IllegalStateException("File invalid: " + multipartFile.getOriginalFilename());
		}
	}

	private ZipFile toZipFile(File file, Charset charset) {
		try {
			return new ZipFile(file, charset);
		} catch (Exception ex) {
			LOG.warn(ex.getMessage() + ", charset: " + charset);
			return null;
		}
	}

	private FileToPack buildProjectDescriptionFile(String projectName,
			String projectDescription) {
		projectName = projectName + "\n";

		projectName += projectDescription + "\n";
		projectName += Common.getVersion() + "\n";
		projectName += new Date().toLocaleString();

		File file = FileUtil.createTxtTempFile(projectName);

		return new FileToPack(PROJECT_DESCRIPTION_FILE_NAME, file);
	}

	private String getJsonContent() throws Exception {
		ZipEntry jsonFile = zipFile
				.getEntry(ProjectExporterController.JSON_FILE_NAME);
		try(InputStream inputStream = zipFile.getInputStream(jsonFile)) {
			return convertContentToString(inputStream);
		}
	}

	private String convertContentToString(InputStream inputStream)
			throws Exception {
		try(DataInputStream in = new DataInputStream(inputStream)) {
			String strLine;

			StringBuilder contentAsString = new StringBuilder();
			while ((strLine = in.readLine()) != null) {
				contentAsString.append(strLine).append("\n");
			}
			return contentAsString.toString();
		}
	}
}