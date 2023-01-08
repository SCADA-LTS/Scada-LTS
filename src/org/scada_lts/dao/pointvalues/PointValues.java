package org.scada_lts.dao.pointvalues;

@Entity
public class PointValues {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="id")
    private String  COLUMN_NAME_ID = "id";
    @Column(name ="dataType")
    private String  COLUMN_NAME_DATA_TYPE = "dataType";
    @Column(name ="pointValue")
    private   String  COLUMN_NAME_POINT_VALUE = "pointValue";
    @Column(name ="textPointValueShort")
    private   String  COLUMN_NAME_TEXT_POINT_VALUE_SHORT = "textPointValueShort";
    @Column(name ="textPointValueLong")
    private   String  COLUMN_NAME_TEXT_POINT_VALUE_LONG = "textPointValueLong";
    @Column(name ="sourceType")
    private   String  COLUMN_NAME_SOURCE_TYPE = "sourceType";
    @Column(name ="ts")
    private   String  COLUMN_NAME_TIME_STAMP = "ts";
    @Column(name ="sourceId")
    private   String  COLUMN_NAME_SOURCE_ID = "sourceId";
    @Column(name ="pointValueId")
    private   String  COLUMN_NAME_POINT_VALUE_ID = "pointValueId";
    @Column(name ="dataPointId")
    private   String  COLUMN_NAME_DATA_POINT_ID = "dataPointId";
    @Column(name ="minTs")
    private   String  COLUMN_NAME_MIN_TIME_STAMP = "minTs";
    @Column(name ="maxTs")
    private   String  COLUMN_NAME_MAX_TIME_STAMP = "maxTs";

    private   String  COLUMN_NAME_USERNAME_IN_TABLE_USERS = "username";
}
