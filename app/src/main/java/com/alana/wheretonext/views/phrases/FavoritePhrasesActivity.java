package com.alana.wheretonext.views.phrases;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import com.alana.wheretonext.R;
import com.alana.wheretonext.views.login.LoginActivity;
import com.alana.wheretonext.models.CountrySection;
import com.alana.wheretonext.models.FavoritePhrase;
import com.alana.wheretonext.network.TranslationClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FavoritePhrasesActivity extends AppCompatActivity {

    public static final String TAG = "FavoritePhrasesActivity";

    private Context context;
    private RecyclerView rvFavePhrases;
    protected List<FavoritePhrase> allFavePhrases;
    private SectionedRecyclerViewAdapter sectionedAdapter;

    protected List<FavoritePhrase> filteredFavePhrases;

    private Map<String, List<String>> favoriteTranslationsMap;  // Map needed for favorite translations and association with country


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_phrases);

        allFavePhrases = new ArrayList<>();
        filteredFavePhrases = new ArrayList<>();
        favoriteTranslationsMap = new HashMap<>();

        sectionedAdapter = new SectionedRecyclerViewAdapter();

        rvFavePhrases = findViewById(R.id.rvFavePhrases);
        rvFavePhrases.setLayoutManager(new LinearLayoutManager(context));
        rvFavePhrases.setAdapter(sectionedAdapter);

        queryFavePhrases();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logoutButton) {
            // Compose icon has been selected
            ParseUser.logOutInBackground();

            // Navigate to the compose activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void queryFavePhrases() {
        // Specify which class to query
        ParseQuery<FavoritePhrase> query = ParseQuery.getQuery(FavoritePhrase.class);

        query.setLimit(20);
        // Get all the favorite phrases from one user
        query.whereEqualTo(FavoritePhrase.KEY_USER, ParseUser.getCurrentUser());
        query.orderByAscending("countryName");

        Map<String, ArrayList<FavoritePhrase>> phrasesPerCountry = new HashMap<>();

        query.findInBackground(new FindCallback<FavoritePhrase>() {
            @Override
            public void done(List<FavoritePhrase> favePhrases, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting phrases", e);
                    return;
                }

                allFavePhrases.addAll(favePhrases);

                // Grab the translations
                try {
                    notifyFavePhrases();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                final Map<String, List<FavoritePhrase>> favoritePhrasesMap = getFavePhrasesByCountry();
                for (final Map.Entry<String, List<FavoritePhrase>> entry : favoritePhrasesMap.entrySet()) {
                    if (entry.getValue().size() > 0) {

                        filteredFavePhrases = entry.getValue();
                        List<String> filteredTranslations = favoriteTranslationsMap.get(entry.getKey());

                        sectionedAdapter.addSection(new CountrySection(entry.getKey(), filteredFavePhrases, filteredTranslations));
                    }
                }
                //adapter.notifyDataSetChanged();
                sectionedAdapter.notifyDataSetChanged();
            }
        });
    }

    private Map<String, List<FavoritePhrase>> getFavePhrasesByCountry() {
        final Map<String, List<FavoritePhrase>> map = new LinkedHashMap<>();

        String currentCountry;
        for (FavoritePhrase favoritePhrase : allFavePhrases) {
            currentCountry = favoritePhrase.getCountryName();
            List<FavoritePhrase> faveCountryPhrases = map.get(currentCountry);

            if (faveCountryPhrases == null) {
                faveCountryPhrases = new ArrayList<>();
                map.put(currentCountry, faveCountryPhrases);
            }
            faveCountryPhrases.add(favoritePhrase);
        }

        return map;
    }

    private void notifyFavePhrases() throws InterruptedException {
        FavoritePhrasesActivity.FetchFaveData fetchFaveData = new FetchFaveData();
        Thread thread = new Thread(fetchFaveData);
        thread.start();
        thread.join();
        favoriteTranslationsMap = fetchFaveData.getTranslationsMap();
    }

    // Fetches the data from the Cloud Translation API
    class FetchFaveData implements Runnable {

        public FetchFaveData() {
            // Required empty constructor
        }

        @Override
        public void run() {
            //super.run();

            for (FavoritePhrase favePhrase : allFavePhrases) {
                // Grab all translations
                String translation = TranslationClient.getTranslation(favePhrase.getFavoritePhrase().getString("phrase"), favePhrase.getLanguageCode());
                String translatedText = "";
                try {
                    JSONObject translationObject = new JSONObject(translation);
                    JSONObject data = translationObject.getJSONObject("data");
                    JSONArray translations = data.getJSONArray("translations");
                    JSONObject textAndSourceLanguage = translations.getJSONObject(0);
                    translatedText = textAndSourceLanguage.getString("translatedText");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
}
