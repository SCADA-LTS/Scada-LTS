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

package org.scada_lts.scripting;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/** 
 * Set new protected context 
 * 
 * @author Zuzana Maczek, grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class SandboxContextFactory extends ContextFactory {
	
	/**
	 * Create and configure a Rhino Context that enforces the sandboxing rules.
	 *
	 * The returned context uses a SandboxWrapFactory and a SandboxClassShutter to
	 * control object wrapping and class access within the scripting environment.
	 *
	 * @return a Context configured with a SandboxWrapFactory and a SandboxClassShutter
	 */
	@Override
	protected Context makeContext() {
		Context cx = super.makeContext();
		cx.setWrapFactory(new SandboxWrapFactory());
		cx.setClassShutter(new SandboxClassShutter());
		return cx;
	}

}