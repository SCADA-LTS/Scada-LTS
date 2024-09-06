package org.scada_lts.login;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.User;
import org.apache.catalina.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.web.beans.ApplicationBeans;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.serotonin.mango.Common.SESSION_USER;

public class LoggedUsers implements ILoggedUsers {

    private static final Logger LOG = LogManager.getLogger(LoggedUsers.class);

    private final Map<Integer, User> loggedUsers = new ConcurrentHashMap<>();
    private final Map<Integer, List<HttpSession>> loggedSessions = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public LoggedUsers() {}

    @Override
    public User addUser(User user, HttpSession session) {
        lock.writeLock().lock();
        try {
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
            update(user, loggedUsers, loggedSessions);
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
                    update(user, loggedUsers, loggedSessions);
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

    @Override
    public Collection<User> getUsers() {
        lock.readLock().lock();
        try {
            return loggedUsers.values();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public User getUser(int id) {
        lock.readLock().lock();
        try {
            return loggedUsers.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void loadSessions(Session[] sessions) {
        for(Session session: sessions) {
            HttpSession httpSession = session.getSession();
            UserService userService = ApplicationBeans.getBean("userService", UserService.class);
            SecurityContext securityContext = (SecurityContext)httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
            if(securityContext != null) {
                Authentication authentication = securityContext.getAuthentication();
                if(authentication != null) {
                    String username = authentication.getName();
                    User sessionUser = userService.getUser(username);
                    if (sessionUser != null && (!sessionUser.isAdmin() || isAdmin(authentication))) {
                        int userId = sessionUser.getId();
                        loggedSessions.putIfAbsent(userId, new ArrayList<>());
                        loggedSessions.get(userId).add(httpSession);
                        loggedUsers.put(userId, sessionUser);
                        LOG.info("Loaded session for user: {}", username);
                    }
                }
            }
        }
    }

    private static void update(User user, Map<Integer, User> loggedUsers,
                               Map<Integer, List<HttpSession>> loggedSessions) {
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
            session.setAttribute(SESSION_USER, user);
        }
        loggedUsers.put(user.getId(), user);
    }

    private static boolean isAdmin(Authentication authentication) {
        for(GrantedAuthority authority: authentication.getAuthorities()) {
            if("ROLE_ADMIN".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
