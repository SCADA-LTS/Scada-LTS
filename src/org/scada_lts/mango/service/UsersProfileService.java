package org.scada_lts.mango.service;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.UsersProfileDAO;

import java.util.Optional;

public class UsersProfileService {

    private static final Log LOG = LogFactory.getLog(UsersProfileService.class);

    private UsersProfileDAO usersProfileDAO;

    public UsersProfileService() {
        this.usersProfileDAO = new UsersProfileDAO(DAO.getInstance().getJdbcTemp());
    }

    public UsersProfileService(UsersProfileDAO usersProfileDAO) {
        this.usersProfileDAO = usersProfileDAO;
    }

    public Optional<UsersProfileVO> getUsersProfileByUser(User user) {
        return usersProfileDAO.selectUsersProfileByUserId(user.getId());
    }
}
