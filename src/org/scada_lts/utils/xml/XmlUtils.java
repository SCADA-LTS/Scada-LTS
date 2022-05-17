package org.scada_lts.utils.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.*;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class XmlUtils {

    private static final Log LOG = LogFactory.getLog(XmlUtils.class);

    private XmlUtils() {}

    public static Validator newRestrictionValidator(Schema schema) throws SAXNotSupportedException, SAXNotRecognizedException {
        return newValidator(schema, a -> false, a -> false);
    }

    public static Validator newValidator(Schema schema, Predicate<String> isIgnoreErrorMsg) throws SAXNotSupportedException, SAXNotRecognizedException {
        return newValidator(schema, isIgnoreErrorMsg, a -> true);
    }

    public static Validator newValidator(Schema schema,
                                         Predicate<String> isIgnoreErrorMsg,
                                         Predicate<String> isIgnoreWarnMsg) throws SAXNotSupportedException, SAXNotRecognizedException {
        Validator validator = schema.newValidator();
        validator.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        validator.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        validator.setFeature("http://xml.org/sax/features/external-general-entities", false);
        validator.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        validator.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                if(isIgnoreWarnMsg.test(exception.getMessage())) {
                    LOG.trace(exception.getMessage());
                    return;
                }
                throw exception;
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                if(isIgnoreErrorMsg.test(exception.getMessage())) {
                    LOG.trace(exception.getMessage());
                    return;
                }
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });
        return validator;
    }

    public static DocumentBuilderFactory newDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setExpandEntityReferences(false);
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        return documentBuilderFactory;
    }

    public static boolean isXml(ZipFile zipFile, ZipEntry entry) {
        return createDocumentXml(zipFile, entry)
                .isPresent();
    }

    public static boolean isXml(File file) {
        return createDocumentXml(file)
                .isPresent();
    }

    public static boolean isXml(MultipartFile file) {
        return createDocumentXml(file)
                .isPresent();
    }

    public static Optional<Document> createDocumentXml(ZipFile zipFile, ZipEntry entry) {
        try(InputStream inputStream = zipFile.getInputStream(entry)) {
            return Optional.of(parseXml(inputStream));
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<Document> createDocumentXml(MultipartFile multipartFile) {
        try(InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes())) {
            return Optional.of(parseXml(inputStream));
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<Document> createDocumentXml(File svg) {
        try(InputStream inputStream = new FileInputStream(svg)) {
            return Optional.of(parseXml(inputStream));
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            return Optional.empty();
        }
    }

    private static Document parseXml(InputStream inputStream) throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory dbFactory = newDocumentBuilderFactory();
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
