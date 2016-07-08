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
package com.serotonin.mango.web.dwr;

import java.util.ArrayList;
import java.util.List;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.PublisherDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.util.IntMessagePair;
import com.serotonin.mango.vo.publish.PublishedPointVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class PublisherListDwr extends BaseDwr {
    public DwrResponseI18n init() {
        DwrResponseI18n response = new DwrResponseI18n();

        List<IntMessagePair> translatedTypes = new ArrayList<IntMessagePair>();
        for (PublisherVO.Type type : PublisherVO.Type.values())
            translatedTypes.add(new IntMessagePair(type.getId(), new LocalizableMessage(type.getKey())));

        response.addData("types", translatedTypes);
        response.addData("publishers", new PublisherDao().getPublishers(new PublisherDao.PublisherNameComparator()));

        return response;
    }

    public DwrResponseI18n togglePublisher(int publisherId) {
        DwrResponseI18n response = new DwrResponseI18n();

        RuntimeManager runtimeManager = Common.ctx.getRuntimeManager();
        PublisherVO<? extends PublishedPointVO> publisher = runtimeManager.getPublisher(publisherId);

        publisher.setEnabled(!publisher.isEnabled());
        runtimeManager.savePublisher(publisher);

        response.addData("enabled", publisher.isEnabled());
        response.addData("id", publisherId);

        return response;
    }

    public int deletePublisher(int publisherId) {
        Common.ctx.getRuntimeManager().deletePublisher(publisherId);
        return publisherId;
    }
}
