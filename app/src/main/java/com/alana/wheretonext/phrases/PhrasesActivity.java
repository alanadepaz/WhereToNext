package com.alana.wheretonext.phrases;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.alana.wheretonext.login.LoginActivity;
import com.alana.wheretonext.models.Phrase;
import com.alana.wheretonext.network.TranslationClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alana.wheretonext.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class PhrasesActivity extends AppCompatActivity {

    public static final String TAG = "PhrasesActivity";

    private Context context;
    private RecyclerView rvPhrases;
    private TextView tvCountryName;
    private Button btnToFavePhrases;
    protected PhrasesAdapter adapter;
    protected List<Phrase> allPhrases;

    // Initialize the array that will hold the translations
    protected List<String> allTranslations = Collections.synchronizedList(new ArrayList<String>());
    private SwipeRefreshLayout swipeContainer;

    private String countryName;
    private String language;

    public PhrasesActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrases);

        SlidingUpPanelLayout layout = findViewById(R.id.slidingUp);

        layout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                findViewById(R.id.rvFavePhrases).setAlpha(1 - slideOffset);

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    Toast.makeText(PhrasesActivity.this, "Panel expanded", Toast.LENGTH_SHORT).show();
                    //goFavePhrasesActivity();
                }
                else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    Toast.makeText(PhrasesActivity.this, "Panel collapsed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Grab the country name and language from the MapActivity
        countryName = getIntent().getExtras().getString("countryName");

        language = getIntent().getExtras().getString("language");

        Log.d(TAG, "Language: " + language);

        tvCountryName = findViewById(R.id.tvCountryName);
        tvCountryName.setText(countryName);

        rvPhrases = findViewById(R.id.rvPhrases);
        btnToFavePhrases = findViewById(R.id.btnToFavePhrases);

        // Initialize the array that will hold phrases and create a PhrasesAdapter
        allPhrases = new ArrayList<>();

        adapter = new PhrasesAdapter(this, allPhrases, countryName, language, allTranslations);

        // Set the adapter on the recycler view
        rvPhrases.setAdapter(adapter);
        // Set the layout manager on the recycler view
        rvPhrases.setLayoutManager(new LinearLayoutManager(this));
        // Query phrases from Parse
        queryPhrases();


        btnToFavePhrases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "btnToFavePhrases clicked");
                goFavePhrasesActivity();
            }
        });
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
        //query.include(Phrase.KEY_USER);

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

                adapter.notifyDataSetChanged();
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

    private void goFavePhrasesActivity() {
        Intent i = new Intent(this, FavoritePhrasesActivity.class);
        startActivity(i);
    }

    // Fetches the data from the Cloud Translation API
    class FetchData implements Runnable {

        String data = "";
        PhrasesActivity phrasesActivity;

        public FetchData(PhrasesActivity phrasesActivity) {
            this.phrasesActivity = phrasesActivity;
        }

        public FetchData() {
            // Required empty constructor
        }

        @Override
        public void run() {
            //super.run();

            for (Phrase phrase : allPhrases) {
                // Grab all translations
                String translation = TranslationClient.getTranslation(phrase.getPhrase(), language);
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
                allTranslations.add(translatedText);
            }
        }

        public List<String> getTranslations() {
            return allTranslations;
        }
    }
}