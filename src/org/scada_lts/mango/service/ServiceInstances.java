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
package org.scada_lts.mango.service;

/**
 *
 *
 * Bridge to DAO as Service's
 * To have access to DAO, you have here it, by one place on Scada application.
 *
 *
 * @autor hyski.mateusz@gmail.com (SoftQ) on 28.06.19
 *
 */
public class ServiceInstances {

    public static final CompoundEventDetectorService CompoundEventDetectorService = new CompoundEventDetectorService();
    public static final WatchListService WatchListService = new WatchListService();
    public static final ViewService ViewService = new ViewService();
    public static final FlexProjectService FlexProjectService = new FlexProjectService();
    public static final UserService UserService = new UserService();
    //public static final UsersProfileService UsersProfileService = new UsersProfileService();
    public static final ScheduledEventService ScheduledEventService = new ScheduledEventService();
    public static final ReportService ReportService = new ReportService();
    public static final PublisherService PublisherService = new PublisherService();
    public static final PointValueService PointValueService = new PointValueService();
    public static final PointLinkService PointLinkService = new PointLinkService();
    public static final MaintenanceEventService MaintenanceEventService = new MaintenanceEventService();
    public static final MailingListService MailingListService = new MailingListService();
    public static final EventService EventService = new EventService();
    public static final DataSourceService DataSourceService = new DataSourceService();
    public static final DataPointService DataPointService = new DataPointService();
    //public static final ScriptService ScriptService = new ScriptService();

}
