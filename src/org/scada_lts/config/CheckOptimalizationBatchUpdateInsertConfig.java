package org.scada_lts.config;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckOptimalizationBatchUpdateInsertConfig implements Configurable<Boolean> {

private static final Log LOG = LogFactory.getLog(CheckPerformanceConfig.class);
	
	boolean checkOptimalizationBatchUpdateInsertConfig = false;
	
	public CheckOptimalizationBatchUpdateInsertConfig() {
		try {
			checkOptimalizationBatchUpdateInsertConfig = ScadaConfig.getInstance().getBoolean(ScadaConfig.SET_OPTIMALIZATION_BATCH_UPDATE_INSERT, false);
		} catch (IOException e) {
			LOG.error(e);
		}
	}

	@Override
	public Boolean getConfig() {
		return checkOptimalizationBatchUpdateInsertConfig;
	}
	
}
