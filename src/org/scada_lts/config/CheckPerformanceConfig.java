package org.scada_lts.config;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckPerformanceConfig implements Configurable<Boolean> {
	
	private static final Log LOG = LogFactory.getLog(CheckPerformanceConfig.class);
	
	boolean checkPerformance = false;
	
	public CheckPerformanceConfig() {
		try {
			checkPerformance = ScadaConfig.getInstance().getBoolean(ScadaConfig.PROFILE_LOG, false);
		} catch (IOException e) {
			LOG.error(e);
		}
	}

	@Override
	public Boolean getConfig() {
		return checkPerformance;
	}

}
