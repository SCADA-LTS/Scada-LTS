/*
 * (c) 2017 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.permissions.model;

import java.io.Serializable;

/**
 * @author Grzegorz Bylica grzegorz.bylica@gmail.com
 **/
public class EntryDto implements Serializable {

    private static final long serialVersionUID = -3334611742082372612L;
    private Long id;
    private byte mask;

    public EntryDto() {
        //
    }

    public EntryDto(Long id, byte mask) {
        this.id = id;
        this.mask = mask;
    }

    public Integer getId() {
        return new Integer(Math.toIntExact(id));
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte getMask() {
        return mask;
    }

    public void setMask(byte mask) {
        this.mask = mask;
    }

    public EntryDto getSelf() {
        return this;
    }

    @Override
    public String toString() {
        return "EntryDto{" +
                "id=" + id +
                ", mask=" + mask +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntryDto entryDto = (EntryDto) o;

        if (mask != entryDto.mask) return false;
        return id != null ? id.equals(entryDto.id) : entryDto.id == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) mask;
        return result;
    }
}
