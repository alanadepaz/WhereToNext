package com.example.wheretonext;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register the parse model
        ParseObject.registerSubclass(Phrase.class);
        ParseObject.registerSubclass(Country.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("18XnKbXJL3jtpUHSCLag2AXqPbxQQkxeBWtAReHX")
                .clientKey("fn2gTL1DJVhj33yNcKGCAxcQL4A1ReNDiov45LZg")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
