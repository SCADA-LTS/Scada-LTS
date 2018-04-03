package org.scada_lts.serorepl.utils;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class DirectoryUtilsTest {
    @Test
    public void getDirectorySize() throws Exception {

        assertEquals(DirectoryUtils.getDirectorySize(new File("src/br/org/scadabr/api/vo")).getSize() , com.serotonin.util.DirectoryUtils.getDirectorySize(new File("src/br/org/scadabr/api/vo")).getSize());
        assertEquals(DirectoryUtils.getDirectorySize(new File("src/br/org/scadabr/api/da")).getSize() , com.serotonin.util.DirectoryUtils.getDirectorySize(new File("src/br/org/scadabr/api/da")).getSize());
        assertEquals(DirectoryUtils.getDirectorySize(new File("src/br/org/scadabr/api/config")).getSize() , com.serotonin.util.DirectoryUtils.getDirectorySize(new File("src/br/org/scadabr/api/config")).getSize());
        assertEquals(DirectoryUtils.getDirectorySize(new File("src/br/org/scadabr/api")).getSize() , com.serotonin.util.DirectoryUtils.getDirectorySize(new File("src/br/org/scadabr/api")).getSize());
    }

    @Test
    public void bytesDescription() throws Exception {

        long size1 = 1654668425;
        long size2 = 18425;
        long size3 = 668425;
        long size4 = 425;

        assertEquals(DirectoryUtils.bytesDescription(size1) , com.serotonin.util.DirectoryUtils.bytesDescription(size1));
        assertEquals(DirectoryUtils.bytesDescription(size2) , com.serotonin.util.DirectoryUtils.bytesDescription(size2));
        assertEquals(DirectoryUtils.bytesDescription(size3) , com.serotonin.util.DirectoryUtils.bytesDescription(size3));
        assertEquals(DirectoryUtils.bytesDescription(size4) , com.serotonin.util.DirectoryUtils.bytesDescription(size4));
    }

}