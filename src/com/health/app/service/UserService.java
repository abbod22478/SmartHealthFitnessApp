package com.health.app.service;

import com.health.app.dao.UserDAO;
import com.health.app.model.User;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public boolean createAccount(User user, String password) {
        return userDAO.addUser(user, password);
    }

    public User getUserById(int userId) {
        return userDAO.getUserById(userId);
    }

    public void updateProfile(User user) {
        // Will implement after updateUser() in UserDAO.
    }

    public void updateFitnessGoal(int userId, String newGoal) {
        // Will implement after updateFitnessGoal() in UserDAO.
    }
}