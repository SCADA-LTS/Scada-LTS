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
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONException;
import org.json.JSONObject;
/**
 *
 * class works as a DTO without DTO in end of class name
 *
 * That class calls , actually apiAlarmsLevel, same as a defined view in database.
 * Name of view is defined in flyway part of software -> org.scada_lts.dao.migration.mysql
 *
 *
 * @author hyski.mateusz@gmail.com
 */
public class ApiAlarmsLive {

    private int id;

    @JsonProperty("activation-time")
    private String activationTime;
    @JsonProperty("inactivation-time")
    private String inactivationTime;
    private String level;
    private String name;

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
/*
    public JSONObject toJSONObject(){
        try {
            return new JSONObject()
                    .put("id", getId())
                    .put("activation-time", getActivationTime())
                    .put("inactivation-time", getInactivationTime())
                    .put("level", String.valueOf( getLevel() ) )
                    .put("name", getName());
        }
        catch (JSONException e){
            try {
                // if exception occurs as a result of invoking rest service , is received also message about exception
                return new JSONObject().put("error during build JSONObject", e.getMessage());
            }
            catch (JSONException exception){
                e.getMessage();
            }
        }
        return null;
    }*/
}
