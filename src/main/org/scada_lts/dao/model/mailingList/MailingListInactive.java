/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.dao.model.mailingList;

/**
 * MailingListInactive bean
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class MailingListInactive {

	private int mailingListId;
	private int inactiveInterval;

	public MailingListInactive() {

	}

	public MailingListInactive(int mailingListId, int inactiveInterval) {
		this.mailingListId = mailingListId;
		this.inactiveInterval = inactiveInterval;
	}

	public int getMailingListId() {
		return mailingListId;
	}

	public void setMailingListId(int mailingListId) {
		this.mailingListId = mailingListId;
	}

	public int getInactiveInterval() {
		return inactiveInterval;
	}

	public void setInactiveInterval(int inactiveInterval) {
		this.inactiveInterval = inactiveInterval;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		MailingListInactive that = (MailingListInactive) o;

		if (mailingListId != that.mailingListId) {
			return false;
		}
		return inactiveInterval == that.inactiveInterval;

	}

	@Override
	public int hashCode() {
		int result = mailingListId;
		result = 31 * result + inactiveInterval;
		return result;
	}

	@Override
	public String toString() {
		return "MailingListInactive{" +
				"mailingListId=" + mailingListId +
				", inactiveInterval=" + inactiveInterval +
				'}';
	}
}
