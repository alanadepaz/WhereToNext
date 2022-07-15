package com.alana.wheretonext.service;

import com.alana.wheretonext.data.db.UserRepository;
import com.alana.wheretonext.exceptions.UserException;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class UserService {
    private UserRepository userRepository;

    public UserService() {
        userRepository = new UserRepository();
    }

    public void loginUser(String username, String password) throws UserException {
        userRepository.loginUser(username, password);
    }

    public boolean isLoggedIn() {
        return userRepository.isLoggedIn();
    }

    public void saveUser(String username, String password, String email) throws UserException {
        userRepository.saveUser(username, password, email);
    }

    public void logoutUser() {
        userRepository.logoutUser();
    }

    public String getUserUsername() { return userRepository.getUserUsername(); }

    public String getUserEmail() { return userRepository.getUserEmail(); }

    public ParseFile getProfileImage() { return userRepository.getProfileImage(); }
}
