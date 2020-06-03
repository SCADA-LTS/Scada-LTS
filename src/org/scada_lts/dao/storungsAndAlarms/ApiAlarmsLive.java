package org.scada_lts.dao.storungsAndAlarms;
/*
 * (c) 2020 hyski.mateusz@gmail.com
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
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */
class ApiAlarmsLive {

    private int id;

    private String
            activationTime,
            inactivationTime,
            level,
            name;

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

    public String toJSONObject(){
        try {
            return new JSONObject()
                    .put("id", getId())
                    .put("activation-time", getActivationTime())
                    .put("inactivation-time", getInactivationTime())
                    .put("level", getLevel())
                    .put("name", getName()).toString();
        }
        catch (JSONException e){
            e.getCause();
        }
        return null;
    }
}
