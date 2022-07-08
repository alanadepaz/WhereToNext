package com.alana.wheretonext.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.alana.wheretonext.data.db.models.Translation;

@Database(entities={Translation.class}, version=1)
public abstract class WhereToNextDatabase extends RoomDatabase {
    // Declare data access objects as abstract
    public abstract TranslationDao translationDao();

    // Database name to be used
    public static final String NAME = "WhereToNextDatabase";
}
