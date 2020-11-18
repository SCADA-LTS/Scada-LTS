package org.scada_lts.dao.alarms;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    public NotificationService(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    /* --- --- --- --- ---- */
    /* --- CRUD methods --- */
    /* --- --- --- --- ---- */

    public List<Scheduler> getAllSchedulers() {return  notificationDAO.getAllSchedulers();}

    public Scheduler getSchedulerById(Long id) {
        return notificationDAO.getSchedulerById(id);
    }

    public Map<String, Long> getSchedulerRawData(Long id) {
        return notificationDAO.getSchedulerRawData(id);
    }

    public Scheduler createScheduler(Long rangeId, Long notificationId) {
        return notificationDAO.createScheduler(rangeId, notificationId);
    }

    public Scheduler updateScheduler(Long schedulerId, Long rangeId, Long notificationId) {
        return notificationDAO.updateScheduler(schedulerId, rangeId, notificationId);
    }

    public void deleteScheduler(Long schedulerId) {
        notificationDAO.deleteScheduler(schedulerId);
    }

    public List<Range> getAllRanges() {
        return notificationDAO.getAllRanges();
    }

    public Range getRangeById(Long id) {
        return notificationDAO.getRangeById(id);
    }

    public Range insertRange(Range range) {
        return notificationDAO.insertRange(range);
    }

    public Range updateRange(Range range) {
        return notificationDAO.updateRange(range);
    }

    public void deleteRange(Long id) {
        notificationDAO.deleteRange(id);
    }

    public List<Notification> getAllNotifications() {
        return notificationDAO.getAllNotifications();
    }

    public Notification insertNotification(Notification notification) {
        return notificationDAO.insertNotification(notification);
    }

    public Notification updateNotification(Notification notification) {
        return notificationDAO.updateNotification(notification);
    }

    public void deleteNotification(Long notificationId) {
        notificationDAO.deleteNotification(notificationId);
    }

    public List<Scheduler> getDataPointSchedulers(Long dataPointId) {
        return notificationDAO.getDataPointSchedulers(dataPointId);
    }

    public List<SchedulerUserData> getAllSchedulerUserData(Long userId) {
        return notificationDAO.getAllSchedulersForUser(userId);
    }

    public List<Long> getDataPointIdsByScheduler(Long schedulerId) {
        return notificationDAO.getDataPointsBySchedulerId(schedulerId);
    }

    public List<Long> getUserIdsByScheduler(Long schedulerId) {
        return notificationDAO.getUsersBySchedulerId(schedulerId);
    }

    /* --- --- --- --- --- --- --- --- --- */
    /* ---  Creating simple relations  --- */
    /* --- --- --- --- --- --- --- --- --- */

    public void bindSchedulerWithDataPoint(Long schedulerId, Long dataPointId) {
        notificationDAO.bindSchedulerWithDataPoint(schedulerId, dataPointId);
    }

    public void bindSchedulerWithUser(Long schedulerId, Long userId) {
        notificationDAO.bindSchedulerWithUser(schedulerId, userId);
    }

    /* --- --- --- --- --- --- --- --- --- */
    /* ---  Creating complex relations --- */
    /* --- --- --- --- --- --- --- --- --- */

    public void createSchedulerWithUserAndDatapoint(Long notificationId, Long rangeId, Long userId, Long datapointId) {
        Scheduler scheduler = notificationDAO.createScheduler(rangeId, notificationId);
        notificationDAO.bindSchedulerWithDataPoint(scheduler.getId(), datapointId);
        notificationDAO.bindSchedulerWithUser(scheduler.getId(), userId);
    }

    public void createSchedulerWithUserAndDatapoint(Notification notification, Long rangeId, Long userId, Long datapointId) {
        Notification inserted = insertNotification(notification);
        createSchedulerWithUserAndDatapoint(inserted.getId(), rangeId, userId, datapointId);
    }

    public void acknowledgeNotification(Long schedulerId) {
        Notification notification = notificationDAO.getNotificationBySchedulerId(schedulerId);
        notificationDAO.acknowledgeNotification(notification.getId());
    }





}
