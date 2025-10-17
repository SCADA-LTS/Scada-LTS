package org.scada_lts.utils.xml;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import static org.junit.Assert.*;

public class XmlValidatorConfigSecurityTest {

    private Validator subject;

    @Before
    public void config() throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newDefaultInstance();
        Schema schema = schemaFactory.newSchema();
        subject = XmlUtils.newValidator(schema, msg -> true);
    }

    @Test
    public void when_getFeature_validation_then_true() throws SAXException {
        //when:
        boolean supprot = subject.getFeature("http://xml.org/sax/features/validation");

        //then:
        assertEquals(true, supprot);
    }

    @Test
    public void when_getFeature_disallow_doctype_decl_then_false() throws SAXException {
        //when:
        boolean result = subject.getFeature("http://apache.org/xml/features/disallow-doctype-decl");

        //then:
        assertEquals(false, result);
    }

    @Test
    public void when_getFeature_external_general_entities_then_false() throws SAXException {
        //when:
        boolean result = subject.getFeature("http://xml.org/sax/features/external-general-entities");

        //then:
        assertEquals(false, result);
    }

    @Test
    public void when_getFeature_external_parameter_entities_then_false() throws SAXException {
        //when:
        boolean result = subject.getFeature("http://xml.org/sax/features/external-parameter-entities");

        //then:
        assertEquals(false, result);
    }

    @Test
    public void when_getFeature_secure_processing_then_false() throws SAXException {
        //when:
        boolean result = subject.getFeature("http://javax.xml.XMLConstants/feature/secure-processing");

        //then:
        assertEquals(true, result);
    }
}