package com.example.wheretonext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// Implement OnMapReadyCallback.
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = "MapActivity";
    public static final float DEFAULT_ZOOM = 4f;
    private Button btnToPhrases;

    // For fetching country data
    ArrayList<String> countryList;
    ArrayAdapter<String> listAdapter;
    Handler mainHandler = new Handler();
    ProgressDialog progressDialog;



    // Widgets
    private AutoCompleteTextView mSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout file as the content view.
        setContentView(R.layout.activity_map);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);

        // For fetching country data for the search
        new fetchData().start();
        initializeCountryList(mSearchText);

        // For navigating to the Phrases page
        btnToPhrases = findViewById(R.id.btnToPhrases);

        btnToPhrases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "btnToPhrases clicked");
                goPhrasesActivity();
            }
        });

    }

    // For initializing the GoogleMap
    private void init(GoogleMap googleMap) {
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    // Execute method for searching
                    geoLocate(googleMap);
                }
                return false;
            }
        });
    }

    // For geolocating the searched location on the map and moving the camera
    private void geoLocate(GoogleMap googleMap) {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCam(googleMap, new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM);
        }
    }

    // Moves the camera on the Google Map
    private void moveCam(GoogleMap googleMap, LatLng latLng, float zoom) {
        Log.d(TAG, "moveCam: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
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

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        init(googleMap);
        Log.i(TAG, "Google map ready.");

    }

    // Navigate to the phrases activity
    private void goPhrasesActivity() {
        Intent i = new Intent(this, PhrasesActivity.class);
        startActivity(i);
    }

    // Will add the countries found from fetchData to an adapter
    private void initializeCountryList(AutoCompleteTextView searchText) {

        countryList = new ArrayList<>();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countryList);

        listAdapter.addAll(countryList);
        searchText.setAdapter(listAdapter);
    }

    // Fetches the data from the RESTCountries API
    class fetchData extends Thread{

        String data = "";

        @Override
        public void run() {
            super.run();

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(MapActivity.this);
                    progressDialog.setMessage("Fetching Data");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });
            try {
                URL url = new URL("https://restcountries.com/v3.1/all?fields=name,languages");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    Log.d(TAG, line);
                    data = data + line;
                }

                if (!data.isEmpty()) {
                    JSONArray countries = new JSONArray(data);
                    //JSONArray countries = jsonObject.getJSONArray("all");
                    countryList.clear();
                    for (int i = 0; i < countries.length(); i++) {
                        JSONObject names = countries.getJSONObject(i);
                        JSONObject name = names.getJSONObject("name");
                        String commonName = name.getString("common");
                        countryList.add(commonName);
                    }
                    // To put in alphabetical order
                    java.util.Collections.sort(countryList);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                    listAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}