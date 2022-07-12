package com.alana.wheretonext;

import android.app.Application;

import androidx.room.Room;

import com.alana.wheretonext.data.db.WhereToNextDatabase;
import com.alana.wheretonext.data.db.models.ParseFavoritePhrase;
import com.alana.wheretonext.data.db.models.ParsePhrase;
import com.parse.Parse;
import com.parse.ParseObject;

public class MainApplication extends Application {

    WhereToNextDatabase whereToNextDB;

    @Override
    public void onCreate() {
        super.onCreate();

        // Register the parse model
        ParseObject.registerSubclass(ParsePhrase.class);
        ParseObject.registerSubclass(ParseFavoritePhrase.class);

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
