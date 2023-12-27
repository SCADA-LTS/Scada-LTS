package org.scada_lts.svg;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static org.scada_lts.svg.SvgEnvKeys.*;
import static org.scada_lts.utils.PathSecureUtils.getRealPath;
import static org.scada_lts.utils.PathSecureUtils.toSecurePath;
import static org.scada_lts.utils.xml.XmlUtils.newValidator;

final class SvgProcessingUtils {

    private SvgProcessingUtils() {}

    private static final Log LOG = LogFactory.getLog(SvgProcessingUtils.class);

    public static boolean isValidatorEnabled() {
        return SvgEnvKeys.isEnabled();
    }

    public static boolean isSvg(Document document) {
        if(isOnlyXml())
            return true;
        List<File> schemas = getSchemas();
        if(schemas.isEmpty())
            return false;
        for(File schema: schemas) {
            if(isSvg(document, schema, getSchemaLanguage(schema))) {
                return true;
            }
        }
        return false;
    }

    private static List<File> getSchemas() {
        return Stream.of(getSchemaPaths())
                .map(Paths::get)
                .filter(SvgProcessingUtils::isXsdFile)
                .flatMap(filepath ->
                        toSecurePath(Paths.get(getRealPath(File.separator) + File.separator + filepath))
                                .stream())
                .collect(Collectors.toList());
    }

    private static boolean isSvg(Document document, File schemaFile, String schemaLanguage) {
        try {
            Node clonedDocument = document.cloneNode(true);
            removeIgnoreTags(clonedDocument, 20);
            validate(clonedDocument, schemaFile, schemaLanguage);
            return true;
        } catch (SAXException ex) {
            LOG.warn(ex.getMessage());
            return false;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }

    private static void validate(Node node, File schemaFile, String schemaLanguage) throws SAXException, IOException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(schemaLanguage);
        Schema schema = schemaFactory.newSchema(schemaFile);
        Validator validator = newValidator(schema, SvgProcessingUtils::isIgnoreMsg);
        validator.validate(new DOMSource(node));
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
        for (String ignore : getIgnoreTags()) {
            if (ignore.equalsIgnoreCase(tag))
                return true;
        }
        return false;
    }

    private static boolean isIgnoreMsg(String msg) {
        if(msg == null)
            return false;
        List<Predicate<String>> filtering = Stream.of(getIgnoreMsgs())
                .map(a -> (Predicate<String>) s -> s.contains(a)).collect(Collectors.toList());
        for(Predicate<String> ignore: filtering) {
            if(ignore.test(msg))
                return true;
        }
        return false;
    }

    private static String getSchemaLanguage(File schemaFile) {
        return isXsdFile(schemaFile.toPath()) ? W3C_XML_SCHEMA_NS_URI : "";
    }

    public static boolean isXsdFile(Path path) {
        return FilenameUtils.isExtension(path.toFile().getName(), "xsd");
    }
}
