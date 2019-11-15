/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.rt.dataImage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class maintains an ordered list of the most recent values for a data point. It will mirror values in the
 * database, but provide a much faster lookup for a limited number of values.
 * 
 * Because there is not a significant performance problem for time-based lookups, they are not handled here, but rather
 * are still handled by the database.
 * 
 * @author Matthew Lohbihler
 */
public class PointValueCache {
    private final int dataPointId;
    private final int defaultSize;
    private BridgeBetweenPointValueCacheAndPointValueDao bridgeBetweenPointValueCacheAndPointValueDao;

    /**
     * IMPORTANT: The list object should never be written to! The implementation here is for performance. Never call
     * methods like add() or remove() on the cache object. Further, since the cache object can be replaced from time to
     * time, always use a local copy of the variable for read purposes.
     */
    private LinkedList<PointValueTime>  cache = new LinkedList<PointValueTime>();
    private int maxSize = 10;

    public PointValueCache(int dataPointId, int defaultSize) {
        this.dataPointId = dataPointId;
        this.defaultSize = defaultSize;
        bridgeBetweenPointValueCacheAndPointValueDao = new BridgeBetweenPointValueCacheAndPointValueDao(dataPointId);

        if (defaultSize > 0)
            refreshCacheDependsOnGivenSize(defaultSize);
    }

    public void savePointValue(PointValueTime pvt, SetPointSource source, boolean logValue, boolean async) {
        if (logValue) {
            bridgeBetweenPointValueCacheAndPointValueDao.savePointValue(pvt,source,async);
        }
        if(cache.size()==maxSize){
            cache.removeLast();
            cache.addFirst(pvt);
        }
        else
            cache.add(pvt);

    }

    /**
     * Saves the given value to the database without adding it to the cache.
     */
    void logPointValueAsync(PointValueTime pointValue, SetPointSource source) {
        // Save the new value and get a point value time back that has the id and annotations set, as appropriate.
        bridgeBetweenPointValueCacheAndPointValueDao.logPointValueAsync(pointValue,source);
    }

    public PointValueTime getLatestPointValue() {
        if (maxSize == 0)
            refreshCacheDependsOnGivenSize(1);

        if (cache.size() > 0)
            return cache.get(0);

        return null;
    }

    public List<PointValueTime> getLatestPointValues(int limit) {
        if (maxSize < limit)
            refreshCacheDependsOnGivenSize(limit);

        if (limit == cache.size())
            return cache;

        if (limit > cache.size())
            limit = cache.size();

        return new ArrayList<PointValueTime>(cache.subList(0,limit));
    }

    private void refreshCacheDependsOnGivenSize(int size) {
        if (size > maxSize) {
            maxSize = size;
            if (size == 1) {
                // Performance thingy
                readOnlyOneRowWithMaxTSAndPutIntoCache();
            }
            else
                getPointValuesAndFillCacheDependingOnRowsLimit(size);
        }
    }

    /**
     * Never manipulate the contents of this list!
     */
    public List<PointValueTime> getCacheContents() {
        return cache;
    }

    public void reset() {

        int size = defaultSize;
        if (cache.size() < size)
            size = cache.size();

        maxSize = size;
    }

    private void readOnlyOneRowWithMaxTSAndPutIntoCache(){
        PointValueTime pointValueTime = bridgeBetweenPointValueCacheAndPointValueDao.getLatestPointValueFromDao();
        if (pointValueTime != null) {
            cache.addFirst(pointValueTime);
        }
    }

    private void getPointValuesAndFillCacheDependingOnRowsLimit(int size){
        cache = bridgeBetweenPointValueCacheAndPointValueDao.getDefinedLimitRowsOfLatestPointValues(size);
    }
}
