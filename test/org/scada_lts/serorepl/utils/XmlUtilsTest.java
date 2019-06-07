package org.scada_lts.serorepl.utils;

import com.serotonin.util.XmlUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class XmlUtilsTest {


    /**
     * This test test all method implemented in XmlUtils
     * @throws Exception
     */
    @Test
    public void getElementsByTagName() throws Exception {

        String pathName = "WebContent/WEB-INF/dox/manifest.xml";

        XmlUtils oldXml = new XmlUtils();
        org.scada_lts.serorepl.utils.XmlUtils newXml = new org.scada_lts.serorepl.utils.XmlUtils();

        Document document = oldXml.parse(new File(pathName));
        Document document1 = newXml.parse(new File(pathName));

        Element root = document.getDocumentElement();
        Element root1 = document1.getDocumentElement();

        int oldLength = oldXml.getElementsByTagName(root,"item").toArray().length;
        int newLength = newXml.getElementsByTagName(root1,"item").toArray().length;

        System.out.println(pathName);
        System.out.println("old :" + oldLength + " new :" + newLength);
        assertEquals(oldLength ,  newLength );


    }

    @Test
    public void getElementsByTagName1() throws Exception {

        String pathName = "WebContent/WEB-INF/applicationContext.xml";

        XmlUtils oldXml = new XmlUtils();
        org.scada_lts.serorepl.utils.XmlUtils newXml = new org.scada_lts.serorepl.utils.XmlUtils();

        Document document = oldXml.parse(new File(pathName));
        Document document1 = newXml.parse(new File(pathName));

        Element root = document.getDocumentElement();
        Element root1 = document1.getDocumentElement();

        int oldLength = oldXml.getElementsByTagName(root,"beans").toArray().length;
        int newLength = newXml.getElementsByTagName(root1,"beans").toArray().length;

        System.out.println(pathName);
        System.out.println("old :" + oldLength + " new :" + newLength);
        assertEquals(oldLength ,  newLength );


    }

}