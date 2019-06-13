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
package com.serotonin.mango.rt.publish;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.vo.publish.PublishedPointVO;

/**
 * @author Matthew Lohbihler
 */
public class PublishQueue<T extends PublishedPointVO> {
    protected final LinkedList<PublishQueueEntry<T>> queue = new LinkedList<PublishQueueEntry<T>>();
    private final PublisherRT<T> owner;
    private final int warningSize;
    private boolean warningActive = false;

    public PublishQueue(PublisherRT<T> owner, int warningSize) {
        this.owner = owner;
        this.warningSize = warningSize;
    }

    public synchronized void add(T vo, PointValueTime pvt) {
        queue.add(new PublishQueueEntry<T>(vo, pvt));
        sizeCheck();
    }

    public synchronized void add(T vo, List<PointValueTime> pvts) {
        for (PointValueTime pvt : pvts)
            queue.add(new PublishQueueEntry<T>(vo, pvt));
        sizeCheck();
    }

    public synchronized PublishQueueEntry<T> next() {
        if (queue.size() == 0)
            return null;
        return queue.get(0);
    }

    public synchronized List<PublishQueueEntry<T>> get(int max) {
        if (queue.size() == 0)
            return null;

        int amt = max;
        if (amt > queue.size())
            amt = queue.size();

        return new ArrayList<PublishQueueEntry<T>>(queue.subList(0, amt));
    }

    public synchronized void remove(PublishQueueEntry<T> e) {
        queue.remove(e);
        sizeCheck();
    }

    public int getSize() {
        return queue.size();
    }

    private void sizeCheck() {
        if (warningActive) {
            if (queue.size() <= warningSize) {
                owner.deactivateQueueSizeWarningEvent();
                warningActive = false;
            }
        }
        else {
            if (queue.size() > warningSize) {
                owner.fireQueueSizeWarningEvent();
                warningActive = true;
            }
        }
    }
}
