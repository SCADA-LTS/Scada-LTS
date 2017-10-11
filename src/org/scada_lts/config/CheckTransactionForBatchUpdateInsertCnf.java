package org.scada_lts.config;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckTransactionForBatchUpdateInsertCnf implements Configurable<Boolean> {
	
private static final Log LOG = LogFactory.getLog(CheckPerformanceConfig.class);
	
	boolean checkTransactionForBatchUpdateInsert = false;
	
	public CheckTransactionForBatchUpdateInsertCnf() {
		try {
			checkTransactionForBatchUpdateInsert = ScadaConfig.getInstance().getBoolean(ScadaConfig.TRANSACTION_FOR_BATCH_UPDATE_INSERT, false);
		} catch (IOException e) {
			LOG.error(e);
		}
	}

	@Override
	public Boolean getConfig() {
		return checkTransactionForBatchUpdateInsert;
	}

}
