package cc.radiuino.scadabr.rt.datasource.radiuino;

import java.nio.ByteBuffer;

import cc.radiuino.scadabr.vo.datasource.radiuino.RadiuinoDataType;
import cc.radiuino.scadabr.vo.datasource.radiuino.RadiuinoPointLocatorVO;

import com.serotonin.mango.rt.dataImage.PointValueTime;

public class RadiuinoUtils {

	public static PointValueTime parsePacoteRadiuino(long time, byte[] pacote,
			RadiuinoPointLocatorVO dataPointVO) throws Exception {
		// for (int i = 0; i < pacote.length; i++) {
		// System.out.print("" + unsignedByteToInt(pacote[i]) + " ");
		// }
		// System.out.println("");

		PointValueTime pointValueTime = null;

		if (dataPointVO.getRadiuinoDataType() == RadiuinoDataType.BINARY) {
			boolean value = pacote[dataPointVO.getIndiceByte()] == 1;
			pointValueTime = new PointValueTime(value, time);
		} else if (dataPointVO.getRadiuinoDataType() == RadiuinoDataType.ONE_BYTE_INT_UNSIGNED) {
			float value = unsignedByteToInt(pacote[dataPointVO.getIndiceByte()]);
			value = value * dataPointVO.getMultiplicador()
					+ dataPointVO.getOffset();
			pointValueTime = new PointValueTime(value, time);
		} else if (dataPointVO.getRadiuinoDataType() == RadiuinoDataType.TWO_BYTE_INT_UNSIGNED) {
			float value = unsignedByteToInt(pacote[dataPointVO.getIndiceByte()])
					* 256
					+ unsignedByteToInt(pacote[dataPointVO.getIndiceByte() + 1]);
			value = value * dataPointVO.getMultiplicador()
					+ dataPointVO.getOffset();
			pointValueTime = new PointValueTime(value, time);
		} else if (dataPointVO.getRadiuinoDataType() == RadiuinoDataType.FOUR_BYTE_INT_UNSIGNED) {
			float value = (unsignedByteToInt(pacote[dataPointVO.getIndiceByte()]) * 256 + unsignedByteToInt(pacote[dataPointVO
					.getIndiceByte() + 1]))
					* 65536
					+ (unsignedByteToInt(pacote[dataPointVO.getIndiceByte() + 2]) * 256 + unsignedByteToInt(pacote[dataPointVO
							.getIndiceByte() + 3]));
			value = value * dataPointVO.getMultiplicador()
					+ dataPointVO.getOffset();
			pointValueTime = new PointValueTime(value, time);
		} else if (dataPointVO.getRadiuinoDataType() == RadiuinoDataType.FOUR_BYTE_INT_UNSIGNED) {
			ByteBuffer byteBuffer = ByteBuffer.allocate(4);
			byteBuffer.put(pacote[dataPointVO.getIndiceByte()]);
			byteBuffer.put(pacote[dataPointVO.getIndiceByte() + 1]);
			byteBuffer.put(pacote[dataPointVO.getIndiceByte() + 2]);
			byteBuffer.put(pacote[dataPointVO.getIndiceByte() + 4]);
			float value = byteBuffer.getFloat();
			value = value * dataPointVO.getMultiplicador()
					+ dataPointVO.getOffset();
			pointValueTime = new PointValueTime(value, time);
		} else if (dataPointVO.getRadiuinoDataType() == RadiuinoDataType.RSSI) {
			double value = unsignedByteToInt(pacote[dataPointVO.getIndiceByte()]);
			if (value > 128) {
				value = ((value - 256) / 2.0) - 74;
			} else {
				value = (value / 2.0) - 74;
			}
			value = value * dataPointVO.getMultiplicador()
					+ dataPointVO.getOffset();
			pointValueTime = new PointValueTime(value, time);
		}

		return pointValueTime;
	}

	private static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

}
