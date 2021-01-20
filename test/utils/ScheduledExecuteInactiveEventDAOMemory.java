package utils;

import org.scada_lts.dao.event.ScheduledExecuteInactiveEvent;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ScheduledExecuteInactiveEventDAOMemory implements ScheduledExecuteInactiveEventDAO {

    private final List<ScheduledExecuteInactiveEvent> relations = new CopyOnWriteArrayList<>();

    @Override
    public List<ScheduledExecuteInactiveEvent> select(int limit) {
        return relations.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduledExecuteInactiveEvent> selectByMailingListId(int mailingListId,
                                                                     List<ScheduledExecuteInactiveEvent> exclude,
                                                                     int limit) {
        return relations.stream()
                .filter(a -> a.getMailingListId() == mailingListId)
                .limit(limit)
                .filter(a -> !exclude.contains(a))
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduledExecuteInactiveEvent> selectByMailingListId(int mailingListId) {
        return relations.stream()
                .filter(a -> a.getMailingListId() == mailingListId)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduledExecuteInactiveEvent> selectByMailingListId(int mailingListId, int limit) {
        return relations.stream()
                .filter(a -> a.getMailingListId() == mailingListId)
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public ScheduledExecuteInactiveEvent insert(ScheduledExecuteInactiveEvent scheduledInactiveCommunicationEvent) {
        if(relations.contains(scheduledInactiveCommunicationEvent))
            throw new IllegalArgumentException("Duplicate!");
        relations.add(scheduledInactiveCommunicationEvent);
        return scheduledInactiveCommunicationEvent;
    }

    @Override
    public void delete(ScheduledExecuteInactiveEvent scheduledInactiveCommunicationEvent) {
        if(!relations.contains(scheduledInactiveCommunicationEvent))
            throw new IllegalArgumentException("No exists!");
        relations.remove(scheduledInactiveCommunicationEvent);
    }
}
