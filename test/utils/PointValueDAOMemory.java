package utils;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.AnnotatedPointValueTime;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.scada_lts.dao.model.point.PointValueAdnnotation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class PointValueDAOMemory {

    private final Map<Long, PointValueTime> pointValues = new HashMap<>();
    private final Map<Long, PointValueAdnnotation> pointValuesAnno = new HashMap<>();
    private static final AtomicLong serial = new AtomicLong(222);

    public Object[] create(final int pointId, final int dataType, final double dvalue, final long time) {
        Object value = convert(dataType, dvalue);
        PointValueTime pointValueTime = new PointValueTime(MangoValue.stringToValue(String.valueOf(value), dataType), time);
        long id = serial.getAndIncrement();
        pointValues.put(id, pointValueTime);
        return new Object[] {id};
    }

    public Object[] create(PointValueAdnnotation pointValueAdnnotation) {
        pointValuesAnno.put(pointValueAdnnotation.getPointValueId(), pointValueAdnnotation);
        return new Object[] {0};
    }

    public PointValueTime getPointValue(long id) {
        if(pointValuesAnno.containsKey(id)) {
            PointValueTime pointValueTime = pointValues.get(id);
            if(pointValueTime.getValue().getDataType() == DataTypes.ALPHANUMERIC) {
                PointValueAdnnotation pointValueAdnnotation = pointValuesAnno.get(id);
                return new AnnotatedPointValueTime(
                        new AlphanumericValue(pointValueAdnnotation.getTextPointValueShort() == null ?
                                pointValueAdnnotation.getTextPointValueLong() : pointValueAdnnotation.getTextPointValueShort()),
                        pointValueTime.getTime(), pointValueAdnnotation.getSourceType(), (int) pointValueAdnnotation.getSourceId());
            }
        }
        return pointValues.get(id);
    }

    private Object convert(int dataType, double dvalue) {
        Object value;
        if(dataType == DataTypes.NUMERIC) {
            value = dvalue;
        } else {
            value = (int) dvalue;
        }
        return value;
    }

    public void clear() {
        pointValuesAnno.clear();
        pointValues.clear();
    }
}
