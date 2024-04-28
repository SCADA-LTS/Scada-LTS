package utils;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.AnnotatedPointValueTime;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.event.handlers.SetPointHandlerRT;
import com.serotonin.mango.rt.link.PointLinkRT;
import com.serotonin.mango.vo.AnonymousUser;
import com.serotonin.mango.vo.bean.LongPair;
import com.serotonin.mango.vo.bean.PointHistoryCount;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.link.PointLinkVO;
import org.scada_lts.dao.IUserDAO;
import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.dao.pointvalues.IPointValueDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.serotonin.mango.util.AnnotatedPointValueUtils.createAnnotatedPointValueTime;

public class PointValueDAOMemory implements IPointValueDAO {

    private final Map<Long, PointValueTime> pointValues;
    private final Map<Long, AnnotatedPointValueTime> pointValuesAnno;
    private static final AtomicLong serial = new AtomicLong(222);
    private static final AtomicLong annoSerial = new AtomicLong(111);
    private final IUserDAO usersDAO;


    public PointValueDAOMemory(IUserDAO usersDAO) {
        this.pointValues = new HashMap<>();
        this.pointValuesAnno = new HashMap<>();
        this.usersDAO = usersDAO;
    }

    public synchronized Object[] create(final int pointId, final int dataType, final double dvalue, final long time) {
        Object value = convert(dataType, dvalue);
        PointValueTime pointValueTime = new PointValueTime(MangoValue.stringToValue(String.valueOf(value), dataType), time);
        long id = serial.getAndIncrement();
        pointValues.put(id, pointValueTime);
        return new Object[] {id};
    }


    @Override
    public int createAnnotation(long pointValueId, String textPointValueShort, String textPointValueLong, int sourceType, int sourceId) {
        PointValueTime pointValueTime = pointValues.get(pointValueId);
        long time = pointValueTime.getTime();
        MangoValue mangoValue = pointValueTime.getValue();
        if(textPointValueShort != null) {
            mangoValue = MangoValue.objectToValue(textPointValueShort);
        } else if(textPointValueLong != null) {
            mangoValue = MangoValue.objectToValue(textPointValueLong);
        }
        pointValuesAnno.put(pointValueId, new AnnotatedPointValueTime(mangoValue, time, sourceType, sourceId));
        return 0;
    }

    @Override
    public synchronized PointValueTime getPointValue(long id) {
        if(pointValuesAnno.containsKey(id)) {
            AnnotatedPointValueTime annotatedPointValueTime = pointValuesAnno.get(id);
            if(annotatedPointValueTime.getSourceType() == SetPointSource.Types.USER) {
                annotatedPointValueTime.setSourceDescriptionArgument(usersDAO.getUser(annotatedPointValueTime.getSourceId()).getUsername());
            }
            return annotatedPointValueTime;
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

    public synchronized void clear() {
        pointValuesAnno.clear();
        pointValues.clear();
    }

    @Override
    public List<PointValue> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PointValue findById(Object[] pk) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PointValue> findByIdAndTs(long id, long ts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PointValue> filtered(String filter, Object[] argsFilter, long limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] create(PointValue entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] createNoTransaction(int pointId, int dataType, double dvalue, long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void executeBatchUpdateInsert(List<Object[]> params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getInceptionDate(int dataPointId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long dateRangeCount(int dataPointId, long from, long to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LongPair getStartAndEndTime(List<Integer> dataPointIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getStartTime(List<Integer> dataPointIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getEndTime(List<Integer> dataPointIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Long> getFiledataIds() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getLatestPointValue(int dataPointId) {
        PointValueTime pointValue = getPointValue(dataPointId);
        if(pointValue == null)
            return null;
        return pointValue.getTime();
    }

    @Override
    public double applyBounds(double value) {
        if (Double.isNaN(value))
            return 0;
        if (value == Double.POSITIVE_INFINITY)
            return Double.MAX_VALUE;
        if (value == Double.NEGATIVE_INFINITY)
            return -Double.MAX_VALUE;

        return value;
    }

    @Override
    public long deletePointValuesBeforeWithOutLast(int dataPointId, long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long deletePointValuesBeforeWithOutLastTwo(int dataPointId, long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long deletePointValue(int dataPointId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long deleteAllPointData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long deletePointValuesWithMismatchedType(int dataPointId, int dataType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long deletePointValuesWithValueLimit(int dataPointId, int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getMinTs(int dataPointId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getMaxTs(int dataPointId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PointHistoryCount> getTopPointHistoryCounts() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAnnotation(int userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PointValueTime getPointValueBefore(int dataPointId, long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PointValueTime getPointValueAt(int dataPointId, long time) {
        throw new UnsupportedOperationException();
    }
}
