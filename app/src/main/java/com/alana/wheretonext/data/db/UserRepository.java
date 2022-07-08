package com.alana.wheretonext.data.db;

import android.util.Log;

import com.alana.wheretonext.exceptions.UserException;
import com.parse.ParseException;
import com.parse.ParseUser;

public class UserRepository {
    public static final String TAG = "UserRepository";

    public void loginUser(String username, String password) throws UserException {
        Log.i(TAG, "Attempting to login user" + username);

        try {
            ParseUser.logIn(username, password);
        }
        catch (ParseException parseException) {
            throw new UserException("Unable to login: " + parseException.getMessage());
        }
    }

    public boolean isLoggedIn() {
        return (ParseUser.getCurrentUser() != null);
    }

    public void saveUser(String username, String password, String email) throws UserException {
        Log.i(TAG, "Attempting to save a new user: " + username);
        ParseUser newUser = new ParseUser();
        newUser.put("username", username);
        newUser.put("password", password);
        newUser.put("email", email);
        try {
            newUser.signUp();
        } catch (ParseException parseException) {
            throw new UserException("Unable to sign up: " + parseException.getMessage());
        }
    }

    public void logoutUser() {
        Log.i(TAG, "Attempting to log out user");
        ParseUser.logOutInBackground();
    }
}