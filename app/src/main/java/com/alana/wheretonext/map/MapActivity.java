package com.alana.wheretonext.map;

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
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.alana.wheretonext.BuildConfig;
import com.alana.wheretonext.phrases.PhrasesActivity;
import com.alana.wheretonext.login.LoginActivity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alana.wheretonext.R;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// Implement OnMapReadyCallback.
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = "MapActivity";
    public static final float DEFAULT_ZOOM = 4f;
    private Button btnToPhrases;

    // For fetching country data
    ArrayList<String> countryList;
    Map<String,String> countryAndLang = new HashMap<>();
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

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng position) {
                //Get the lat long here from variable position
                //Use GeoCoder class to fetch the country from latlong like this
                Geocoder geocoder = new Geocoder(MapActivity.this);
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(addresses.size() > 0){
                    String country = addresses.get(0).getCountryName();
                    Log.i(TAG, "Country name: " + country);

                    goPhrasesActivity(country);
                }
            }
        });
    }

    // Navigate to the phrases activity
    private void goPhrasesActivity(String countryName) {
        Intent i = new Intent(this, PhrasesActivity.class);
        i.putExtra("countryName", countryName);
        i.putExtra("language", countryAndLang.get(countryName));
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
                String url = "https://restcountries.com/v2/all?fields=name,languages";

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    data = response.body().string();
                }

                if (!data.isEmpty()) {
                    JSONArray countries = new JSONArray(data);
                    countryList.clear();
                    countryAndLang.clear();

                    for (int i = 0; i < countries.length(); i++) {
                        JSONObject country = countries.getJSONObject(i);
                        String countryName = country.getString("name");

                        // Handling some exceptions in the database
                        if (countryName.equals("Russian Federation")) {
                            countryName = "Russia";
                        }
                        if (countryName.equals("Bolivia (Plurinational State of)")) {
                            countryName = "Bolivia";
                        }
                        if (countryName.equals("Congo (Democratic Republic of the)")) {
                            countryName = "Republic of the Congo";
                        }
                        if (countryName.equals("Iran (Islamic Republic of)")) {
                            countryName = "Iran";
                        }
                        if (countryName.equals("Korea (Democratic People's Republic of)")) {
                            countryName = "North Korea";
                        }
                        if (countryName.equals("Korea (Republic of)")) {
                            countryName = "South Korea";
                        }


                        countryList.add(countryName);

                        JSONArray languages = country.getJSONArray("languages");

                        // Grab the first language listed, as it is the most spoken OR official language
                        String language = languages.getJSONObject(0).getString("iso639_1");

                        //Log.i(TAG, "Country: " + countryName + ", Language: " + language);
                        countryAndLang.put(countryName, language);
                        }
                    }

                    java.util.Collections.sort(countryList);

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