package com.example.wheretonext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class PhrasesActivity extends AppCompatActivity {

    public static final String TAG = "PhrasesActivity";

    private Context context;
    private RecyclerView rvPhrases;
    private Button btnToFavePhrases;
    protected PhrasesAdapter adapter;
    protected List<Phrase> allPhrases;
    private SwipeRefreshLayout swipeContainer;

    public PhrasesActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrases);

        // Grab the country name and language from the MapActivity
        String countryName = getIntent().getExtras().getString("countryName");
        String language = getIntent().getExtras().getString("language");

        rvPhrases = findViewById(R.id.rvPhrases);
        btnToFavePhrases = findViewById(R.id.btnToFavePhrases);

        // Initialize the array that will hold phrases and create a PhrasesAdapter
        allPhrases = new ArrayList<>();
        adapter = new PhrasesAdapter(this, allPhrases, countryName, language);

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
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void goFavePhrasesActivity() {
        Intent i = new Intent(this, FavoritePhrasesActivity.class);
        startActivity(i);
    }
}