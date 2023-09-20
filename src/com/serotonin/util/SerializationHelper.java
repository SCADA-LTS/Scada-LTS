//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.serotonin.util;

import com.serotonin.ShouldNeverHappenException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SerializationHelper {

    private static final Log LOG = LogFactory.getLog(SerializationHelper.class);
    private SerializationHelper() {
    }

    public static void writeSafeUTF(ObjectOutputStream out, String utf) throws IOException {
        if (utf == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            long utf8Length = utf.getBytes(StandardCharsets.UTF_8).length;
            if (utf8Length <= 0xFFFFL) {
                out.writeUTF(utf);
            } else if (utf.length() == utf8Length) {
                out.writeObject(utf);
            } else {
                String utf2 = new String(utf.getBytes(), StandardCharsets.UTF_8);
                out.writeObject(utf2);
            }
        }
    }

    public static String readSafeUTF(ObjectInputStream in) throws IOException {
        boolean exists = in.readBoolean();
        try {
            return exists ? in.readUTF() : null;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            try {
                return exists ? (String) in.readObject() : null;
            } catch (Exception ex2) {
                LOG.warn(ex2.getMessage(), ex2);
                return null;
            }
        }
    }

    public static Object readObject(InputStream is) throws ShouldNeverHappenException {
        if (is == null) {
            return null;
        } else {
            try {
                return (new ObjectInputStream(is)).readObject();
            } catch (Exception var2) {
                throw new ShouldNeverHappenException(var2);
            }
        }
    }

    public static Object readObjectInContext(InputStream is) throws ShouldNeverHappenException {
        if (is == null) {
            return null;
        } else {
            try {
                return (new ClassLoaderObjectInputStream(is, Thread.currentThread().getContextClassLoader())).readObject();
            } catch (Exception var2) {
                throw new ShouldNeverHappenException(var2);
            }
        }
    }

    public static Object readObject(InputStream is, ClassLoader classLoader) throws ShouldNeverHappenException {
        if (is == null) {
            return null;
        } else {
            try {
                return (new ClassLoaderObjectInputStream(is, classLoader)).readObject();
            } catch (Exception var3) {
                throw new ShouldNeverHappenException(var3);
            }
        }
    }

    public static Object readObjectFromArray(byte[] data) throws ShouldNeverHappenException {
        return data == null ? null : readObject(new ByteArrayInputStream(data));
    }

    public static ByteArrayInputStream writeObject(Object o) throws ShouldNeverHappenException {
        return o == null ? null : new ByteArrayInputStream(writeObjectToArray(o));
    }

    public static byte[] writeObjectToArray(Object o) throws ShouldNeverHappenException {
        if (o == null) {
            return null;
        } else {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                (new ObjectOutputStream(baos)).writeObject(o);
                return baos.toByteArray();
            } catch (IOException var2) {
                throw new ShouldNeverHappenException(var2);
            }
        }
    }

    static class ClassLoaderObjectInputStream extends ObjectInputStream {
        private final ClassLoader classLoader;

        public ClassLoaderObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
            super(in);
            this.classLoader = classLoader;
        }

        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            String name = desc.getName();

            try {
                return Class.forName(name, false, this.classLoader);
            } catch (ClassNotFoundException var4) {
                return super.resolveClass(desc);
            }
        }
    }
}
