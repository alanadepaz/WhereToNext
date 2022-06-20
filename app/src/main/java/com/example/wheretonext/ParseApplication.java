package com.example.wheretonext;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("18XnKbXJL3jtpUHSCLag2AXqPbxQQkxeBWtAReHX")
                .clientKey("fn2gTL1DJVhj33yNcKGCAxcQL4A1ReNDiov45LZg")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
