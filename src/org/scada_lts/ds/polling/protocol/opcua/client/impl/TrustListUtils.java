package org.scada_lts.ds.polling.protocol.opcua.client.impl;

import com.serotonin.mango.util.LoggingUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TrustListUtils {

    private static final Logger LOG = LogManager.getLogger(TrustListUtils.class);

    public static File createDir() {
        try {
            return createDirInSecurity();
        } catch (Throwable e) {
            LOG.error(LoggingUtils.exceptionInfo(e));
            return getDirBin();
        }
    }

    private static File createDirInSecurity() throws IOException {
        String catalinaHome = System.getProperty("catalina.home");
        File file = new File(catalinaHome + File.separator + "security" + File.separator + "certs");
        if(Files.notExists(file.toPath()))
            file.mkdirs();
        if(Files.exists(file.toPath()) && !Files.notExists(file.toPath())) {
            return file;
        } else {
            throw new IOException("File access denied: " + file);
        }
    }

    private static File getDirBin() {
        String catalinaHome = System.getProperty("catalina.home");
        return new File(catalinaHome + File.separator + "bin");
    }
}
