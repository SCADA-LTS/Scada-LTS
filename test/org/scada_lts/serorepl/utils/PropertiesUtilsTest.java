package org.scada_lts.serorepl.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PropertiesUtilsTest {

    PropertiesUtils scadaProp;
    com.serotonin.util.PropertiesUtils seroProp;

    PropertiesUtils scadaProp1;
    com.serotonin.util.PropertiesUtils seroProp1;

    @Before
    public void init() throws Exception{
        scadaProp = new PropertiesUtils("env");
        seroProp = new com.serotonin.util.PropertiesUtils("env");

        scadaProp1 = new PropertiesUtils("");
        seroProp1 = new com.serotonin.util.PropertiesUtils("");

    }


    @Test
    public void getString() throws Exception {
        assertEquals(scadaProp.getString("db.type")  ,  seroProp.getString("db.type"));
    }

    @Test
    public void getStringWithDefaultValue() throws Exception {
        assertEquals(scadaProp.getString("db.type", "derby")  ,  seroProp.getString("db.type", "derby"));
    }

    @Test
    public void getInt() throws Exception {
        assertEquals(scadaProp.getInt("js.optimizationlevel")  ,  seroProp.getInt("js.optimizationlevel"));

    }

    @Test
    public void getIntWithDefaultValue() throws Exception {
        assertEquals(scadaProp1.getInt("js.optimizationlevel", 0)  ,  seroProp1.getInt("js.optimizationlevel", 0));



    }

    @Test
    public void getBoolean() throws Exception {
        assertEquals(scadaProp1.getBoolean("auth.crowd.on",false)  ,  seroProp1.getBoolean("auth.crowd.on",false));

    }

}