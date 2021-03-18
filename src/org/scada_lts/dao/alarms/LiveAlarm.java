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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * class works as a DTO without DTO in end of class name
 *
 * That class calls , actually apiAlarmsLevel, same as a defined view in database.
 * Name of view is defined in flyway part of software -> org.scada_lts.dao.migration.mysql
 *
 *
 * @author hyski.mateusz@gmail.com
 * @update kamil.jarmusik@gmail.com
 *
 */

public class LiveAlarm {

    private int id;

    @JsonProperty("activation-time")
    private String activationTime;
    @JsonProperty("inactivation-time")
    private String inactivationTime;
    private String level;
    private String name;

    /**
     * Added pointId information so that additional information about the point can be get, e.g.:
     * 1. Point desc,
     * 2. Event text render
     **/
    private int dataPointId;

    /**
     * Point description
     */
    private String description;

    /**
     * Event text render
     */
    private String eventTextRender;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(String activationTime) {
        this.activationTime = activationTime;
    }

    public String getInactivationTime() {
        return inactivationTime;
    }

    public void setInactivationTime(String inactivationTime) {
        this.inactivationTime = inactivationTime;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDataPointId() {
        return dataPointId;
    }

    public void setDataPointId(int dataPointId) {
        this.dataPointId = dataPointId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventTextRender() {
        return eventTextRender;
    }

    public void setEventTextRender(String eventTextRender) {
        this.eventTextRender = eventTextRender;
    }
}
