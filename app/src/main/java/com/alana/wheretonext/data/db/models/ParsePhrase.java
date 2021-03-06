package com.alana.wheretonext.data.db.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Locale;

@ParseClassName("Phrase")
public class ParsePhrase extends ParseObject {
    public static final String KEY_PHRASE = "phrase";

    public ParsePhrase() {
        // Required empty constructor
    };

    public String getPhrase() { return getString(KEY_PHRASE); }

}
