package com.example.wheretonext;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;

@ParseClassName("Country")
public class Country extends ParseObject {
    public static final String KEY_COUNTRY_NAME = "countryName";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_FAVORITE_PHRASES = "favoritePhrases";

    public Country() {
        // Required empty constructor
    };

    public String getCountryName() { return getString(KEY_COUNTRY_NAME); }

    public void setCountryName(String countryName) { put(KEY_COUNTRY_NAME, countryName); }

    public String getLanguage() { return getString(KEY_LANGUAGE); }

    public void setLanguage(String language) { put(KEY_LANGUAGE, language); }

    public ParseRelation<Phrase> getFavePhrasesRelation() { return getRelation(KEY_FAVORITE_PHRASES); }

    public void addFavePhrase(Phrase favePhrase) {
        getFavePhrasesRelation().add(favePhrase);
        saveInBackground();
    }

    public void removeFavePhrase(Phrase favePhrase) {
        getFavePhrasesRelation().remove(favePhrase);
        saveInBackground();
    }
}
