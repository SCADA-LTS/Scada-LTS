package com.serotonin.mango.rt.dataSource.snmp;


import com.serotonin.mango.vo.dataSource.snmp.SnmpDataSourceVO;
import org.apache.log4j.helpers.LogLog;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.logging.LogManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class SnmpDataSourceRTTest {

    private final String  ADDRESS = "127.0.0.1";

    static SnmpAgent agent;
    @BeforeClass
    public static void setup() throws Exception {
        LogManager.getLogManager().reset();
      //  agent = new SnmpAgent();
      //  agent.start();
    }

    @AfterClass
    public static void tearDown() {
      //  agent.stop();
    }

/*    @Test
    public void testGet() throws Exception {
        String actual = SnmpClient.get("127.0.0.1", 161, ".1.3.6.1.2.1.1.1.0");
        assertEquals("13", actual);
    }*/

  @Test
    public void should_have_index() {
       // LogLog.setQuietMode(true);
        SnmpDataSourceVO snmpDTSVO = new SnmpDataSourceVO();
        snmpDTSVO.setHost(ADDRESS);
        snmpDTSVO.setPort(3333);
        snmpDTSVO.setContextName("public");
        snmpDTSVO.setRetries(2);
        snmpDTSVO.setCommunity("public");
        snmpDTSVO.setSnmpVersion(1);

 /*     SnmpDataSourceRT calcService = mock(SnmpDataSourceRT.class);
      calcService.initialize();
      calcService.doPoll(500000);*/
      SnmpDataSourceRT snmpDSRT = new SnmpDataSourceRT(snmpDTSVO);
           // SnmpAgent snmpAgent = new SnmpAgent();

        //snmpDTSVO.setPort("");
        //SnmpDataSourceRT snmpDSRT = new SnmpDataSourceRT(snmpDTSVO);
       /* OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(jetty.getUrl())
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
   /*     assertEquals(200,200);*/
    }

}

