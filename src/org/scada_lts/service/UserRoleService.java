package org.scada_lts.service;

import org.scada_lts.dao.UserRoleDAO;
import org.scada_lts.service.model.UserRole;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService {

    private final UserRoleDAO userRoleDAO = new UserRoleDAO();

    public List<UserRole> getUserRoles(int userId){
        return userRoleDAO.getUserRoles(userId);
    }

    public void insertUserRole(int userId, boolean isAdmin) {
        String role = determineUserRole(isAdmin);
        userRoleDAO.insert(userId, role);
    }

    public void updateUserRole(int userId, boolean isAdmin) {
        String role = determineUserRole(isAdmin);
        userRoleDAO.update(role, userId);
    }

    public void deleteUserRoleUser(int userId) {
        userRoleDAO.deleteUserRoleUser(userId);
    }

    public void deleteUserRole(int userId, String role) {
        userRoleDAO.deleteUserRole(userId, role);
    }

    private String determineUserRole(boolean isAdmin) {
        return isAdmin ? "ROLE_ADMIN" : "ROLE_USER";
    }
}
