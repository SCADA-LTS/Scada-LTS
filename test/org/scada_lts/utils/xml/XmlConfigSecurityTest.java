package org.scada_lts.utils.xml;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.junit.Assert.*;

public class XmlConfigSecurityTest {

    private DocumentBuilderFactory subject;

    @Before
    public void config() throws SAXException, ParserConfigurationException {
        subject = XmlUtils.newDocumentBuilderFactory();
    }

    //Validating by XML Schema Definition (xsd)
    @Test
    public void when_getFeature_validation_then_false() throws ParserConfigurationException {
        //when:
        boolean result = subject.getFeature("http://xml.org/sax/features/validation");

        //then:
        assertEquals(false, result);
    }

    @Test
    public void when_isValidating_then_false() {
        //when:
        boolean result = subject.isValidating();

        //then:
        assertEquals(false, result);
    }


    @Test
    public void when_getFeature_load_external_dtd_then_false() throws ParserConfigurationException {
        //when:
        boolean result = subject.getFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd");

        //then:
        assertEquals(false, result);
    }

    @Test
    public void when_getFeature_namespaces_then_false() throws ParserConfigurationException {
        //when:
        boolean result = subject.getFeature("http://xml.org/sax/features/namespaces");

        //then:
        assertEquals(true, result);
    }

    @Test
    public void when_getFeature_disallow_doctype_decl_then_false() throws ParserConfigurationException {
        //when:
        boolean result = subject.getFeature("http://apache.org/xml/features/disallow-doctype-decl");

        //then:
        assertEquals(true, result);
    }

    @Test
    public void when_getFeature_external_general_entities_then_false() throws ParserConfigurationException {
        //when:
        boolean result = subject.getFeature("http://xml.org/sax/features/external-general-entities");

        //then:
        assertEquals(false, result);
    }

    @Test
    public void when_getFeature_external_parameter_entities_then_false() throws ParserConfigurationException {
        //when:
        boolean result = subject.getFeature("http://xml.org/sax/features/external-parameter-entities");

        //then:
        assertEquals(false, result);
    }

    @Test
    public void when_getFeature_secure_processing_then_false() throws ParserConfigurationException {
        //when:
        boolean result = subject.getFeature("http://javax.xml.XMLConstants/feature/secure-processing");

        //then:
        assertEquals(true, result);
    }

    @Test
    public void when_isCoalescing_then_false() {
        //when:
        boolean result = subject.isCoalescing();

        //then:
        assertEquals(false, result);
    }

    @Test
    public void when_isExpandEntityReferences_then_false() {
        //when:
        boolean result = subject.isExpandEntityReferences();

        //then:
        assertEquals(false, result);
    }

    @Test
    public void when_isIgnoringComments_then_false() {
        //when:
        boolean result = subject.isIgnoringComments();

        //then:
        assertEquals(false, result);
    }

    @Test
    public void when_isNamespaceAware_then_true() {
        //when:
        boolean result = subject.isNamespaceAware();

        //then:
        assertEquals(true, result);
    }

    @Test
    public void when_isIgnoringElementContentWhitespace_then_true() {
        //when:
        boolean result = subject.isIgnoringElementContentWhitespace();

        //then:
        assertEquals(true, result);
    }

    @Test
    public void when_isXIncludeAware_then_false() {
        //when:
        boolean result = subject.isXIncludeAware();

        //then:
        assertEquals(false, result);
    }

    @Test
    public void when_getAttribute_accessExternalDTD_then_empty() {
        Object result = subject.getAttribute(XMLConstants.ACCESS_EXTERNAL_DTD);

        //then:
        assertEquals("", result);
    }

    @Test
    public void when_getAttribute_accessExternalSchema_then_empty() {
        Object result = subject.getAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA);

        //then:
        assertEquals("", result);
    }
}