package com.serotonin.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utils.FileTestUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RunWith(Parameterized.class)
public class SerializationHelperTest {

    @Parameterized.Parameters(name= "{index}: file: {0}, encoding: {1}")
    public static String[][] data() {
        return new String[][] {
                {"662_bytes_utf8.txt", "UTF-8"},
                {"65535_bytes_utf8.txt", "UTF-8"},
                {"65536_bytes_utf8.txt", "UTF-8"},
                {"5mb_5242880_bytes_utf8.txt", "UTF-8"},
        };
    }

    private final String toWrite;
    private final String utf8Expected;

    public SerializationHelperTest(String file, String encoding) throws IOException {
        this.toWrite = Files.readString(Path.of(FileTestUtils.getResourcesPath("encoding", file)), Charset.forName(encoding));
        this.utf8Expected = new String(toWrite.getBytes(), StandardCharsets.UTF_8);
    }

    @Test
    public void when_writeSafeUTF_and_readSafeUTF_then_equals_bytes() throws IOException {

        //given:
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //when:
        try(ObjectOutputStream outputStream = new ObjectOutputStream(baos)) {
            SerializationHelper.writeSafeUTF(outputStream, toWrite);
        }

        //then
        try(ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            String result = SerializationHelper.readSafeUTF(inputStream);

            byte[] bytesExpected = utf8Expected.getBytes();
            byte[] bytesResult = result.getBytes();

            Assert.assertEquals(bytesExpected.length, bytesResult.length);

            for(int i = 0; i < bytesExpected.length ; i ++) {
                Assert.assertEquals(bytesExpected[i], bytesResult[i]);
            }
        }
    }

    @Test
    public void when_writeSafeUTF_and_readSafeUTF_then_equals_chars() throws IOException {

        //given:
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //when:
        try(ObjectOutputStream outputStream = new ObjectOutputStream(baos)) {
            SerializationHelper.writeSafeUTF(outputStream, toWrite);
        }

        //then
        try(ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            String result = SerializationHelper.readSafeUTF(inputStream);

            char[] charsExpected = utf8Expected.toCharArray();
            char[] charsResult = result.toCharArray();

            Assert.assertEquals(charsExpected.length, charsResult.length);

            for(int i = 0; i < charsExpected.length ; i ++) {
                Assert.assertEquals(charsExpected[i], charsResult[i]);
            }
        }
    }
}