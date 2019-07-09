/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.serotonin.mango.dao_cache;

import br.org.scadabr.db.dao.FlexProjectDao;
import br.org.scadabr.db.dao.ScriptDao;
import br.org.scadabr.db.dao.UsersProfileDao;
import com.serotonin.mango.db.dao.*;

/**
 *
 * Responsibility :
 *
 * To have access to Dao, you have here it, by one place on Scada application
 *
 *
 * @autor hyski.mateusz@gmail.com (SoftQ) on 28.06.19
 *
 */

public class DaoInstances {

    public static final ViewDao ViewDao =new ViewDao();
    public static final ScriptDao ScriptDao=new ScriptDao();
    public static final DataPointDao DataPointDao=new DataPointDao();
    public static final UserDao UserDao=new UserDao();
    public static final UsersProfileDao UsersProfileDao=new UsersProfileDao();
    public static final DataSourceDao DataSourceDao=new DataSourceDao();
    public static final WatchListDao WatchListDao=new WatchListDao();
    public static final EventDao EventDao=new EventDao();
    public static final ScheduledEventDao ScheduledEventDao=new ScheduledEventDao();
    public static final ReportDao ReportDao=new ReportDao();
    public static final PointLinkDao PointLinkDao=new PointLinkDao();
    public static final MailingListDao MailingListDao=new MailingListDao();
    public static final MaintenanceEventDao MaintenanceEventDao=new MaintenanceEventDao();
    public static final CompoundEventDetectorDao CompoundEventDetectorDao=new CompoundEventDetectorDao();
    public static final PublisherDao PublisherDao=new PublisherDao();
    public static final PointValueDao PointValueDao=new PointValueDao();
    public static final FlexProjectDao FlexProjectDao=new FlexProjectDao();

}
