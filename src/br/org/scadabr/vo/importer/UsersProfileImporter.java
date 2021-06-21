package br.org.scadabr.vo.importer;

import br.org.scadabr.api.exception.DAOException;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;

import com.serotonin.json.*;
import com.serotonin.mango.Common;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.dwr.beans.ImportTask;
import com.serotonin.web.dwr.DwrResponseI18n;
import org.scada_lts.mango.service.*;

import java.util.List;
import java.util.ResourceBundle;

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

	public void importUsersProfile(JsonObject profileJson, DwrResponseI18n response,
								   JsonReader reader, ImportTask task) throws DAOException, JsonException {
		ResourceBundle bundle = Common.getBundle();
		String usersProfileXid = profileJson.getString("xid");
		try {
			UsersProfileVO newProfile = reader.readObject(profileJson.toJsonObject(), UsersProfileVO.class);
			UsersProfileVO oldProfile = usersProfileService.getUserProfileByXid(newProfile.getXid());
			if(oldProfile != null) {
				newProfile.setId(oldProfile.getId());
				usersProfileService.updateProfile(newProfile);
				response.addGenericMessage("emport.profilePermission.prefix", newProfile.getXid(), bundle.getString("emport.saved"));
			} else {
				newProfile.setId(Common.NEW_ID);
				usersProfileService.saveUsersProfile(newProfile);
				response.addGenericMessage("emport.profilePermission.prefix", newProfile.getXid(), bundle.getString( "emport.added"));
			}

			List<User> usersOnProfile = getUsersOnProfile(task.getUsers(), newProfile, userService, response);
			for (User user : usersOnProfile) {
				usersProfileService.updateUsersProfile(user, newProfile);
				response.addGenericMessage("emport.profilePermission.prefix",  newProfile.getXid() + ", user: " + user.getUsername(), bundle.getString("emport.saved"));
			}

		} catch (LocalizableJsonException e) {
			response.addGenericMessage("emport.profilePermission.prefix", usersProfileXid, e.getMsg());
		} catch (JsonException e) {
			response.addGenericMessage("emport.profilePermission.prefix", usersProfileXid, task.getJsonExceptionMessage(e));
		}
	}
}
