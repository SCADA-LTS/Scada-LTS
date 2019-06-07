package br.org.scadabr.db.scenarios;

import java.util.LinkedList;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.SystemSettingsDAO;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;

public class ScenarioWithAdministrador extends DatalessDatabaseScenario {

	@Override
	public void setupScenario(DatabaseAccess database) {
		super.setupScenario(database);

		User user = new User();
		user.setId(Common.NEW_ID);
		user.setUsername("admin");
		user.setPassword(Common.encrypt("admin"));
		user.setEmail("admin@yourMangoDomain.com");
		user.setHomeUrl("");
		user.setPhone("");
		user.setAdmin(true);
		user.setDisabled(false);
		user.setDataSourcePermissions(new LinkedList<Integer>());
		user.setDataPointPermissions(new LinkedList<DataPointAccess>());
		new UserDao().saveUser(user);

		new SystemSettingsDAO().setValue(
				SystemSettingsDAO.DATABASE_SCHEMA_VERSION, Common.getVersion());
	}

}
