package org.scada_lts.config;

public class Configurations {
	
	private Configurable<Boolean> checkPerformance;
	private Configurable<Boolean> checkTransactionForBatchUpdateInsert;
	private Configurable<Boolean> checkOptimalizationBatchUpdateInsertConfig;

	private static Configurations instance = null;
	
	public static Configurations getInstance() {
		if (instance == null) {
			instance = new Configurations();
		}
		return instance;
	}
	
	private Configurations() {		
		checkPerformance = new CheckPerformanceConfig();
		checkTransactionForBatchUpdateInsert = new CheckTransactionForBatchUpdateInsertCnf();
		checkOptimalizationBatchUpdateInsertConfig = new CheckOptimalizationBatchUpdateInsertConfig();
	}

	public Configurable<Boolean> getCheckPerformance() {
		return checkPerformance;
	}
	
	public Configurable<Boolean> getCheckTransForBatchUpdateInsert() {
		return checkTransactionForBatchUpdateInsert;
	}
	
	public Configurable<Boolean> getCheckOptimalizationBatchUpdateInsertConfig() {
		return checkOptimalizationBatchUpdateInsertConfig;
	}
	

}
