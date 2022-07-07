package com.alana.wheretonext;

import android.app.Application;

import androidx.room.Room;

import com.alana.wheretonext.models.FavoritePhrase;
import com.alana.wheretonext.models.Phrase;
import com.parse.Parse;
import com.parse.ParseObject;

public class MainApplication extends Application {

    WhereToNextDatabase whereToNextDB;

    @Override
    public void onCreate() {
        super.onCreate();

        // Register the parse model
        ParseObject.registerSubclass(Phrase.class);
        ParseObject.registerSubclass(FavoritePhrase.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("18XnKbXJL3jtpUHSCLag2AXqPbxQQkxeBWtAReHX")
                .clientKey("fn2gTL1DJVhj33yNcKGCAxcQL4A1ReNDiov45LZg")
                .server("https://parseapi.back4app.com")
                .build()
        );

        // when upgrading versions, kill the original tables by using fallbackToDestructiveMigration()
        whereToNextDB = Room.databaseBuilder(this, WhereToNextDatabase.class, WhereToNextDatabase.NAME).fallbackToDestructiveMigration().build();
    }

    public WhereToNextDatabase getWhereToNextDB() {
        return whereToNextDB;
    }
}
