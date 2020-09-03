/*
 * (c) 2020 hyski.mateusz@gmail.com, kamil.jarmusik@gmail.com
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
        return new AlarmAcknowledge(id, "FAULT", error);
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
