package org.scada_lts.dao.error;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

@Deprecated(since = "2.7.5.4")
public class CustomSQLEroorCodesTranslator extends SQLErrorCodeSQLExceptionTranslator {
	
	@Resource
	MessageSource msgSource;
	
	//protected DataAccessException customTranslate(String task, String sql, SQLException sqlex) {
		
		//TODO
		/*
		 * if (sqlex.getErrorCode() == 1664) {
		 
			DataAccessException dae = new DataAccessException(msgSource.getMessage("error.view_hierarchy.add.error1",null, Locale.getDefault())) {
			} 
			new DataAccessException();return  
		}*/
		
	//}

}
