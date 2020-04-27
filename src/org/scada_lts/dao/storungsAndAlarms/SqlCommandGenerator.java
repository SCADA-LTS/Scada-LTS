package org.scada_lts.dao.storungsAndAlarms;

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
}
