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
package com.serotonin.mango.rt.publish.pachube;

import java.util.Iterator;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.publish.PublishQueue;
import com.serotonin.mango.rt.publish.PublishQueueEntry;
import com.serotonin.mango.rt.publish.PublisherRT;
import com.serotonin.mango.vo.publish.pachube.PachubePointVO;

public class PachubePublishQueue extends PublishQueue<PachubePointVO> {
    public PachubePublishQueue(PublisherRT<PachubePointVO> owner, int warningSize) {
        super(owner, warningSize);
    }

    @Override
    public synchronized void add(PachubePointVO vo, PointValueTime pvt) {
        // Remove duplicate points.
        Iterator<PublishQueueEntry<PachubePointVO>> iter = queue.iterator();
        while (iter.hasNext()) {
            PachubePointVO entry = iter.next().getVo();
            if (entry.getFeedId() == vo.getFeedId() && entry.getDataStreamId() == vo.getDataStreamId())
                iter.remove();
        }

        super.add(vo, pvt);
    }
}
