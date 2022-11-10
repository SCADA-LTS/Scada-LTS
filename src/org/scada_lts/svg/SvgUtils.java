package org.scada_lts.svg;

import org.scada_lts.utils.security.SafeFile;
import org.scada_lts.utils.security.SafeMultipartFile;
import org.scada_lts.utils.security.SafeZipEntry;
import org.scada_lts.utils.security.SafeZipFile;


import static org.scada_lts.svg.SvgProcessingUtils.isValidatorEnabled;
import static org.scada_lts.utils.xml.XmlUtils.createDocumentXml;

public final class SvgUtils {

    private SvgUtils() {}
    
    public static boolean isSvg(SafeFile svg) {
        if(!isValidatorEnabled())
            return true;
        return createDocumentXml(svg)
                .filter(SvgProcessingUtils::isSvg)
                .isPresent();
    }

    public static boolean isSvg(SafeMultipartFile svg) {
        if(!isValidatorEnabled())
            return true;
        return createDocumentXml(svg)
                .filter(SvgProcessingUtils::isSvg)
                .isPresent();
    }

    public static boolean isSvg(SafeZipFile zipFile, SafeZipEntry entry) {
        if(!isValidatorEnabled())
            return true;
        return createDocumentXml(zipFile, entry)
                .filter(SvgProcessingUtils::isSvg)
                .isPresent();
    }
}