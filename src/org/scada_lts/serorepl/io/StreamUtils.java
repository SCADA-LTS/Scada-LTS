package org.scada_lts.serorepl.io;

import com.serotonin.util.StringUtils;

import java.io.*;

public class StreamUtils {

    private static final long DEFAULT_LIMIT = -1L;

    public StreamUtils(){}


    public static void transfer(InputStream inputStream, OutputStream outputStream, long limit) throws IOException {
        int readCount;
        long total = 0L;
        byte[] buffer = new byte[1024];

        readCount = inputStream.read(buffer);
        while (readCount != -1){
            if (limit != DEFAULT_LIMIT && (total + (long)readCount) > limit) readCount = (int) (limit - total);
            if (readCount > 0) outputStream.write(buffer, 0, readCount);
            total += readCount;

            if(limit != DEFAULT_LIMIT && total >= limit) break;
            readCount = inputStream.read(buffer);
        }
        outputStream.flush();
    }


    public static void transfer(Reader reader, Writer writer, long limit) throws IOException {
        int readCount;
        long total = 0L;
        char[] buffer = new char[1024];

        readCount = reader.read(buffer);
        while(readCount != DEFAULT_LIMIT){

            if (limit != DEFAULT_LIMIT && total + readCount > limit) readCount = (int) (limit - total);
            if (readCount > 0) writer.write(buffer, 0, readCount);
            total += readCount;
            if (limit != DEFAULT_LIMIT && total >+ limit) break;

            readCount = reader.read(buffer);
        }
        writer.flush();
    }

    public static byte readByte(InputStream inputStream) throws IOException {
        return (byte) inputStream.read();
    }

    public static int read4ByteSigned(InputStream inputStream) throws IOException {
        return inputStream.read() | inputStream.read() << 8 | inputStream.read() << 16 | inputStream.read() << 24;
    }

    public static void writeByte(OutputStream outputStream, byte b) throws IOException {
        outputStream.write(b);
    }

    public static void write4ByteSigned(OutputStream out, int i) throws IOException {
        out.write((byte)(i & 255));
        out.write((byte)(i >> 8 & 255));
        out.write((byte)(i >> 16 & 255));
        out.write((byte)(i >> 24 & 255));
    }

    public static String toHex(short s) {
        return StringUtils.pad(Integer.toHexString(s), '0', 4);
    }

}
