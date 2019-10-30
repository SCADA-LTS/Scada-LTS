package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.db.dao.PointValueDao;

class PointValueCacheCooperateWithDao {

    private final PointValueDao dao = new PointValueDao();
    private final  int dataPointId;

    PointValueCacheCooperateWithDao( int dataPointId){
        this.dataPointId = dataPointId;
    }
    PointValueTime savePointValue(PointValueTime pvt, SetPointSource source,   boolean async){
        if (async)
            dao.savePointValueAsync(dataPointId, pvt, source);
        else
            pvt = dao.savePointValueSync(dataPointId, pvt, source);

        return pvt;
    }
}
