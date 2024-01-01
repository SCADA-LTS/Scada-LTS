package utils;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.AnnotatedPointValueTime;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.bean.LongPair;
import com.serotonin.mango.vo.bean.PointHistoryCount;
import org.scada_lts.dao.IUserDAO;
import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.dao.model.point.PointValueAdnnotation;
import org.scada_lts.dao.pointvalues.IPointValueDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class PointValueDAOMemory implements IPointValueDAO {

    private final Map<Long, PointValueTime> pointValues;
    private final Map<Long, AnnotatedPointValueTime> pointValuesAnno;
    private static final AtomicLong serial = new AtomicLong(222);
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
            PointValueTime pointValueTime = pointValues.get(id);
            User user = usersDAO.getUser(annotatedPointValueTime.getSourceId());
            annotatedPointValueTime.setSourceDescriptionArgument(user.getUsername());
            if(pointValueTime.getValue() instanceof AlphanumericValue) {
                return annotatedPointValueTime;
            } else {
                AnnotatedPointValueTime annotatedPointValueTime1 = new AnnotatedPointValueTime(pointValueTime.getValue(), pointValueTime.getTime(), SetPointSource.Types.USER, user.getId());
                annotatedPointValueTime1.setSourceDescriptionArgument(user.getUsername());
                return annotatedPointValueTime1;
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

    public synchronized void clear() {
        pointValuesAnno.clear();
        pointValues.clear();
    }

    @Override
    public List<PointValue> findAll() {
        return null;
    }

    @Override
    public PointValue findById(Object[] pk) {
        return null;
    }

    @Override
    public List<PointValue> findByIdAndTs(long id, long ts) {
        return null;
    }

    @Override
    public List<PointValue> filtered(String filter, Object[] argsFilter, long limit) {
        return null;
    }

    @Override
    public Object[] create(PointValue entity) {
        return new Object[0];
    }

    @Override
    public Object[] createNoTransaction(int pointId, int dataType, double dvalue, long time) {
        return new Object[0];
    }

    @Override
    public void executeBatchUpdateInsert(List<Object[]> params) {

    }

    @Override
    public Long getInceptionDate(int dataPointId) {
        return null;
    }

    @Override
    public long dateRangeCount(int dataPointId, long from, long to) {
        return 0;
    }

    @Override
    public LongPair getStartAndEndTime(List<Integer> dataPointIds) {
        return null;
    }

    @Override
    public long getStartTime(List<Integer> dataPointIds) {
        return 0;
    }

    @Override
    public long getEndTime(List<Integer> dataPointIds) {
        return 0;
    }

    @Override
    public List<Long> getFiledataIds() {
        return null;
    }

    @Override
    public Long getLatestPointValue(int dataPointId) {
        return null;
    }

    @Override
    public double applyBounds(double value) {
        return 0;
    }

    @Override
    public long deletePointValuesBeforeWithOutLast(int dataPointId, long time) {
        return 0;
    }

    @Override
    public long deletePointValuesBeforeWithOutLastTwo(int dataPointId, long time) {
        return 0;
    }

    @Override
    public long deletePointValue(int dataPointId) {
        return 0;
    }

    @Override
    public long deleteAllPointData() {
        return 0;
    }

    @Override
    public long deletePointValuesWithMismatchedType(int dataPointId, int dataType) {
        return 0;
    }

    @Override
    public long deletePointValuesWithValueLimit(int dataPointId, int limit) {
        return 0;
    }

    @Override
    public long getMinTs(int dataPointId) {
        return 0;
    }

    @Override
    public long getMaxTs(int dataPointId) {
        return 0;
    }

    @Override
    public List<PointHistoryCount> getTopPointHistoryCounts() {
        return null;
    }

    @Override
    public void updateAnnotation(int userId) {

    }
}
