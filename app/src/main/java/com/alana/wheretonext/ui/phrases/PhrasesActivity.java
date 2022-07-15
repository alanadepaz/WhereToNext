package com.alana.wheretonext.ui.phrases;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.alana.wheretonext.MainActivity;
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
import java.util.Locale;
import java.util.Map;

import com.alana.wheretonext.R;
import com.alana.wheretonext.ui.map.MapFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseFile;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class PhrasesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "PhrasesActivity";
    public static final int MAP_FRAGMENT = 0;
    public static final int SETTINGS_FRAGMENT = 1;

    private Context context;
    private RecyclerView rvPhrases;
    private TextView tvCountryName;
    protected PhrasesAdapter phraseAdapter;
    protected List<String> allPhrases;

    private SlidingUpPanelLayout layout;

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

    TextToSpeech tts;

    // For tool bar and side menu
    private DrawerLayout phrasesDrawerLayout;
    private NavigationView phrasesNavView;
    private Toolbar phrasesToolbar;

    private ImageView ivProfileImage;
    private TextView tvUsername;
    private TextView tvEmail;

    public PhrasesActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        setContentView(R.layout.activity_phrases);

        // Grab the country name and language from the MapActivity
        countryName = getIntent().getExtras().getString("countryName");

        language = getIntent().getExtras().getString("language");

        Log.d(TAG, "Language: " + language);

        tvCountryName = findViewById(R.id.tvCountryName);
        tvCountryName.setText(countryName);

        rvPhrases = findViewById(R.id.rvPhrases);

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

        layout = findViewById(R.id.slidingUp);

        // For circular animation
        if (savedInstanceState == null) {
            layout.setVisibility(View.INVISIBLE);

            final ViewTreeObserver viewTreeObserver = layout.getViewTreeObserver();

            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity();
                        layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                });
            }

        }

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

        tts = phraseAdapter.setUpTTS();

        queryPhrases();

        queryFavePhrases();

        phrasesToolbar = findViewById(R.id.phrasesToolbar);
        setSupportActionBar(phrasesToolbar);

        phrasesDrawerLayout = findViewById(R.id.phrasesDrawerLayout);
        phrasesNavView = findViewById(R.id.phrasesNavView);

        // For toolbar and navigation side menu
        View headerView = phrasesNavView.getHeaderView(0);

        ivProfileImage = headerView.findViewById(R.id.ivProfileImage);
        tvUsername = headerView.findViewById(R.id.tvUsername);
        tvEmail = headerView.findViewById(R.id.tvEmail);

        tvUsername.setText(userService.getUserUsername());
        tvEmail.setText(userService.getUserEmail());

        // Default profile image
        Glide.with(PhrasesActivity.this)
                .load(R.mipmap.default_profile_round)
                .into(ivProfileImage);

        ParseFile image = userService.getProfileImage();

        if (image != null) {
            Glide.with(PhrasesActivity.this)
                    .load(image.getUrl())
                    .transform(new RoundedCorners(100))
                    .placeholder(R.mipmap.default_profile_round)
                    .error(R.mipmap.default_profile_round)
                    .into(ivProfileImage);
        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                phrasesDrawerLayout,
                phrasesToolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        phrasesDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        phrasesNavView.setNavigationItemSelectedListener(this);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_map:
                Intent mapIntent = new Intent(this, MainActivity.class);
                mapIntent.putExtra("fragmentToLoad", MAP_FRAGMENT);
                startActivity(mapIntent);
                break;

            case R.id.nav_settings:
                Intent settingsIntent = new Intent(this, MainActivity.class);
                settingsIntent.putExtra("fragmentToLoad", SETTINGS_FRAGMENT);
                startActivity(settingsIntent);
                break;

            case R.id.nav_logout:
                userService.logoutUser();

                Intent logoutIntent = new Intent(this, LoginActivity.class);
                startActivity(logoutIntent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

                PhrasesSection newSection = new PhrasesSection(entry.getKey(), filteredFavePhrases, filteredTranslations, filteredFavePhrases.get(0).getLanguageCode(), tts);
                favePhraseAdapter.addSection(newSection);
            }
        }
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

    private void circularRevealActivity() {
        int cx = layout.getWidth() / 2;
        int cy = layout.getWidth() / 2;

        float finalRadius = Math.max(layout.getWidth(), layout.getHeight());

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                layout,
                cx,
                cy,
                0,
                finalRadius);

        circularReveal.setDuration(1000);
        layout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = layout.getWidth() / 2;
            int cy = layout.getBottom() / 2;

            float finalRadius = Math.max(layout.getWidth(), layout.getHeight());
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(layout, cx, cy, finalRadius, 0);

            circularReveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    layout.setVisibility(View.INVISIBLE);
                    finish();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            circularReveal.setDuration(1000);
            circularReveal.start();
        }
        else {
            super.onBackPressed();
        }
    }
}