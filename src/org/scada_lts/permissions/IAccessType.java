package org.scada_lts.permissions;

/** 
 * Access type interface 
 * 
 * @author Arkadiusz Parafiniuk    email: arkadiusz.parafiniuk@gmail.com
 * 
 */

public interface IAccessType {
	static final int ACCESS_TYPE_NONE = 0;
	static final int ACCESS_TYPE_READ = 1;
	static final int ACCESS_TYPE_SET = 2;
	static final int ACCESS_TYPE_ADMIN = 3;
}
