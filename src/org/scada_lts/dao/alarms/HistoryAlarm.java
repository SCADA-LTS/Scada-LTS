/*
 * Copyright (C) 2020 Abil'I.T.
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
 * Create by at Mateusz Hyski
 *
 * @author Mateusz Hyski on behalf of Abil'I.T. (code owner) email: sdt@abilit.eu
 * @author Kamil Jarmusik on behalf of Abil'I.T. (code owner) email: sdt@abilit.eu
 *
 */

public class HistoryAlarm {

    private String activeTime;
    private String inactiveTime;
    private String acknowledgeTime;
    private String name;
    private int level;

    public String getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
    }

    public String getInactiveTime() {
        return inactiveTime;
    }

    public void setInactiveTime(String inactiveTime) {
        this.inactiveTime = inactiveTime;
    }

    public String getAcknowledgeTime() {
        return acknowledgeTime;
    }

    public void setAcknowledgeTime(String acknowledgeTime) {
        this.acknowledgeTime = acknowledgeTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
