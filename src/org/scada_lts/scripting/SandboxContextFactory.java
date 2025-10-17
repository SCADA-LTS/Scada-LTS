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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.scada_lts.utils.SystemSettingsUtils;

import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** 
 * Set new protected context 
 * 
 * @author Zuzana Maczek, grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class SandboxContextFactory extends ContextFactory {

	private static final Log LOG = LogFactory.getLog(SandboxContextFactory.class);

	private static final Pattern[] SECURITY_JS_ACCESS_DENIED_CLASS_REGEXES = Stream.of(SystemSettingsUtils.getSecurityJsAccessDeniedClassRegexes())
			.map(Pattern::compile)
			.collect(Collectors.toList())
			.toArray(new Pattern[]{});

	private static final Pattern[] SECURITY_JS_ACCESS_GRANTED_CLASS_REGEXES = Stream.of(SystemSettingsUtils.getSecurityJsAccessGrantedClassRegexes())
			.map(Pattern::compile)
			.collect(Collectors.toList())
			.toArray(new Pattern[]{});
	
	@Override
	protected Context makeContext() {
		Context cx = super.makeContext();
		cx.setWrapFactory(new SandboxWrapFactory());
		cx.setClassShutter(className -> {
			for(Pattern pattern: SECURITY_JS_ACCESS_DENIED_CLASS_REGEXES) {
				if (pattern.matcher(className).matches()) {
					if(LOG.isWarnEnabled())
						LOG.warn("access denied for class: " + className);
					return false;
				}
			}
			for(Pattern pattern: SECURITY_JS_ACCESS_GRANTED_CLASS_REGEXES) {
				if (pattern.matcher(className).matches()) {
					return true;
				}
			}
			if(LOG.isWarnEnabled())
				LOG.warn("access denied for class: " + className);
			return false;
		});
		return cx;
	}

}
