package org.scada_lts.web.mvc.api.json;

import org.apache.commons.lang3.StringUtils;
import org.scada_lts.dao.SystemSettingsDAO;

public class ExportConfig {

    private String projectName;
    private String projectDescription;
    private int pointValuesMaxZip;
    private boolean includePointValues;
    private boolean includeUploadsFolder;
    private boolean includeGraphicsFolder;

    private ExportConfig(String projectName, String projectDescription, Integer pointValuesMaxZip, boolean includePointValues, boolean includeUploadsFolder, boolean includeGraphicsFolder) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.pointValuesMaxZip = pointValuesMaxZip == null ? 100 : pointValuesMaxZip;
        this.includePointValues = includePointValues;
        this.includeUploadsFolder = includeUploadsFolder;
        this.includeGraphicsFolder = includeGraphicsFolder;
    }

    public static ExportConfig defaultConfig(String projectName) {

        return new ExportConfig(getProjectName(projectName), "", 100,
                true, true, false);
    }

    public static ExportConfig onlyTextConfig(String projectName,
                                              Integer pointValuesMax,
                                              String projectDescription) {

        return new ExportConfig(getProjectName(projectName), projectDescription, pointValuesMax,
                true, false, false);
    }

    public static ExportConfig withImagesConfig(String projectName,
                                                Integer pointValuesMax,
                                                String projectDescription) {

        return new ExportConfig(getProjectName(projectName), projectDescription, pointValuesMax,
                true, true, true);
    }

    public static ExportConfig config(String projectName,
                                   boolean includeGraphicsFolder,
                                   boolean includeUploadsFolder) {

        return new ExportConfig(getProjectName(projectName), "", 100,
                true, includeUploadsFolder, includeGraphicsFolder);
    }

    public static ExportConfig config(String projectName,
                                   boolean includeGraphicsFolder,
                                   boolean includeUploadsFolder,
                                      Integer pointValuesMax) {

        return new ExportConfig(getProjectName(projectName), "", pointValuesMax,
                true, includeUploadsFolder, includeGraphicsFolder);
    }

    public static ExportConfig config(String projectName,
                                   boolean includeGraphicsFolder,
                                   boolean includeUploadsFolder,
                                      Integer pointValuesMax,
                                   String projectDescription) {

        return new ExportConfig(getProjectName(projectName), projectDescription, pointValuesMax,
                true, includeUploadsFolder, includeGraphicsFolder);
    }

    private static String getProjectName(String projectName) {
        if(StringUtils.isEmpty(projectName))
            projectName = SystemSettingsDAO.getValue(SystemSettingsDAO.INSTANCE_DESCRIPTION);
        return projectName;
    }


    public String getProjectName() {
        return projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public boolean isIncludePointValues() {
        return includePointValues;
    }

    public int getPointValuesMaxZip() {
        return pointValuesMaxZip;
    }

    public boolean isIncludeUploadsFolder() {
        return includeUploadsFolder;
    }

    public boolean isIncludeGraphicsFolder() {
        return includeGraphicsFolder;
    }
}
