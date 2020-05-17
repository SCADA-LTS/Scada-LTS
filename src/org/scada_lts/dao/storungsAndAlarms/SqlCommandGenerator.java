package org.scada_lts.dao.storungsAndAlarms;

import com.sun.org.apache.xpath.internal.operations.String;

class SqlCommandGenerator {

    private static StringBuilder stringBuilder;
    private static String string;
    public static String generateStringBuilderSqlForStorungs(){
        stringBuilder = new StringBuilder("");
        stringBuilder.append("select * ");
        stringBuilder.append("from ");
        stringBuilder.append("pointValues ");
        stringBuilder.append("where id=1;");

        return stringBuilder.toString();
    }


    public static String generateStringSqlForStorungs(){
        string="";
        string+="select * ";
        string+="from ";
        string+="pointValues ";
        string+="where id=1;";

        return string;
    }
    public static java.lang.String setAcknowledge()
    {
        return new StringBuilder("update plcAlarms set akcnowledgeTime='1' where id=?").toString();
    }
}
