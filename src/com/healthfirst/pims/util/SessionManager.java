package com.healthfirst.pims.util;

import com.healthfirst.pims.model.User;

public class SessionManager {

    private static User currentUser = null;

    private SessionManager() {}

    public static synchronized void setCurrentUser(User user) {
        currentUser = user;
    }

    public static synchronized User getCurrentUser() {
        return currentUser;
    }

    public static synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    public static synchronized void logout() {
        currentUser = null;
    }
}