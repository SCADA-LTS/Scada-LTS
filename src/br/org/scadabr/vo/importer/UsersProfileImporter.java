package br.org.scadabr.vo.importer;

import br.org.scadabr.api.exception.DAOException;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;

import com.serotonin.json.*;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.dwr.beans.ImportTask;
import com.serotonin.web.dwr.DwrResponseI18n;
import org.scada_lts.mango.service.*;

import java.util.List;

import static br.org.scadabr.vo.usersProfiles.DeserializeUsersProfileUtils.*;


public class UsersProfileImporter {

	private final UsersProfileService usersProfileService;
	private final UserService userService;

	public UsersProfileImporter() {
		usersProfileService = new UsersProfileService();
		userService = new UserService();
	}

	public UsersProfileImporter(UsersProfileService usersProfileService, UserService userService) {
		this.usersProfileService = usersProfileService;
		this.userService = userService;
	}

	public void importUsersProfile(JsonObject profileJson,
								   DwrResponseI18n response, JsonReader reader, ImportTask task)
			throws DAOException, JsonException {

		UsersProfileVO newProfile = reader.readObject(profileJson.toJsonObject(), UsersProfileVO.class);
		UsersProfileVO oldProfile = usersProfileService.getUserProfileByXid(newProfile.getXid());
		if(oldProfile != null) {
			newProfile.setId(oldProfile.getId());
			usersProfileService.updateProfile(newProfile);
		} else {
			newProfile.setId(Common.NEW_ID);
			usersProfileService.saveUsersProfile(newProfile);
		}

		List<User> usersOnProfile = getUsersOnProfile(task.getUsers(), newProfile, userService, response);
		for(User user: usersOnProfile) {
			usersProfileService.updateUsersProfile(user, newProfile);
		}
	}
}
