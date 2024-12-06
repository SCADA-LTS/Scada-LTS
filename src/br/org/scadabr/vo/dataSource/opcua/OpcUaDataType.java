package br.org.scadabr.vo.dataSource.opcua;

import com.serotonin.mango.DataTypes;

import java.util.stream.Stream;

public enum OpcUaDataType {

    BOOL ("boolean", DataTypes.BINARY) {
        public Object convert(Object value) throws Exception {
            return OpcUaDataType.toBoolean(value);
        }
    },

    SINT ("int 8", DataTypes.NUMERIC) {
        public Object convert(Object value) throws Exception {
            return toByte(value);
        }
    },

    USINT ("uint 8", DataTypes.NUMERIC) {
        public Object convert(Object value) throws Exception {
            return toByte(value);
        }
    },

    BYTE ("uint 8", DataTypes.NUMERIC) {
        public Object convert(Object value) throws Exception {
            return toByte(value);
        }
    },

    INT ("int 16", DataTypes.NUMERIC) {
        public Object convert(Object value) throws Exception {
            return toShort(value);
        }
    },

    UINT ("uint 16", DataTypes.NUMERIC) {
        public Object convert(Object value) throws Exception {
            return toShort(value);
        }
    },

    WORD ("uint 16", DataTypes.NUMERIC) {
        public Object convert(Object value) throws Exception {
            return toShort(value);
        }
    },

    DINT ("int 32", DataTypes.NUMERIC) {
        public Object convert(Object value) throws Exception {
            return toInt(value);
        }
    },

    UDINT ("uint 32", DataTypes.NUMERIC) {
        public Object convert(Object value) throws Exception {
            return toInt(value);
        }
    },

    DWORD ("uint 32", DataTypes.NUMERIC) {
        public Object convert(Object value) throws Exception {
            return toInt(value);
        }
    },

    LINT ("int 64", DataTypes.NUMERIC) {
        public Object convert(Object value) throws Exception {
            return toLong(value);
        }
    },

    ULINT ("uint 64", DataTypes.NUMERIC) {
        public Object convert(Object value) throws Exception {
            return toLong(value);
        }
    },

    LWORD ("uint 64", DataTypes.NUMERIC) {
        public Object convert(Object value) throws Exception {
            return toLong(value);
        }
    },

    REAL ("float", DataTypes.NUMERIC) {
        public Object convert(Object value) throws Exception {
            return toFloat(value);
        }
    },

    LREAL ("double", DataTypes.NUMERIC) {
        public Object convert(Object value) throws Exception {
            return toDouble(value);
        }
    },

    CHAR ("char", DataTypes.ALPHANUMERIC) {
        public Object convert(Object value) throws Exception {
            return toCharOrString(value);
        }
    },

    WCHAR ("2 byte char", DataTypes.ALPHANUMERIC) {
        public Object convert(Object value) throws Exception {
            return toCharOrString(value);
        }
    },

    STRING ("utf-8", DataTypes.ALPHANUMERIC) {
        public Object convert(Object value) throws Exception {
            return OpcUaDataType.toString(value);
        }
    },

    WSTRING ("utf-16", DataTypes.ALPHANUMERIC) {
        public Object convert(Object value) throws Exception {
            return OpcUaDataType.toString(value);
        }
    };

    public static final OpcUaDataType DEFAULT = OpcUaDataType.STRING;

    private final String decription;
    private final int dataTypesId;

    OpcUaDataType(String decription, int dataTypesId) {
        this.decription = decription;
        this.dataTypesId = dataTypesId;
    }

    public String getCode() {
        return this.name();
    }

    public int getDataTypesId() {
        return dataTypesId;
    }

    public String getDescription() {
        return getCode() + " (" + this.decription + ")";
    }

    public abstract Object convert(Object value) throws Exception;

    public static OpcUaDataType valueOf(int id) {
        return Stream.of(OpcUaDataType.values()).filter(a -> a.getDataTypesId() == id).findAny().orElse(OpcUaDataType.STRING);
    }

    private static boolean toBoolean(Object value) {
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private static Object toCharOrString(Object value) {
        String result = String.valueOf(value);
        char[] resultRaw = result.toCharArray();
        return resultRaw.length == 1 ? resultRaw[0] : result;
    }

    private static byte toByte(Object value) {
        return Byte.parseByte(toStringForInt(value));
    }

    private static short toShort(Object value) {
        return Short.parseShort(toStringForInt(value));
    }

    private static int toInt(Object value) {
        return Integer.parseInt(toStringForInt(value));
    }

    private static long toLong(Object value) {
        return Long.parseLong(toStringForInt(value));
    }

    private static double toDouble(Object value) {
        return Double.parseDouble(String.valueOf(value));
    }

    private static float toFloat(Object value) {
        return Float.parseFloat(String.valueOf(value));
    }

    private static String toStringForInt(Object value) {
        return String.valueOf(value).replace(".0", "");
    }

    private static String toString(Object value) {
        return String.valueOf(value);
    }
}
