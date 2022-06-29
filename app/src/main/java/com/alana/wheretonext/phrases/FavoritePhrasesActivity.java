package com.alana.wheretonext.phrases;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.alana.wheretonext.login.LoginActivity;
import com.alana.wheretonext.models.CountrySection;
import com.alana.wheretonext.models.FavoritePhrase;
import com.alana.wheretonext.models.Phrase;
import com.alana.wheretonext.network.TranslationClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import com.alana.wheretonext.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class FavoritePhrasesActivity extends AppCompatActivity {

    public static final String TAG = "FavoritePhrasesActivity";
    private RecyclerView rvFavePhrases;
    protected List<FavoritePhrase> allFavePhrases;
    //protected FavoritePhrasesAdapter adapter;
    SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();

    protected List<String> allFaveTranslations = Collections.synchronizedList(new ArrayList<String>());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_phrases);

        rvFavePhrases = findViewById(R.id.rvFavePhrases);

        allFavePhrases = new ArrayList<>();

        //adapter = new FavoritePhrasesAdapter(this, allFavePhrases, allFaveTranslations);

        // Set the adapter on the recycler view
        //rvFavePhrases.setAdapter(adapter);

        rvFavePhrases.setAdapter(sectionAdapter);
        // Set the layout manager on the recycler view
        rvFavePhrases.setLayoutManager(new LinearLayoutManager(this));
        // Query phrases from Parse
        queryPhrases();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    String favePhraseCountry = "";
    protected void queryPhrases() {
        // Specify which class to query
        ParseQuery<FavoritePhrase> query = ParseQuery.getQuery(FavoritePhrase.class);

        query.setLimit(20);
        // Get all the favorite phrases from one user
        query.whereEqualTo(FavoritePhrase.KEY_USER,ParseUser.getCurrentUser());
        query.orderByAscending("countryName");

        Map<String, ArrayList<FavoritePhrase>> phrasesPerCountry = new HashMap<>();

        query.findInBackground(new FindCallback<FavoritePhrase>() {
            @Override
            public void done(List<FavoritePhrase> favePhrases, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting phrases", e);
                    return;
                }
                for (FavoritePhrase favePhrase : favePhrases) {
                    Log.i(TAG, "Fave Phrase: " + favePhrase.getFavoritePhrase());

                    // Country has not already been added to the Map
                    if (!favePhrase.getCountryName().equals(favePhraseCountry)) {
                        favePhraseCountry = favePhrase.getCountryName();
                        //phrasesPerCountry.put(favePhraseCountry, new ArrayList<>());
                        sectionAdapter.addSection(new CountrySection(allFavePhrases, allFaveTranslations, favePhraseCountry));
                    }
                    //phrasesPerCountry.get(favePhraseCountry).add(favePhrase);
                }

                allFavePhrases.addAll(favePhrases);

                // Grab the translations
                try {
                    notifyFavePhrases();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                //adapter.notifyDataSetChanged();
                sectionAdapter.notifyDataSetChanged();
            }
        });
    }

    private void notifyFavePhrases() throws InterruptedException {
        FetchFaveData fetchFaveData = new FetchFaveData();
        Thread thread = new Thread(fetchFaveData);
        thread.start();
        thread.join();
        allFaveTranslations = fetchFaveData.getTranslations();
    }

    // Fetches the data from the Cloud Translation API
    class FetchFaveData implements Runnable {

        String data = "";
        PhrasesActivity phrasesActivity;

        public FetchFaveData(PhrasesActivity phrasesActivity) {
            this.phrasesActivity = phrasesActivity;
        }

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

                Log.d(TAG, "Translation: " + translatedText);
                allFaveTranslations.add(translatedText);
            }
        }

        public List<String> getTranslations() {
            return allFaveTranslations;
        }
    }
}