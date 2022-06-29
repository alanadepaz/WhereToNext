package com.alana.wheretonext.phrases;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import com.alana.wheretonext.R;
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


public class FavoritePhrasesFragment extends Fragment {

    private static final String DIALOG_TAG = "SectionItemInfoDialogTag";
    public static final String TAG = "FavoritePhrasesFragment";

    private RecyclerView rvFavePhrases;
    protected List<FavoritePhrase> allFavePhrases;
    protected List<String> allFaveTranslations = Collections.synchronizedList(new ArrayList<>());
    private SectionedRecyclerViewAdapter sectionedAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_favorite_phrases, container, false);

        sectionedAdapter = new SectionedRecyclerViewAdapter();

        final Map<String, List<FavoritePhrase>> favoritePhrasesMap = getFavePhrasesByCountry();
        for (final Map.Entry<String, List<FavoritePhrase>> entry : favoritePhrasesMap.entrySet()) {
            if (entry.getValue().size() > 0) {
                sectionedAdapter.addSection(new CountrySection(entry.getKey(), entry.getValue(), new ArrayList<String>()));
            }
        }

        final RecyclerView recyclerView = view.findViewById(R.id.rvFavePhrases);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionedAdapter);

        return view;
    }

    protected void queryPhrases() {
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
                for (FavoritePhrase favePhrase : favePhrases) {
                    Log.i(TAG, "Fave Phrase: " + favePhrase.getFavoritePhrase());
                }

                allFavePhrases.addAll(favePhrases);

                // Grab the translations
                try {
                    notifyFavePhrases();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                //adapter.notifyDataSetChanged();
                sectionedAdapter.notifyDataSetChanged();
            }
        });
    }

    private Map<String, List<FavoritePhrase>> getFavePhrasesByCountry() {
        final Map<String, List<FavoritePhrase>> map = new LinkedHashMap<>();
        queryPhrases();

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
        FavoritePhrasesFragment.FetchFaveData fetchFaveData = new FetchFaveData();
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
