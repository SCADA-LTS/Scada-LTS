package utils;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.IUserDAO;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class UsersDAOMemory implements IUserDAO {

    private final Map<Integer, User> users = new HashMap<>();
    private final static AtomicInteger id = new AtomicInteger();


    @Override
    public List<Integer> getAll() {
        return new ArrayList<>(users.keySet());
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> getActiveUsers() {
        return users.values().stream().filter(a -> !a.isDisabled()).collect(Collectors.toList());
    }

    @Override
    public User getUser(String username) {
        return users.values().stream().filter(a -> a.getUsername().equals(username)).findAny().orElse(null);
    }

    @Override
    public User getUser(int id) {
        return users.values().stream().filter(a -> a.getId() == id).findAny().orElse(null);
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void updateHomeUrl(int userId, String homeUrl) {
        users.values().stream().filter(a -> a.getId() == userId).peek(a -> a.setHomeUrl(homeUrl)).close();
    }

    @Override
    public void updateUserPassword(int userId, String newPassword) {
        users.values().stream().filter(a -> a.getId() == userId).peek(a -> a.setPassword(newPassword)).close();
    }

    @Override
    public void updateLogin(int userId) {
        users.values().stream().filter(a -> a.getId() == userId).peek(a -> a.setLastLogin(System.currentTimeMillis())).close();
    }

    @Override
    public void delete(int userId) {
        users.remove(userId);
    }

    @Override
    public int insert(User user) {
        if(users.containsKey(user.getId())) {
            throw new IllegalStateException();
        }
        int userId = id.incrementAndGet();
        user.setId(userId);
        users.put(userId, user);
        return userId;
    }

    @Override
    public void updateUserLang(int userId, String lang) {
        users.values().stream().filter(a -> a.getId() == userId).peek(a -> a.setLang(lang)).close();
    }
}
