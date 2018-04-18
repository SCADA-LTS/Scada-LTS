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

        XmlUtils seroXML = new XmlUtils();
        org.scada_lts.serorepl.utils.XmlUtils scadaXML = new org.scada_lts.serorepl.utils.XmlUtils();

        Document document = seroXML.parse(new File(pathName));
        Document document1 = scadaXML.parse(new File(pathName));

        Element root = document.getDocumentElement();
        Element root1 = document1.getDocumentElement();

        int seroLength = seroXML.getElementsByTagName(root,"item").toArray().length;
        int scadaLength = scadaXML.getElementsByTagName(root1,"item").toArray().length;

     //   System.out.println("sero length " + seroLength + " scada length " + scadaLength);

        assertEquals(seroLength ,  scadaLength );


    }

}