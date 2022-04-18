package org.scada_lts.svg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

public enum SvgSchema {

    SVG_1_0_20010904("svg10-flat-20010904" + File.separator + "svg10-flat-20010904.xsd", W3C_XML_SCHEMA_NS_URI),
    SVG_1_1_20030114("svg11-flat-20030114" + File.separator + "svg11-flat-20030114.xsd", W3C_XML_SCHEMA_NS_URI),
    SVG_1_1_20110816("svg11-flat-20110816" + File.separator + "svg11-flat-20110816.xsd", W3C_XML_SCHEMA_NS_URI),
    SVG_1_1_TINY("svg11-tiny-flat" + File.separator + "svg11-tiny-flat.xsd", W3C_XML_SCHEMA_NS_URI),
    SVG_1_1_BASIC("svg11-basic-flat" + File.separator + "svg11-basic-flat.xsd", W3C_XML_SCHEMA_NS_URI);

    private static final String IS_NOT_ALLOWED_TO_APPEAR_IN = "is not allowed to appear in element '";
    private static final String MUST_APPEAR_ON_ELEMENT = "must appear on element";
    private static final String SODIPODI_ATTR = "Attribute 'sodipodi:";
    private static final String INKSCAPE_ATTR = "Attribute 'inkscape:";

    /**
     * Incompatible with SVG 1.0/1.1/1.1 Second Edition/1.1 Tiny/1.1 Basic - often found in files;
     * rdf:RDF - https://www.w3.org/TR/SVGTiny12/metadata.html
     */
    private static final List<Predicate<String>> EXCLUDE_MSGS = Arrays.asList(
            msg -> msg.contains(SODIPODI_ATTR) && msg.contains(IS_NOT_ALLOWED_TO_APPEAR_IN),
            msg -> msg.contains(INKSCAPE_ATTR) && msg.contains(IS_NOT_ALLOWED_TO_APPEAR_IN),
            msg -> msg.contains(MUST_APPEAR_ON_ELEMENT),
            msg -> msg.contains("Attribute 'blend' is not allowed to appear in element 'feBlend'.")
    );

    private static final String[] IGNORE_TAGS = {"rdf:RDF", "sodipodi:namedview"};

    private static final Log LOG = LogFactory.getLog(SvgSchema.class);

    private final File schemaFile;
    private final String schemaLanguage;

    SvgSchema(String filePath, String schemaLanguage) {
        this.schemaFile = new File("WebContent" + File.separator + "svg" + File.separator + filePath);
        this.schemaLanguage = schemaLanguage;
    }

    public boolean isSvg(Document document) {
        try {
            validate(document);
            return true;
        } catch (SAXException ex) {
            LOG.warn(ex.getMessage());
            return false;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }

    private void validate(Document document) throws SAXException, IOException {
        Node copy = document.cloneNode(true);
        removeIgnoreTags(copy, 20);
        validate(copy);
    }

    private void validate(Node copy) throws SAXException, IOException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(schemaLanguage);
        Schema schema = schemaFactory.newSchema(schemaFile);
        Validator validator = schema.newValidator();
        validator.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                LOG.warn(exception.getMessage());
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                if(isIgnore(exception.getMessage())) {
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

        validator.validate(new DOMSource(copy));
    }

    private static void removeIgnoreTags(Node parent, int deepMax) {
        if(deepMax < 0)
            return;
        NodeList children = parent.getChildNodes();
        int end = children.getLength();
        List<Node> toRemove = new ArrayList<>();
        for (int i = 0; i < end; i++) {
            Node child = children.item(i);
            if (isIgnoreTag(child.getNodeName())) {
                toRemove.add(child);
            } else if (child.hasChildNodes()) {
                removeIgnoreTags(child, --deepMax);
            }
        }
        toRemove.forEach(parent::removeChild);
    }

    private static boolean isIgnoreTag(String tag) {
        for (String ignore: IGNORE_TAGS) {
            if(ignore.equalsIgnoreCase(tag))
                return true;
        }
        return false;
    }

    private static boolean isIgnore(String msg) {
        if(msg == null)
            return false;
        for(Predicate<String> ignore: EXCLUDE_MSGS) {
            if(ignore.test(msg))
                return true;
        }
        return false;
    }
}