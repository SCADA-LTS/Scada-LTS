package utils.mock;

import com.serotonin.mango.rt.dataImage.*;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.vo.DataPointVO;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChangePointValueDataPointRtMock extends DataPointRT implements IDataPointRT {

    private Iterator<PointValueTime> pointValueTimes;

    public ChangePointValueDataPointRtMock(List<PointValueTime> pointValueTimes) {
        super(null);
        this.pointValueTimes = pointValueTimes.iterator();
    }

    @Override
    public List<PointValueTime> getLatestPointValues(int limit) {
        return null;
    }

    @Override
    public void updatePointValue(PointValueTime newValue) {

    }

    @Override
    public void updatePointValue(PointValueTime newValue, boolean async) {

    }

    @Override
    public void setPointValue(PointValueTime newValue, SetPointSource source) {

    }

    @Override
    public PointValueTime getPointValue() {
        return pointValueTimes.next();
    }

    @Override
    public PointValueTime getPointValueBefore(long time) {
        return null;
    }

    @Override
    public List<PointValueTime> getPointValues(long since) {
        return null;
    }

    @Override
    public List<PointValueTime> getPointValuesBetween(long from, long to) {
        return null;
    }

    @Override
    public int getDataTypeId() {
        return 0;
    }

    @Override
    public PointValueCache getPointValueCache() {
        return null;
    }

    @Override
    public List<PointValueTime> getLatestPointValuesUsedForJunitTest(int limit) {
        return null;
    }

    @Override
    public void addCollectionIntoCache(PointValueTime pvt) {

    }

    @Override
    public PointValueTime getPointValueAt(long time) {
        return null;
    }

    @Override
    public void resetValues() {

    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public int getDataSourceId() {
        return 0;
    }

    @Override
    public void setAttribute(String key, Object value) {

    }

    @Override
    public Object getAttribute(String key) {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public void initializeHistorical() {

    }

    @Override
    public void terminateHistorical() {

    }

    @Override
    public <T extends PointLocatorRT> T getPointLocator() {
        return null;
    }

    @Override
    public DataPointVO getVO() {
        return null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void terminate() {

    }

    @Override
    public void scheduleTimeout(long fireTime) {

    }

    @Override
    public void joinTermination() {

    }

    @Override
    public void notifyWebSocketSubscribers(MangoValue message) {

    }

    @Override
    public String toString() {
        return "ChangePointValueDataPointRtMock{" +
                "pointValueTimes=" + pointValueTimes +
                '}';
    }
}
