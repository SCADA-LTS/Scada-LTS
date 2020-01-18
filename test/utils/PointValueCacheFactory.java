package utils;

import com.serotonin.mango.db.dao.IPointValueDao;
import com.serotonin.mango.rt.dataImage.*;

public class PointValueCacheFactory {

    public static PointValueCache newPointValueCacheV2(int dataPointId, int defaultSize, IPointValueDao dao) {
        return new PointValueCache(dataPointId,defaultSize,dao);
    }

}
