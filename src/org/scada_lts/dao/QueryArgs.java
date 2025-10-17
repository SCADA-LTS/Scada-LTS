package org.scada_lts.dao;

public class QueryArgs {
    private final String query;
    private final Object[] args;

    public QueryArgs(String query, Object[] args) {
        this.query = query;
        this.args = args;
    }

    public String getQuery() {
        return query;
    }

    public Object[] getArgs() {
        return args;
    }
}
