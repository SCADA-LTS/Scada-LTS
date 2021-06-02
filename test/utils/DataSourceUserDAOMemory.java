package utils;


import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.dao.DataPointUserDAO;
import org.scada_lts.dao.DataSourceDAO;
import org.scada_lts.dao.UsersProfileDAO;

import java.util.Collections;
import java.util.List;

public class DataSourceUserDAOMemory extends DataSourceDAO {

    private final UsersProfileDAO usersProfileDAO;

    public DataSourceUserDAOMemory(UsersProfileDAO usersProfileDAO) {
        this.usersProfileDAO = usersProfileDAO;
    }

    @Override
    public List<Integer> selectDataSourcePermissionsByProfileId(int profileId) {
        UsersProfileVO profile = usersProfileDAO.selectProfiles(0, Integer.MAX_VALUE).stream().filter(a -> a.getId() == profileId).findAny().orElse(null);
        return profile == null ? Collections.emptyList() : profile.getDataSourcePermissions();
    }
}
