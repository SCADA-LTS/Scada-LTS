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

package org.scada_lts.mango.adapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;


/** 
 * Adapter for {@link MangoScadaConfigurable}
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class MangoScadaConfig implements MangoScadaConfigurable {

	private final Log LOG = LogFactory.getLog(MangoScadaConfig.class);
	
	@Override
	public void init() {
		
		LOG.info("start");
	
		 if (ScadaConfig.isExistCustomLogo()) {
			 // nothning doing
		 } else {
			 LOG.info("copy logo");
			 ScadaConfig.copyLogo();
		 }
		 
		 if (ScadaConfig.isExistCustomCSS()) {
			// nothning doing
		 } else {
			 LOG.info("copy css");
			 ScadaConfig.copyCSS();
		 }
		 
		 if (ScadaConfig.isExistCustomEnvProperties()) {
			 LOG.info("copy env.properties");
			 ScadaConfig.copyConfig();
		 }
				
	}

}
