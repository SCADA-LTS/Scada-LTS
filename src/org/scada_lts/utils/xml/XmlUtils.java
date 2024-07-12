package org.scada_lts.utils.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.svg.SvgEnv;
import org.scada_lts.utils.security.SafeFile;
import org.scada_lts.utils.security.SafeMultipartFile;
import org.scada_lts.utils.security.SafeZipEntry;
import org.scada_lts.utils.security.SafeZipFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.*;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public final class XmlUtils {

    private static final Log LOG = LogFactory.getLog(XmlUtils.class);

    private XmlUtils() {}

    public static Validator newRestrictionValidator(Schema schema) throws SAXNotSupportedException, SAXNotRecognizedException {
        return newValidator(schema, a -> false, a -> false, true);
    }

    public static Validator newValidator(Schema schema, Predicate<String> isIgnoreErrorMsg) throws SAXNotSupportedException, SAXNotRecognizedException {
        return newValidator(schema, isIgnoreErrorMsg, a -> true);
    }

    public static Validator newValidator(Schema schema,
                                         Predicate<String> isIgnoreErrorMsg,
                                         Predicate<String> isIgnoreWarnMsg) throws SAXNotSupportedException, SAXNotRecognizedException {
        return newValidator(schema, isIgnoreErrorMsg, isIgnoreWarnMsg, SvgEnv.isDisallowDoctypeDeclarationEnabled());
    }

    public static Validator newValidator(Schema schema,
                                         Predicate<String> isIgnoreErrorMsg,
                                         Predicate<String> isIgnoreWarnMsg,
                                         boolean disallowDoctypeDeclarationEnabled) throws SAXNotSupportedException, SAXNotRecognizedException {
        Validator validator = schema.newValidator();
        validator.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        validator.setFeature("http://apache.org/xml/features/disallow-doctype-decl", disallowDoctypeDeclarationEnabled);
        validator.setFeature("http://xml.org/sax/features/external-general-entities", false);
        validator.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        validator.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                if(isIgnoreWarnMsg.test(exception.getMessage())) {
                    LOG.warn(exception.getMessage());
                    return;
                }
                throw exception;
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                if(isIgnoreErrorMsg.test(exception.getMessage())) {
                    LOG.warn(exception.getMessage());
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
        return newDocumentBuilderFactory(SvgEnv.isDisallowDoctypeDeclarationEnabled());
    }

    public static DocumentBuilderFactory newDocumentBuilderFactory(boolean disallowDoctypeDeclarationEnabled) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", disallowDoctypeDeclarationEnabled);
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

    public static boolean isXml(SafeZipFile zipFile, SafeZipEntry entry) {
        return createDocumentXml(zipFile, entry)
                .isPresent();
    }

    public static boolean isXml(SafeFile file) {
        return createDocumentXml(file)
                .isPresent();
    }

    public static boolean isXml(SafeMultipartFile file) {
        return createDocumentXml(file)
                .isPresent();
    }

    public static Optional<Document> createDocumentXml(SafeZipFile zipFile, SafeZipEntry entry) {
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

    public static Optional<Document> createDocumentXml(SafeMultipartFile multipartFile) {
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

    public static Optional<Document> createDocumentXml(SafeFile svg) {
        try(InputStream inputStream = new FileInputStream(svg.toFile())) {
            return Optional.of(parseXml(inputStream));
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            return Optional.empty();
        }
    }

    public static Node getFirstNodeByTagName(Document documente, String tagName) {
        return getNodeByTagName(documente, tagName, 0);
    }

    public static Node getNodeByTagName(Document document, String tagName, int index) {
        NodeList element = document.getElementsByTagName(tagName);
        return element.item(index);
    }

    public static int getIntValue(Node node, String attrName) {
        return getNodeValue(node, attrName, value -> {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
                LOG.warn(e.getMessage());
                return -1;
            }
        });
    }

    public static <T> T getNodeValue(Node node, String attrName, Function<String, T> converter) {
        Node valueNode = node.getAttributes().getNamedItem(attrName);
        if(valueNode != null) {
            return converter.apply(valueNode.getNodeValue());
        } else {
            return converter.apply("");
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
