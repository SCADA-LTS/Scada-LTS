package org.scada_lts.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class SvgUtils {

    private SvgUtils() {}

    private static final Log LOG = LogFactory.getLog(SvgUtils.class);

    private static final String WEB_CONTENT_SVG_PATH = "WebContent" + File.separator + "svg" + File.separator;
    private static final File SVG_1_1_20110816 = new File(WEB_CONTENT_SVG_PATH + "svg11-flat-20110816" + File.separator + "svg11-flat-20110816.xsd");
    private static final File SVG_1_1_20030114 = new File(WEB_CONTENT_SVG_PATH + "svg11-flat-20030114" + File.separator + "svg11-flat-20030114.xsd");
    private static final File SVG_1_1_TINY = new File(WEB_CONTENT_SVG_PATH + "svg11-tiny-flat" + File.separator + "svg11-tiny-flat.xsd");

    /**
     * Incompatible with SVG 1.1 - often found in files;
     * rdf:RDF - https://www.w3.org/TR/SVGTiny12/metadata.html
     */
    private static final String[] MSG_IGNORE = {
            "Attribute 'sodipodi:docname' is not allowed to appear in element 'svg'",
            "Attribute 'inkscape:version' is not allowed to appear in element 'svg'",
            "Invalid content was found starting with element '{\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\":namedview}'",
            "Attribute 'inkscape:label' is not allowed to appear in element 'g'.",
            "Attribute 'inkscape:groupmode' is not allowed to appear in element 'g'.",
            "must appear on element",
            "Invalid content was found starting with element 'rdf:RDF'. No child element is expected at this point."
    };

    public static boolean isSvg(File svg) {
        if(!isXml(svg))
            return false;
        if(isSvg(svg, SVG_1_1_20110816))
            return true;
        if(isSvg(svg, SVG_1_1_20030114))
            return true;
        return isSvg(svg, SVG_1_1_TINY);
    }

    public static boolean isSvg(MultipartFile svg) {
        if(!isXml(svg))
            return false;
        if(isSvg(svg, SVG_1_1_20110816))
            return true;
        if(isSvg(svg, SVG_1_1_20030114))
            return true;
        return isSvg(svg, SVG_1_1_TINY);
    }

    public static boolean isSvg(ZipFile zipFile, ZipEntry entry) {
        if(!isXml(zipFile, entry))
            return false;
        if(isSvg(zipFile, entry, SVG_1_1_20110816))
            return true;
        if(isSvg(zipFile, entry, SVG_1_1_20030114))
            return true;
        return isSvg(zipFile, entry, SVG_1_1_TINY);
    }

    public static boolean isXml(File svg) {
        try(InputStream inputStream = new FileInputStream(svg)) {
            return isXml(inputStream);
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return false;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return false;
        }
    }

    public static boolean isXml(MultipartFile multipartFile) {
        try(InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes())) {
            return isXml(inputStream);
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return false;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return false;
        }
    }

    public static boolean isXml(ZipFile zipFile, ZipEntry entry) {
        try(InputStream inputStream = zipFile.getInputStream(entry)) {
            return isXml(inputStream);
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return false;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return false;
        }
    }

    private static boolean isSvg(File svg, File xsd) {
        try(InputStream inputStream = new FileInputStream(svg)) {
            return isSvg(inputStream, xsd);
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return false;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return false;
        }
    }

    private static boolean isSvg(MultipartFile multipartFile, File xsd) {
        try(InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes())) {
            return isSvg(inputStream, xsd);
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return false;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return false;
        }
    }

    private static boolean isSvg(ZipFile zipFile, ZipEntry entry, File xsd) {
        try(InputStream inputStream = zipFile.getInputStream(entry)) {
            return isSvg(inputStream, xsd);
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return false;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return false;
        }
    }

    private static boolean isSvg(InputStream inputStream, File xsdSchema) {
        try {
            Document doc = parseBySchema(inputStream, xsdSchema);
            return doc != null;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LOG.error(ex.getMessage());
            return false;
        }
    }

    private static boolean isXml(InputStream inputStream) {
        try {
            Document doc = parseXml(inputStream);
            return doc != null;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LOG.error(ex.getMessage());
            return false;
        }
    }

    private static Document parseBySchema(InputStream inputStream, File xsdSchema) throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        dbFactory.setIgnoringElementContentWhitespace(true);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(xsdSchema);
        dbFactory.setSchema(schema);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        dBuilder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw new SAXException(exception);
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                if(isIgnore(exception)) {
                    LOG.warn(exception.getMessage());
                    return;
                }
                throw new SAXException(exception);
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw new SAXException(exception);
            }
        });
        return dBuilder.parse(inputStream);
    }

    private static boolean isIgnore(SAXParseException exception) {
        return exception.getMessage() != null && isIgnore(exception.getMessage());
    }

    private static Document parseXml(InputStream inputStream) throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        dbFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        dBuilder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw new SAXException(exception);
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw new SAXException(exception);
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw new SAXException(exception);
            }
        });
        return dBuilder.parse(inputStream);
    }

    private static boolean isIgnore(String msg) {
        for(String ignore: MSG_IGNORE) {
            if(msg.contains(ignore))
                return true;
        }
        return false;
    }
}
