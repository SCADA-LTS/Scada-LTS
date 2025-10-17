package org.scada_lts.web.ws.beans;

import com.serotonin.mango.vo.User;

public class ScadaPrincipal  implements java.security.Principal {
	private final User user;
	
	public ScadaPrincipal(User user) {
		this.user = user;
	}
	
	public int getId() {
		if(user == null)
			return -1;
		return user.getId();
	}
	
	public boolean isAuthenticated() {
		return user != null;
	}
	
	public String getNameId() {
		return getName() + "[" + getId() + "]";
	}

	@Override
	public String getName() {
		if(user == null)
			return "anonymous";
		return user.getUsername();
	}

	@Override
	public int hashCode() {
		if(user != null)
			return user.hashCode();
		return 0;
	}

	public User getUser() {
		return user;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScadaPrincipal other = (ScadaPrincipal) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if(user != null)
			return "ScadaPrincipal [user=" + user.toString() + "]";
		return "ScadaPrincipal [user=<anonymous>]";
	}
}
