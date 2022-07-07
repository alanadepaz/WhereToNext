package com.alana.wheretonext.phrases;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alana.wheretonext.MainApplication;
import com.alana.wheretonext.login.LoginActivity;
import com.alana.wheretonext.models.CountrySection;
import com.alana.wheretonext.models.FavoritePhrase;
import com.alana.wheretonext.models.Phrase;
import com.alana.wheretonext.models.Translation;
import com.alana.wheretonext.network.TranslationClient;
import com.alana.wheretonext.network.TranslationDao;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alana.wheretonext.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class PhrasesActivity extends AppCompatActivity {

    public static final String TAG = "PhrasesActivity";

    private Context context;
    private RecyclerView rvPhrases;
    private TextView tvCountryName;
    protected PhrasesAdapter phraseAdapter;
    protected List<Phrase> allPhrases;

    // Initialize the array that will hold the translations
    protected List<String> allTranslations = Collections.synchronizedList(new ArrayList<String>());

    private String countryName;
    private String language;

    // FOR FAVORITE PHRASES PANEL
    private RecyclerView rvFavePhrases;
    protected List<FavoritePhrase> allFavePhrases;
    private SectionedRecyclerViewAdapter favePhraseAdapter;

    protected List<FavoritePhrase> filteredFavePhrases;

    private Map<String, List<String>> favoriteTranslationsMap;  // Map needed for favorite translations and association with country

    public PhrasesActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrases);

        // Grab the country name and language from the MapActivity
        countryName = getIntent().getExtras().getString("countryName");

        language = getIntent().getExtras().getString("language");

        Log.d(TAG, "Language: " + language);

        tvCountryName = findViewById(R.id.tvCountryName);
        tvCountryName.setText(countryName);

        rvPhrases = findViewById(R.id.rvPhrases);
        //btnToFavePhrases = findViewById(R.id.btnToFavePhrases);

        // Initialize the array that will hold phrases and create a PhrasesAdapter
        allPhrases = new ArrayList<>();

        favePhraseAdapter = new SectionedRecyclerViewAdapter();

        phraseAdapter = new PhrasesAdapter(this, allPhrases, countryName, language, allTranslations, favePhraseAdapter);

        // Set the adapter on the recycler view
        rvPhrases.setAdapter(phraseAdapter);
        // Set the layout manager on the recycler view
        rvPhrases.setLayoutManager(new LinearLayoutManager(this));

        // FOR FAVORITE PHRASES PANEL
        allFavePhrases = new ArrayList<>();
        filteredFavePhrases = new ArrayList<>();
        favoriteTranslationsMap = new HashMap<>();

        rvFavePhrases = findViewById(R.id.rvFavePhrases);
        rvFavePhrases.setLayoutManager(new LinearLayoutManager(context));
        rvFavePhrases.setAdapter(favePhraseAdapter);

        SlidingUpPanelLayout layout = findViewById(R.id.slidingUp);

        layout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.d(TAG, "In bind method onPanelSlide");
                findViewById(R.id.rvPhrases).setAlpha(1 - slideOffset);

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.d(TAG, "In bind method onPanelStateChanged");
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    //recreate();
                    Toast.makeText(PhrasesActivity.this, "Panel expanded", Toast.LENGTH_SHORT).show();

                    //phraseAdapter.notifyDataSetChanged();
                    //CountrySection section = (CountrySection) favePhraseAdapter.getSection(0);
                    //section.favePhrasesList.add(new FavoritePhrase());
                    favePhraseAdapter.notifyDataSetChanged();
                    //queryFavePhrases();
                }
                else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    //recreate();
                    //Toast.makeText(PhrasesActivity.this, "Panel collapsed", Toast.LENGTH_SHORT).show();
                    //phraseAdapter.notifyDataSetChanged();
                    favePhraseAdapter.notifyDataSetChanged();
                }
            }
        });

        // Query phrases from Parse
        queryPhrases();

        // Query favorite Phrases too
        queryFavePhrases();
    }

    // TODO: update sharedPrefs onResume(), and then also query the data again to know if something is favorited

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

    protected void queryPhrases() {
        // Specify which class to query
        ParseQuery<Phrase> query = ParseQuery.getQuery(Phrase.class);

        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<Phrase>() {
            @Override
            public void done(List<Phrase> phrases, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting phrases", e);
                    return;
                }
                for (Phrase phrase : phrases) {
                    Log.i(TAG, "Phrase: " + phrase.getPhrase());
                }
                allPhrases.addAll(phrases);

                // Grab the translations
                try {
                    notifyPhrases();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                phraseAdapter.notifyDataSetChanged();
            }
        });
    }

    private void notifyPhrases() throws InterruptedException {
        FetchData fetchData = new FetchData();
        Thread thread = new Thread(fetchData);
        thread.start();
        thread.join();
        allTranslations = fetchData.getTranslations();
    }

    // Fetches the data from the Cloud Translation API
    class FetchData implements Runnable {

        PhrasesActivity phrasesActivity;

        public FetchData() {
            // Required empty constructor
        }

        @Override
        public void run() {
            final TranslationDao translationDao = ((MainApplication) getApplicationContext()).getWhereToNextDB().translationDao();

            // TODO: Optimize this by sending in multiple phrases at once rather than with a for loop
            for (Phrase phrase : allPhrases) {
                // Grab all translations
                Translation translation = translationDao.getTranslation(phrase.getPhrase(), language);
                Log.d(TAG, "Cached translation: " + translation);
                if (translation == null){
                    String translatedText = TranslationClient.getTranslation(phrase.getPhrase(), language);
                    translation = new Translation(phrase.getPhrase(), language, translatedText);
                    translationDao.insertTranslation(translation);
                }

                Log.d(TAG, "Translation: " + translation.translation);
                allTranslations.add(translation.translation);
            }
        }

        public List<String> getTranslations() {
            return allTranslations;
        }
    }

    // TODO: find a way to create one section for the one fave phrase
    // TODO: The problem is probably that I'm not updating the adapter before notifying the dataset changed
    // TODO: pass in the favePhrasesAdapter to the phrasesAdapter to update when toggled
    // FOR FAVORITE PHRASES PANEL
    protected void queryFavePhrases() {
        // Specify which class to query
        ParseQuery<FavoritePhrase> query = ParseQuery.getQuery(FavoritePhrase.class);

        query.setLimit(20);
        // Get all the favorite phrases from one user
        query.whereEqualTo(FavoritePhrase.KEY_USER, ParseUser.getCurrentUser());
        query.orderByAscending("countryName");

        Map<String, ArrayList<FavoritePhrase>> phrasesPerCountry = new HashMap<>();

        rvFavePhrases.removeAllViews();
        rvFavePhrases.refreshDrawableState();

        favePhraseAdapter.removeAllSections();

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

                        favePhraseAdapter.addSection(new CountrySection(entry.getKey(), filteredFavePhrases, filteredTranslations));
                    }
                }
                //phraseAdapter.notifyDataSetChanged();
                favePhraseAdapter.notifyDataSetChanged();
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
        FetchFaveData fetchFaveData = new FetchFaveData();
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
                String translatedText = TranslationClient.getTranslation(favePhrase.getFavoritePhrase().getString("phrase"), favePhrase.getLanguageCode());

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