package org.scada_lts.dao.model.point;

@Deprecated
public class PointValueTypeOfREST {

	public final static int TYPE_UNKNOWN = 0;
	public final static int TYPE_BINARY = 1;
	public final static int TYPE_MULTISTATE = 2;
	public final static int TYPE_DOUBLE = 3;
	public final static int TYPE_STRING = 4;

	public static boolean validPointValueType(int pointValueType) {
		switch (pointValueType) {
			case TYPE_UNKNOWN:
			case TYPE_BINARY:
			case TYPE_MULTISTATE:
			case TYPE_DOUBLE:
			case TYPE_STRING:
				return true;
			default:
				return false;
		}
	}
}
