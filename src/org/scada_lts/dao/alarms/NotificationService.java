package org.scada_lts.dao.alarms;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    public NotificationService(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    public List<Scheduler> getSchedulerById(Long id) {
        return notificationDAO.getSchedulerById(id);
    }

    public List<Range> getAllRanges() {
        return notificationDAO.getAllRanges();
    }

    public Range insertRange(Range range) {
        return notificationDAO.insertRange(range);
    }

    public List<Notification> getAllNotifications() {
        return notificationDAO.getAllNotifications();
    }

    public Notification insertNotification(Notification notification) {
        return notificationDAO.insertNotification(notification);
    }

    public void bindSchedulerWithDataPoint(Long schedulerId, Long dataPointId) {
        notificationDAO.bindSchedulerWithDataPoint(schedulerId, dataPointId);
    }

    public List<Scheduler> getDataPointSchedulers(Long dataPointId) {
        return notificationDAO.getDataPointSchedulers(dataPointId);
    }



}
