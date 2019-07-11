package br.org.scadabr.db.utils;

import java.util.ArrayList;

import org.scada_lts.mango.service.ServiceInstances;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;

public class TestUtils {

	public static User createUser() {
		User user = new User();
		user.setUsername("anUser");
		user.setPassword("password");
		user.setEmail("An email");
		user.setPhone("phone");
		user.setAdmin(true);
		user.setDisabled(false);
		user.setHomeUrl("url");
		user.setReceiveAlarmEmails(1);
		user.setReceiveOwnAuditEvents(true);
		user.setDataSourcePermissions(new ArrayList<Integer>());
		user.setDataPointPermissions(new ArrayList<DataPointAccess>());

		ServiceInstances.UserService.saveUser(user);
		return user;
	}

}
