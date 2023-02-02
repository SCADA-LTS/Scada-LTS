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

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.scada_lts.utils.SystemSettingsUtils;

/** 
 * Own sandbox for native java object.
 * 
 * @author Zuzana Maczek, grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class SandboxNativeJavaObject extends NativeJavaObject  {

	private static final long serialVersionUID = -7251515169121482423L;

	public SandboxNativeJavaObject(Scriptable scope, Object javaObject, @SuppressWarnings("rawtypes") Class staticType) {
		super(scope, javaObject, staticType);
	}
 
	@Override
	public Object get(String name, Scriptable start) {
		for(String conf: SystemSettingsUtils.getSecurityJsAccessDeniedMethods()) {
			if (name.matches(conf)) {
				return NOT_FOUND;
			}
		}
		for(String conf: SystemSettingsUtils.getSecurityJsAccessGrantedMethods()) {
			if (name.matches(conf)) {
				return super.get(name, start);
			}
		}
		return NOT_FOUND;
	}

}
