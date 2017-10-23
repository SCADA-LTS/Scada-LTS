package org.scada_lts.permissions;

import java.util.HashMap;
import java.util.Map;

import org.scada_lts.permissions.model.AccessType;

/** 
 * Access type interface 
 * 
 * @author Arkadiusz Parafiniuk    email: arkadiusz.parafiniuk@gmail.com
 * 
 */

public interface IAccessType {
	
	final static Map<Long, AccessType> accessTypes = new HashMap<Long, AccessType>();
	
    void setUp();
    
    default AccessType get(Long idAccessType) {
    	return accessTypes.get(idAccessType);
    }
	
}
