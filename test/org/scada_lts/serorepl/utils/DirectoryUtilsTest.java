package org.scada_lts.serorepl.utils;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class DirectoryUtilsTest {
    @Test
    public void getDirectorySize() throws Exception {

        assertEquals(DirectoryUtils.getDirectorySize(new File("\"C:\\\\Users\\\\Jerzyk\\\\Desktop\\\\remove\"")).size , com.serotonin.util.DirectoryUtils.getDirectorySize(new File("\"C:\\\\Users\\\\Jerzyk\\\\Desktop\\\\remove\"")).getSize());
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