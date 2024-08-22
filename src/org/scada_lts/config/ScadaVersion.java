package org.scada_lts.config;

import com.serotonin.mango.Common;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.SystemSettingsDAO;


import java.io.FileInputStream;
import java.util.Properties;

import static org.scada_lts.dao.SystemSettingsDAO.TOP_DESCRIPTION;
import static org.scada_lts.dao.SystemSettingsDAO.TOP_DESCRIPTION_PREFIX;

/**
 * Scada Version class
 *
 * Handle the static information about the application
 * that was provided in "version.properties" file. If some
 * properties are missing it will render the default values.
 * That information is rendered next to the Scada-LTS Logo in a header.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
public class ScadaVersion {

    private static final Log LOG = LogFactory.getLog(ScadaVersion.class);
    private static final String FILENAME = "version.properties";

    private static boolean showVersionInfo;
    private static String versionNumber;
    private static String buildNumber;
    private static String commitNumber;
    private static String runningOs;
    private static String runningJava;
    private static String companyName;
    private static String poweredBy;

    private static ScadaVersion instance = null;
    private static Properties configuration;

    private ScadaVersion() {
        try {
            configuration = new Properties();
            try(FileInputStream fis = new FileInputStream(getScadaVersionFilePath())) {
                configuration.load(fis);
            }
            setUpScadaVersionProperties();
        } catch (Exception e) {
            LOG.error("Failed to load 'version.properties' file! Using default values. Message: " + e.getMessage());
        }
    }

    private static void setUpScadaVersionProperties() {
        if (configuration != null) {
            showVersionInfo = Boolean.parseBoolean(configuration.getProperty("slts.version.show", "false"));
            versionNumber = configuration.getProperty("slts.version.number",  "Unknown");
            buildNumber = configuration.getProperty("slts.version.build", "0");
            commitNumber = configuration.getProperty("slts.version.commit", "");
            runningOs = configuration.getProperty("slts.version.os", System.getProperty("os.name") + System.getProperty("os.version"));
            runningJava = System.getProperty("java.runtime.name") + System.getProperty("java.runtime.version");
            companyName = configuration.getProperty("slts.version.companyName", "");
            poweredBy = configuration.getProperty("slts.version.poweredBy", "");
        }
    }

    /**
     * Print Scada-LTS welcome message.
     *
     * That message contains the version and build number
     * that are provided in "version.properties" file.
     * If user defined other properties like "poweredBy"
     * or "companyName" this method will also show their values.
     * User can disable that method in 2-ways. First one is to change
     * the "show" parameter in "version.properties" but it will also hide
     * the header decorators. But user can also provide null as argument.
     * @param log - Logger from "MangoContextListener" to display that data during initialization.
     */
    public void printScadaVersionProperties(Log log) {
        if(log != null && showVersionInfo) {
            log.info("\n**************************************************\n" +
                    "*             Welcome to Scada-LTS!              *\n" +
                    "**************************************************\n" +
                    "* Version: " + versionNumber + "\n" +
                    "* Build: " + buildNumber + "\n" +
                    (!poweredBy.isBlank() ? ("* Powered by: " + poweredBy + "\n") : "") +
                    (!companyName.isBlank() ? ("* For: " + companyName + "\n") : ""));
        }
    }

    private static String getScadaVersionFilePath() {
        String fileSeparator = System.getProperty("file.separator");
        String path = Common.ctx.getServletContext().getRealPath("");
        return path +
                fileSeparator + "WEB-INF" +
                fileSeparator + "classes" +
                fileSeparator + FILENAME;
    }

    public static ScadaVersion getInstance() {
        if(instance == null) {
            instance = new ScadaVersion();
        }
        return instance;
    }

    /* ** Those methods are used in the UserInterface inside 'logo.tag' file ** */

    public boolean isShowVersionInfo() {
        return showVersionInfo;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public String getCommitNumber() {
        return commitNumber;
    }

    public String getRunningOs() {
        return runningOs;
    }

    public String getRunningJava() {
        return runningJava;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getPoweredBy() {
        return poweredBy;
    }
    public String getTopDescription() {
        return SystemSettingsDAO.getValue(TOP_DESCRIPTION);
    }
    public String getTopDescriptionPrefix(){
        return SystemSettingsDAO.getValue(TOP_DESCRIPTION_PREFIX);
    }
}
