package com.alana.wheretonext.ui.phrases;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alana.wheretonext.MainApplication;
import com.alana.wheretonext.data.models.FavoritePhrase;
import com.alana.wheretonext.service.PhraseService;
import com.alana.wheretonext.service.UserService;
import com.alana.wheretonext.ui.login.LoginActivity;
import com.alana.wheretonext.data.db.models.Translation;
import com.alana.wheretonext.data.network.TranslationClient;
import com.alana.wheretonext.data.db.TranslationDao;

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
    protected List<String> allPhrases;

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

    private PhraseService phraseService = new PhraseService();
    private UserService userService = new UserService();

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
                findViewById(R.id.rvPhrases).setAlpha(1 - slideOffset);

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {

                    favePhraseAdapter.notifyDataSetChanged();
                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    favePhraseAdapter.notifyDataSetChanged();
                }
            }
        });

        phraseAdapter.setUpTTS();

        queryPhrases();

        queryFavePhrases();
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
            userService.logoutUser();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        phraseAdapter.onDestroy();
    }

    protected void queryPhrases() {

        allPhrases.addAll(phraseService.getPhrases());
        Log.d(TAG, "Phrases size: " + allPhrases.size());

        // Grab the translations
        try {
            notifyPhrases();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        phraseAdapter.notifyDataSetChanged();
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
            for (String phrase : allPhrases) {
                // Grab all translations
                Translation translation = translationDao.getTranslation(phrase, language);
                Log.d(TAG, "Cached translation: " + translation);
                if (translation == null) {
                    if (language != null) {
                        String translatedText = TranslationClient.getTranslation(phrase, language);

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

    // FOR FAVORITE PHRASES PANEL
    protected void queryFavePhrases() {

        List<FavoritePhrase> favoritePhraseList = phraseService.getFavoritePhrases(countryName);

        Map<String, ArrayList<FavoritePhrase>> phrasesPerCountry = new HashMap<>();

        rvFavePhrases.removeAllViews();
        rvFavePhrases.refreshDrawableState();

        favePhraseAdapter.removeAllSections();


        allFavePhrases.addAll(favoritePhraseList);

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

                favePhraseAdapter.addSection(new PhrasesSection(entry.getKey(), filteredFavePhrases, filteredTranslations));
            }
        }
        //phraseAdapter.notifyDataSetChanged();
        favePhraseAdapter.notifyDataSetChanged();
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
}