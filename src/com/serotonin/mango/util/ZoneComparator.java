package com.serotonin.mango.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;

/**
 * Comparator
 * @author Smart e-Tech
 * @category Timezone
 * @version 1
 *  
 */

public class ZoneComparator implements Comparator<ZoneId> {

	@Override
	public int compare(ZoneId zoneId1, ZoneId zoneId2) {
		 LocalDateTime now = LocalDateTime.now();
	        ZoneOffset offset1 = now.atZone(zoneId1).getOffset();
	        ZoneOffset offset2 = now.atZone(zoneId2).getOffset();
	 
	        return offset1.compareTo(offset2);
	}

}
