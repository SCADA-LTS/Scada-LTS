package org.scada_lts.dao.alarms;

import org.scada_lts.dao.DAO;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

public interface PlcAlarmsDAO {
    List<LiveAlarm> getLiveAlarms(int offset, int limit) throws DataAccessException;

    List<HistoryAlarm> getHistoryAlarms(String dayDate, String regex, int offset, int limit) throws DataAccessException;

    int setAcknowledgeTime(int id) throws DataAccessException;

    Optional<Integer> getUniquenessToken(int id) throws DataAccessException;

    static PlcAlarmsDAO getInstance() {
        return new PlcAlarmsDAOImpl(DAO.getInstance().getJdbcTemp());
    }
}
