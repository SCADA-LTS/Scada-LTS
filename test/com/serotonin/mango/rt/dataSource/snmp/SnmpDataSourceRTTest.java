package com.serotonin.mango.rt.dataSource.snmp;


import com.serotonin.mango.vo.dataSource.snmp.SnmpDataSourceVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SnmpDataSourceRTTest {

    private SnmpDataSourceVO snmpDataSourceVO;
    private SnmpDataSourceRT snmpDataSourceRT;
    private int retries=2;

    @Before
    public void init() {
        snmpDataSourceVO= new SnmpDataSourceVO();
        snmpDataSourceVO.setSnmpVersion(1);
        snmpDataSourceVO.setRetries(retries);
        snmpDataSourceVO.setHost("127.0.0.1");
        snmpDataSourceVO.setPort(1024);
        snmpDataSourceVO.setTimeout(1000);
        snmpDataSourceVO.setLocalAddress("127.0.0.1");
        snmpDataSourceVO.setCommunity("public");
        snmpDataSourceRT = new SnmpDataSourceRT(snmpDataSourceVO);

    }

    @After
    public void finalized() {
        snmpDataSourceVO = null;
        snmpDataSourceRT = null;
    }

    @Test
    public void doesTheConditionOfRetriesWorkWhenConnectionIsNotAliveTest() {
        int counter=0;
        snmpDataSourceRT.createSnmpAndStartListening();
        while(snmpDataSourceRT.isSnmpConnectionIsAlive()) {
            System.out.println((++counter)+" one of "+snmpDataSourceVO.getRetries()+ " we try get response from snmp.");
            snmpDataSourceRT.doPoll(-1);
        }
    }
}
