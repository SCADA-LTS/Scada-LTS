package com.serotonin.mango.vo.bean;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.serotonin.db.spring.GenericRowMapper;

public class LongPair {
    public static final GenericRowMapper<LongPair> ROW_MAPPER = new GenericRowMapper<LongPair>() {
        @Override
        public LongPair mapRow(ResultSet rs, int index) throws SQLException {
            return new LongPair(rs.getLong(1), rs.getLong(2));
        }
    };

    private long l1;
    private long l2;

    public LongPair() {
        // no op
    }

    public LongPair(long l1, long l2) {
        this.l1 = l1;
        this.l2 = l2;
    }

    public long getL1() {
        return l1;
    }

    public void setL1(long l1) {
        this.l1 = l1;
    }

    public long getL2() {
        return l2;
    }

    public void setL2(long l2) {
        this.l2 = l2;
    }
}
