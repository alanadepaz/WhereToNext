package com.alana.wheretonext.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("FavoritePhrase")
public class FavoritePhrase extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_COUNTRY_NAME = "countryName";
    public static final String KEY_LANGUAGE_CODE = "languageCode";
    public static final String KEY_FAVORITE_PHRASE = "favoritePhrase";

    public FavoritePhrase() {
        // Empty constructor
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public String getCountryName() { return getString(KEY_COUNTRY_NAME); }

    public void setCountryName(String countryName) { put(KEY_COUNTRY_NAME, countryName); }

    public String getLanguageCode() { return getString(KEY_LANGUAGE_CODE); }

    public void setLanguageCode(String languageCode) { put(KEY_LANGUAGE_CODE, languageCode); }

    public ParseObject getFavoritePhrase() { return getParseObject(KEY_FAVORITE_PHRASE); }

    public void setFavoritePhrase(ParseObject favoritePhrase) { put(KEY_FAVORITE_PHRASE, favoritePhrase); }

}
