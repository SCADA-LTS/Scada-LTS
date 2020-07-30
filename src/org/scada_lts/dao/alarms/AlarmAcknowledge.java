package org.scada_lts.dao.alarms;

/**
 *
 * @author kamil.jarmusik@gmail.com
 *
 */

public class AlarmAcknowledge {

    private long id;
    private String request;
    private String error;

    public AlarmAcknowledge() {
    }

    private AlarmAcknowledge(long id, String request, String error) {
        this.id = id;
        this.request = request;
        this.error = error;
    }

    public static AlarmAcknowledge requestOk(long id) {
        return new AlarmAcknowledge(id, "OK", "none");
    }


    public static AlarmAcknowledge requestFault(long id, String error) {
        return new AlarmAcknowledge(id, "FAULT", error == null || error.isEmpty() ? "Object with id=" + id + " do not exist" : error);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
