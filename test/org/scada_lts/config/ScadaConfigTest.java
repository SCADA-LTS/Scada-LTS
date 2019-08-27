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


import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;

/**
 * Test config Scada-LTS
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class ScadaConfigTest {

    private static ScadaConfig config;

    @BeforeClass
    public static void init() throws Exception {
        DOMConfigurator.configure("WebContent/WEB-INF/classes/log4j.xml");
        String path = MessageFormat.format("{0}resources{0}env-test.properties", File.separator);
        config = ScadaConfig.getConfigFromExternalFile(new File(path));
    }

    @Test
    public void invoke_size_then_return_28() {
        //when:
        int size = config.size();
        //then:
        assertEquals(28, size);
    }

    @Test
    public void invoke_getInt_for_key_corePoolSize_return_3() {
        //when:
        int corePoolSize = config.getInt(ThreadPoolConfigKeys.CORE_POOL_SIZE, -1);
        //then:
        assertEquals(3, corePoolSize);
    }
}
