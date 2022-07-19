package com.alana.wheretonext.data.network;

import android.content.Context;
import android.util.Log;

import com.alana.wheretonext.MainApplication;
import com.alana.wheretonext.data.db.TranslationDao;
import com.alana.wheretonext.data.db.models.Translation;
import com.alana.wheretonext.ui.phrases.PhrasesActivity;

import java.util.List;

// Fetches the data from the Cloud Translation API
public class FetchTranslationData implements Runnable {

    public static final String TAG = "FetchTranslationData";
    private Context context;
    protected List<String> allPhrases;
    private List<String> allTranslations;
    private String language;

    private TranslationClient translationClient = new TranslationClient();

    public FetchTranslationData(Context context, List<String> allPhrases, List<String> allTranslations, String language) {
        this.context = context;
        this.allPhrases = allPhrases;
        this.allTranslations = allTranslations;
        this.language = language;
    }

    @Override
    public void run() {
        final TranslationDao translationDao = ((MainApplication) context).getWhereToNextDB().translationDao();

        for (String phrase : allPhrases) {
            // Grab all translations
            Translation translation = translationDao.getTranslation(phrase, language);
            Log.d(TAG, "Cached translation: " + translation);
            if (translation == null) {
                if (language != null) {
                    String translatedText = translationClient.getTranslation(phrase, language);

                    translation = new Translation(phrase, language, translatedText);
                    translationDao.insertTranslation(translation);
                } else {
                    String translatedText = "";
                    translation = new Translation(phrase, language, translatedText);
                }
            }

            Log.d(TAG, "Translation: " + translation.translation);
            allTranslations.add(translation.translation);
        }
    }

    public List<String> getTranslations() {
        return allTranslations;
    }
}
