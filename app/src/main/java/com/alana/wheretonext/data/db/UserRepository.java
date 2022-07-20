package com.alana.wheretonext.data.db;

import android.util.Log;
import android.widget.Toast;

import com.alana.wheretonext.data.db.models.ParseFavoritePhrase;
import com.alana.wheretonext.exceptions.UserException;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.File;

public class UserRepository {
    public static final String TAG = "UserRepository";
    private static final String KEY_PROFILE_IMAGE = "profileImage";

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

    public String getUserUsername() {
        return ParseUser.getCurrentUser().getUsername();
    }

    public String getUserEmail() {
        return ParseUser.getCurrentUser().getEmail();
    }

    public String getProfileImageURL() {
        ParseFile image = ParseUser.getCurrentUser().getParseFile("profileImage");

        if (image != null) {
            return image.getUrl();
        }
        return null;
    }

    public void setProfileImage(File photoFile) throws UserException {
        ParseUser user = ParseUser.getCurrentUser();
        user.put(KEY_PROFILE_IMAGE, new ParseFile(photoFile));
        try {
            user.save();
        } catch (ParseException e) {
            throw new UserException("Unable to upload profile image: " + e.getMessage());
        }
    }
}
