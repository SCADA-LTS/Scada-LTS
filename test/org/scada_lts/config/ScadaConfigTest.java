/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.scada_lts.config;


import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test config Scada-LTS
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu,
 * kamil.jarmusik@gmail.com
 */

public class ScadaConfigTest {

    private static ScadaConfig config;

    @BeforeClass
    public static void init() throws Exception {
        String path = MessageFormat.format("WebContent{0}test{0}env-test.properties", File.separator);
        config = ScadaConfig.getConfigOnlyTest(new File(path));
    }

    @Test
    public void invoke_size_then_return_28() {
        //when:
        int result = config.size();
        //then:
        assertEquals(28, result);
    }

    @Test
    public void invoke_getInt_for_key_ThreadPoolConfigKeys_corePoolSize_return_3() {
        //when:
        int result = config
                .getInt(ThreadPoolConfigKeys.CORE_POOL_SIZE, -1);

        //then:
        assertEquals(3, result);
    }

    @Test
    public void invoke_getInt_for_key_ThreadPoolConfigKeys_maximumPoolSize_return_100() {
        //when:
        int result = config
                .getInt(ThreadPoolConfigKeys.MAXIMUM_POOL_SIZE, -1);

        //then:
        assertEquals(100, result);
    }

    @Test
    public void invoke_getLong_for_key_ThreadPoolConfigKeys_keepAliveTime_return_60() {
        //when:
        long reslt = config
                .getLong(ThreadPoolConfigKeys.KEEP_ALIVE_TIME,-1);
        //then:
        assertEquals(60, reslt);
    }

    @Test
    public void invoke_getString_for_key_ThreadPoolConfigKeys_blockingQueueInterfaceImpl_return_LinkedBlockingQueue() {
        //when:
        String result = config
                .getString(ThreadPoolConfigKeys.BLOCKING_QUEUE_INTERFACE_IMPL, "");
        //then:
        assertEquals("java.util.concurrent.LinkedBlockingQueue", result);
    }

    @Test
    public void invoke_getString_for_key_ThreadPoolConfigKeys_timeUnitEnumValue_return_SECONDS() {
        //when:
        String result = config
                .getString(ThreadPoolConfigKeys.TIME_UNIT_ENUM_VALUE, "");
        //then:
        assertEquals("SECONDS", result);
    }

    @Test
    public void invoke_getString_for_key_SystemConfigKeys_aclServer_return_http_localhost_8090() {
        //when:
        String result = config
                .getString(SystemConfigKeys.ACL_SERVER, "");
        //then:
        assertEquals("http://localhost:8090", result);
    }

    @Test
    public void invoke_getString_for_key_SystemConfigKeys_croneUpdateCacheDataSourcesPoints_return_0_0_1() {
        //when:
        String result = config
                .getString(SystemConfigKeys.CRONE_UPDATE_CACHE_DATA_SOURCES_POINTS, "");
        //then:
        assertEquals("0 0/1 * * * ?", result);
    }

    @Test
    public void invoke_getString_for_key_SystemConfigKeys_croneUpdateCachePointHierarchy_return_0_0_10() {
        //when:
        String result = config
                .getString(SystemConfigKeys.CRONE_UPDATE_CACHE_POINT_HIERARCHY, "");
        //then:
        assertEquals("0 0/10 * * * ?", result);
    }

    @Test
    public void invoke_getString_for_key_SystemConfigKeys_enableCache_return_true() {
        //when:
        boolean result = config
                .getBoolean(SystemConfigKeys.ENABLE_CACHE, false);
        //then:
        assertTrue(result);
    }

    @Test
    public void invoke_getString_for_key_SystemConfigKeys_httpRetriverDoNotAllowEnableReactivation_return_true() {
        //when:
        boolean result = config
                .getBoolean(SystemConfigKeys.HTTP_RETRIVER_DO_NOT_ALLOW_ENABLE_REACTIVATION, false);
        //then:
        assertTrue(result);
    }
}
