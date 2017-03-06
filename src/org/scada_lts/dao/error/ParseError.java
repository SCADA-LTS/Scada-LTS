package org.scada_lts.dao.error;

public class ParseError {
	
	//TODO rewrite CustomSQLEroorCodesTranslator
	
	public static final ParseErrorResult getError(String str) {
		ParseErrorResult per = new ParseErrorResult();
		try {
			per.setMessage(str.split("#")[1].toString());
		} catch (Exception e) {
			per.setMessageOrgin(str);
			per.setNotErr(false);
		}
		return per;
	}

}
