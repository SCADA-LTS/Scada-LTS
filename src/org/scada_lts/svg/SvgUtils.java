package org.scada_lts.svg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.scada_lts.svg.SvgProcessingUtils.isValidatorEnabled;
import static org.scada_lts.utils.xml.XmlUtils.createDocumentXml;

public final class SvgUtils {

    private static final Log LOG = LogFactory.getLog(SvgUtils.class);

    private SvgUtils() {}
    
    public static boolean isSvg(File svg) {
        if(!isValidatorEnabled())
            return true;
        return createDocumentXml(svg)
                .filter(SvgProcessingUtils::isSvg)
                .isPresent();
    }

    public static boolean isSvg(MultipartFile svg) {
        if(!isValidatorEnabled())
            return true;
        return createDocumentXml(svg)
                .filter(SvgProcessingUtils::isSvg)
                .isPresent();
    }

    public static boolean isSvg(ZipFile zipFile, ZipEntry entry) {
        if(!isValidatorEnabled())
            return true;
        return createDocumentXml(zipFile, entry)
                .filter(SvgProcessingUtils::isSvg)
                .isPresent();
    }
}