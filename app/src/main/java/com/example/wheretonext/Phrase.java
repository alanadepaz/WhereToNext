package com.example.wheretonext;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Phrase")
public class Phrase extends ParseObject {
    public static final String KEY_PHRASE = "phrase";

    public Phrase() {
        // Required empty constructor
    };

    public String getPhrase() { return getString(KEY_PHRASE); }
}
