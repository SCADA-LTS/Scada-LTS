package br.org.scadabr.db.utils;

import java.util.ArrayList;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.mango.service.UserService;

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

		new UserService().saveUser(user);
		return user;
	}

}
