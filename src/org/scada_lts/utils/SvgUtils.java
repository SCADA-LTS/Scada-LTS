package org.scada_lts.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.svg.SvgSchema;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class SvgUtils {

    private SvgUtils() {}

    private static final Log LOG = LogFactory.getLog(SvgUtils.class);

    public static boolean isSvg(File svg) {
        return createDocumentXml(svg)
                .map(doc -> SvgSchema.SVG_1_0_20010904.isSvg(doc)
                        || SvgSchema.SVG_1_1_20110816.isSvg(doc))
                .orElse(false);
    }

    public static boolean isSvg(MultipartFile svg) {
        return createDocumentXml(svg)
                .map(doc -> SvgSchema.SVG_1_0_20010904.isSvg(doc)
                        || SvgSchema.SVG_1_1_20110816.isSvg(doc))
                .orElse(false);
    }

    public static boolean isSvg(ZipFile zipFile, ZipEntry entry) {
        return createDocumentXml(zipFile, entry)
                .map(doc -> SvgSchema.SVG_1_0_20010904.isSvg(doc)
                        || SvgSchema.SVG_1_1_20110816.isSvg(doc))
                .orElse(false);
    }

    public static boolean isXml(ZipFile zipFile, ZipEntry entry) {
        return createDocumentXml(zipFile, entry).stream()
                .map(a -> true)
                .findAny()
                .orElse(false);
    }

    public static boolean isXml(File file) {
        return createDocumentXml(file).stream()
                .map(a -> true)
                .findAny()
                .orElse(false);
    }


    private static Optional<Document> createDocumentXml(ZipFile zipFile, ZipEntry entry) {
        try(InputStream inputStream = zipFile.getInputStream(entry)) {
            return Optional.of(parseXml(inputStream));
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    private static Optional<Document> createDocumentXml(MultipartFile multipartFile) {
        try(InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes())) {
            return Optional.of(parseXml(inputStream));
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    private static Optional<Document> createDocumentXml(File svg) {
        try(InputStream inputStream = new FileInputStream(svg)) {
            return Optional.of(parseXml(inputStream));
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static Document parseXml(InputStream inputStream) throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        dbFactory.setNamespaceAware(true);
        dbFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        dBuilder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });
        return dBuilder.parse(inputStream);
    }
}
