package com.serotonin.mango.view;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


@RunWith(JUnit4.class)
public class ViewTest {

    private static final Log LOG = LogFactory.getLog(ViewTest.class);
    private View view;
    private FileOutputStream out;
    private ObjectOutputStream oout;
    private ObjectInputStream ois;

    @Before
    public void init() {
        view= new View();
        try {
            out = new FileOutputStream("test.txt");
            oout = new ObjectOutputStream(out);
            ois = new ObjectInputStream(new FileInputStream("test.txt"));
        } catch (FileNotFoundException e) {
            LOG.info("FileNotFoundException"+e.getMessage());
        } catch (IOException e) {
            LOG.info("IOException"+e.getMessage());
        }

    }

    @After
    public void finalized() {
        view = null;
        out = null;
        oout = null;
    }

    /**
     * that test should invoke both ways of reading stream because
     * of first value is Integer, like version
     */
    @Test
    public void savedIntValueIntoStreamAndCheckReadSavedValueTest() {

        LOG.info("Here should be invoked WayWithoutVersion and WayForFirstVersion");

        try {
            oout.writeInt(1);
            oout.flush();

            int firstIntValue = view.getFirstIntValueFromObjectInputStream((ObjectInputStream) ois);

            LOG.info("First Integer Value written is 1 and result should be 1. Result is :" + firstIntValue);

            Assert.assertEquals(firstIntValue,1);

        } catch (Exception ex) {
            LOG.info("Exception"+ex.getMessage());
        }
    }

    /**
     * that test should invoke only WayWithoutVersion because
     * of first value is not saved
     */
    @Test
    public void notSavedAnyIntValueIntoStreamAndValueShouldNotExistInStreamTest() {

        LOG.info("Here shouldn't be invoked WayForFirstVersion. Only WayWithoutVersion");
        try {
            oout.flush();

            int firstIntValueReadFromStream = view.getFirstIntValueFromObjectInputStream((ObjectInputStream) ois);

            LOG.info("First Integer Value not exist and result should be -1.Result is :" + firstIntValueReadFromStream);

            Assert.assertEquals(firstIntValueReadFromStream,-1);

        } catch (Exception ex) {
            LOG.info("Exception"+ex.getMessage());
        }
    }
}
