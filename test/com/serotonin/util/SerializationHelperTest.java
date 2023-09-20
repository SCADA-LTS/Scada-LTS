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
                {"662_bytes_iso_latin1.txt", "ISO_8859_1"},
                {"662_bytes_us_ascii.txt", "ASCII"},
                {"662_bytes_utf16.txt", "UTF-16"},

                {"65535_bytes_utf8.txt", "UTF-8"},
                {"65535_bytes_iso_latin1.txt", "ISO_8859_1"},
                {"65535_bytes_us_ascii.txt", "ASCII"},
                {"65535_bytes_utf16.txt", "UTF-16"},

                {"65536_bytes_utf8.txt", "UTF-8"},
                {"65536_bytes_iso_latin1.txt", "ISO_8859_1"},
                {"65536_bytes_us_ascii.txt", "ASCII"},
                {"65536_bytes_utf16.txt", "UTF-16"},

                {"5mb_5242880_bytes_utf8.txt", "UTF-8"},
                {"3_5mb_5242880_bytes_iso_latin1.txt", "ISO_8859_1"},
                {"3_5mb_5242880_bytes_us_ascii.txt", "ASCII"},
                {"7mb_5242880_bytes_utf16.txt", "UTF-16"}
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(ObjectOutputStream outputStream = new ObjectOutputStream(baos)) {

            //when:
            SerializationHelper.writeSafeUTF(outputStream, toWrite);
        }
        try(ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            String result = SerializationHelper.readSafeUTF(inputStream);

            //then
            Assert.assertEquals(toList(utf8Expected.getBytes()), toList(result.getBytes()));
        }
    }

    @Test
    public void when_writeSafeUTF_and_readSafeUTF_then_equals() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(ObjectOutputStream outputStream = new ObjectOutputStream(baos)) {

            //when:
            SerializationHelper.writeSafeUTF(outputStream, toWrite);
        }
        try(ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            String result = SerializationHelper.readSafeUTF(inputStream);

            //then
            Assert.assertEquals(utf8Expected, result);
        }
    }

    private static List<Byte> toList(byte[] bytes) {
        List<Byte> byteList = new ArrayList<>();
        for(byte by: bytes) {
            byteList.add(by);
        }
        return byteList;
    }
}