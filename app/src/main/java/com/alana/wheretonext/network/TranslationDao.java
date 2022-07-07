package com.alana.wheretonext.network;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.alana.wheretonext.models.Translation;

@Dao
public interface TranslationDao {
    @Query("SELECT * FROM Translation where textToTranslate = :text and languageOfTranslation = :targetLang")
    public Translation getTranslation(String text, String targetLang);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertTranslation(Translation translation);
}
