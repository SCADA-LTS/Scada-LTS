package org.scada_lts.login;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.User;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Set;

public interface ILoggedUsers {
    User addUser(User user, HttpSession session);
    void updateUser(User user);
    void updateUsers(UsersProfileVO profile);
    User removeUser(User user, HttpSession session);
    Set<Integer> getUserIds();
    Collection<User> getUsers();
    User getUser(int id);
}
