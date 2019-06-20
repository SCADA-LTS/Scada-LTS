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
import java.util.List;

import com.serotonin.mango.db.dao.PointValueDao;

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
    private final PointValueDao dao;

    /**
     * IMPORTANT: The list object should never be written to! The implementation here is for performance. Never call
     * methods like add() or remove() on the cache object. Further, since the cache object can be replaced from time to
     * time, always use a local copy of the variable for read purposes.
     */
    private List<PointValueTime> cache = new ArrayList<PointValueTime>();

    public PointValueCache(int dataPointId, int defaultSize) {
        this.dataPointId = dataPointId;
        this.defaultSize = defaultSize;
        dao = new PointValueDao();

        if (defaultSize > 0)
            refreshCache(defaultSize);
    }

    private int maxSize = 0;

    public void savePointValue(PointValueTime pvt, SetPointSource source, boolean logValue, boolean async) {
        if (logValue) {
            if (async)
                dao.savePointValueAsync(dataPointId, pvt, source);
            else
                pvt = dao.savePointValueSync(dataPointId, pvt, source);
        }

        List<PointValueTime> c = cache;
        List<PointValueTime> newCache = new ArrayList<PointValueTime>(c.size() + 1);
        newCache.addAll(c);

        // Insert the value in the cache.
        int pos = 0;
        if (newCache.size() == 0)
            newCache.add(pvt);
        else {
            while (pos < newCache.size() && newCache.get(pos).getTime() > pvt.getTime())
                pos++;
            if (pos < maxSize)
                newCache.add(pos, pvt);
        }

        // Check if we need to clean up the list
        while (newCache.size() > maxSize)
            newCache.remove(newCache.size() - 1);
        // if (newCache.size() > maxSize - 1)
        // newCache = new ArrayList<PointValueTime>(newCache.subList(0, maxSize));

        cache = newCache;
    }

    /**
     * Saves the given value to the database without adding it to the cache.
     */
    void logPointValueAsync(PointValueTime pointValue, SetPointSource source) {
        // Save the new value and get a point value time back that has the id and annotations set, as appropriate.
        dao.savePointValueAsync(dataPointId, pointValue, source);
    }

    public PointValueTime getLatestPointValue() {
        if (maxSize == 0)
            refreshCache(1);

        List<PointValueTime> c = cache;
        if (c.size() > 0)
            return c.get(0);

        return null;
    }

    public List<PointValueTime> getLatestPointValues(int limit) {
        if (maxSize < limit)
            refreshCache(limit);

        List<PointValueTime> c = cache;
        if (limit == c.size())
            return c;

        if (limit > c.size())
            limit = c.size();
        return new ArrayList<PointValueTime>(c.subList(0, limit));
    }

    private void refreshCache(int size) {
        if (size > maxSize) {
            maxSize = size;
            if (size == 1) {
                // Performance thingy
                PointValueTime pvt = dao.getLatestPointValue(dataPointId);
                if (pvt != null) {
                    List<PointValueTime> c = new ArrayList<PointValueTime>();
                    c.add(pvt);
                    cache = c;
                }
            }
            else
                cache = dao.getLatestPointValues(dataPointId, size);
        }
    }

    /**
     * Never manipulate the contents of this list!
     */
    public List<PointValueTime> getCacheContents() {
        return cache;
    }

    public void reset() {
        List<PointValueTime> c = cache;

        int size = defaultSize;
        if (c.size() < size)
            size = c.size();

        List<PointValueTime> nc = new ArrayList<PointValueTime>(size);
        nc.addAll(c.subList(0, size));

        maxSize = size;
        cache = c;
    }
}
