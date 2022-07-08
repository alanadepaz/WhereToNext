package com.alana.wheretonext.data.db.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"textToTranslate", "languageOfTranslation"})
public class Translation {

    @ColumnInfo
    @NonNull public String textToTranslate;

    @ColumnInfo
    @NonNull public String languageOfTranslation;

    @ColumnInfo
    @NonNull public String translation;

    public Translation(String textToTranslate, String languageOfTranslation, String translation) {
        this.textToTranslate = textToTranslate;
        this.languageOfTranslation = languageOfTranslation;
        this.translation = translation;
    }
}
