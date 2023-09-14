package org.scada_lts.login;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.serotonin.mango.Common.SESSION_USER;

public class LoggedUsers implements ILoggedUsers {

    private static final Log LOG = LogFactory.getLog(LoggedUsers.class);

    public LoggedUsers() {}

    private final Map<Integer, User> loggedUsers = new ConcurrentHashMap<>();
    private final Map<Integer, List<HttpSession>> loggedSessions = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ThreadLocal<String> blocked = new ThreadLocal<>();

    @Override
    public User addUser(User user, HttpSession session) {
        lock.writeLock().lock();
        try {
            if("setAttribute".equals(blocked.get())) {
                LOG.warn("blocked addUser: " + LoggingUtils.userInfo(user) + ", thread: " + Thread.currentThread().getName());
                return null;
            }
            loggedSessions.putIfAbsent(user.getId(), new CopyOnWriteArrayList<>());
            if(!loggedSessions.get(user.getId()).contains(session)) {
                loggedSessions.get(user.getId()).add(session);
                return loggedUsers.put(user.getId(), user);
            }
            LOG.warn("session exists for: " + LoggingUtils.userInfo(user) + ", thread: " + Thread.currentThread().getName());
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void updateUser(User user) {
        lock.writeLock().lock();
        try {
            update(user, loggedUsers, loggedSessions, blocked);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void updateUsers(UsersProfileVO profile) {
        lock.writeLock().lock();
        try {
            for(User user: new ArrayList<>(loggedUsers.values())) {
                if(user.getUserProfile() == profile.getId()) {
                    profile.apply(user);
                    update(user, loggedUsers, loggedSessions, blocked);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public User removeUser(User user, HttpSession session) {
        lock.writeLock().lock();
        try {
            if("setAttribute".equals(blocked.get())) {
                LOG.warn("blocked removeUser: " + LoggingUtils.userInfo(user) + ", thread: " + Thread.currentThread().getName());
                return null;
            }
            if (loggedSessions.get(user.getId()) == null || (loggedSessions.get(user.getId()).remove(session)
                    && loggedSessions.get(user.getId()).isEmpty())) {
                loggedSessions.remove(user.getId());
                return loggedUsers.remove(user.getId());
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Set<Integer> getUserIds() {
        lock.readLock().lock();
        try {
            return loggedUsers.keySet();
        } finally {
            lock.readLock().unlock();
        }
    }

    private static void update(User user, Map<Integer, User> loggedUsers,
                               Map<Integer, List<HttpSession>> loggedSessions,
                               ThreadLocal<String> blocked) {
        User loggedUser = loggedUsers.get(user.getId());
        if(loggedUser == null) {
            LOG.warn("not logged user: " + LoggingUtils.userInfo(user) + ", thread: " + Thread.currentThread().getName());
            return;
        }
        List<GrantedAuthority> roles = loggedUser.getAttribute("roles");
        if(roles != null)
            user.setAttribute("roles", roles);
        loggedSessions.putIfAbsent(user.getId(), new CopyOnWriteArrayList<>());
        for(HttpSession session : loggedSessions.get(user.getId())) {
            blocked.set("setAttribute");
            session.setAttribute(SESSION_USER, user);
            blocked.set("");
        }
        loggedUsers.put(user.getId(), user);
    }
}
