package com.alana.wheretonext.data.network;

import android.content.Context;

import com.alana.wheretonext.data.models.FavoritePhrase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Fetches the data from the Cloud Translation API
public class FetchFaveTranslationData implements Runnable {

    public static final String TAG = "FetchFaveTranslationData";
    private Context context;
    private List<String> allTranslations;
    private String language;
    private List<FavoritePhrase> allFavePhrases;
    private Map<String, List<String>> favoriteTranslationsMap;  // Map needed for favorite translations and association with country


    public FetchFaveTranslationData(Context context, List<FavoritePhrase> allFavePhrases, List<String> allTranslations, String language, Map<String, List<String>> favoriteTranslationsMap) {
        this.context = context;
        this.allFavePhrases = allFavePhrases;
        this.allTranslations = allTranslations;
        this.language = language;
        this.favoriteTranslationsMap = favoriteTranslationsMap;
    }

    @Override
    public void run() {
        //super.run();

        for (FavoritePhrase favePhrase : allFavePhrases) {
            // Grab all translations
            String translatedText = TranslationClient.getTranslation(favePhrase.getFavoritePhrase(), favePhrase.getLanguageCode());

            List<String> faveCountryTranslations = favoriteTranslationsMap.get(favePhrase.getCountryName());

            if (faveCountryTranslations == null) {
                faveCountryTranslations = new ArrayList<>();
                favoriteTranslationsMap.put(favePhrase.getCountryName(), faveCountryTranslations);
            }
            faveCountryTranslations.add(translatedText);
        }
    }

    public Map<String, List<String>> getTranslationsMap() {
        return favoriteTranslationsMap;
    }
}