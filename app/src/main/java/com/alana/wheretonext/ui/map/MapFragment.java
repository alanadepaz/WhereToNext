package com.alana.wheretonext.ui.map;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.alana.wheretonext.R;
import com.alana.wheretonext.ui.phrases.PhrasesActivity;
import com.alana.wheretonext.utils.CountryUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "MapFragment";
    public static final float DEFAULT_ZOOM = 4f;

    // For fetching country data
    ArrayList<String> countryList;
    Map<String, String> countryAndLang = new HashMap<>();
    ArrayAdapter<String> listAdapter;

    // Widgets
    private AutoCompleteTextView mSearchText;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        mSearchText = (AutoCompleteTextView) view.findViewById(R.id.input_search);

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

                    geoLocate(googleMap);
                }
                return false;
            }
        });
    }

    boolean countryExists;
    // For geolocating the searched location on the map and moving the camera
    private void geoLocate(GoogleMap googleMap) {
        googleMap.clear();

        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        for (int i = 0; i < listAdapter.getCount(); i++) {
            String temp = listAdapter.getItem(i);
            if (searchString.compareTo(temp) == 0) {
                countryExists = true;
                break;
            }
        }

        if (countryExists) {
            Geocoder geocoder = new Geocoder(getContext());
            List<Address> list = new ArrayList<>();
            try {
                list = geocoder.getFromLocationName(searchString, 1);
            } catch (IOException e) {
                Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
            }

            if (list.size() > 0) {
                Address address = list.get(0);

                Log.d(TAG, "geoLocate: found a location: " + address.toString());

                moveCam(googleMap, new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM);
            }

            try {
                String countryCode = getCountryCode(searchString).toLowerCase();
                GeoJsonLayer layer = new GeoJsonLayer(googleMap,
                        getResources().getIdentifier(countryCode, "raw", "com.alana.wheretonext"),
                        getActivity().getApplicationContext());

                GeoJsonPolygonStyle style = layer.getDefaultPolygonStyle();
                style.setFillColor(Color.CYAN);
                style.setStrokeColor(Color.CYAN);
                style.setStrokeWidth(1F);

                layer.addLayerToMap();

                if (layer.isLayerOnMap()) {
                    layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
                        @Override
                        public void onFeatureClick(Feature feature) {
                            goPhrasesActivity(searchString);
                            layer.removeLayerFromMap();
                        }
                    });
                }

            } catch (IOException ex) {
                Log.e("IOException", ex.getLocalizedMessage());
            } catch (JSONException ex) {
                Log.e("JSONException", ex.getLocalizedMessage());
            }

            // Reset
            countryExists = false;
        } else {
            Toast.makeText(getContext(), "Please select a proper country.", Toast.LENGTH_SHORT).show();
            mSearchText.setText("");
        }
    }

    // Moves the camera on the Google Map
    private void moveCam(GoogleMap googleMap, LatLng latLng, float zoom) {
        Log.d(TAG, "moveCam: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
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
                Geocoder geocoder = new Geocoder(getContext());
                List<Address> addresses = new ArrayList<>();
                try {
                    addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses.size() > 0) {
                    String country = addresses.get(0).getCountryName();
                    Log.i(TAG, "Country name: " + country);

                    goPhrasesActivity(country);
                }
            }
        });
    }

    // Navigate to the phrases activity
    private void goPhrasesActivity(String countryName) {
        Intent i = new Intent(getContext(), PhrasesActivity.class);
        i.setAction("fromMap");
        i.putExtra("countryName", countryName);
        i.putExtra("language", countryAndLang.get(countryName));
        startActivity(i);
    }

    // Will add the countries found from fetchData to an adapter
    private void initializeCountryList(AutoCompleteTextView searchText) {

        countryList = new ArrayList<>();
        listAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, countryList);

        listAdapter.addAll(countryList);
        searchText.setAdapter(listAdapter);
    }

    public String getCountryCode(String countryName) {

        // Get all country codes in a string array.
        String[] isoCountryCodes = Locale.getISOCountries();
        String countryCode = "";
        // Iterate through all country codes:
        for (String code : isoCountryCodes) {
            // Create a locale using each country code
            Locale locale = new Locale("", code);
            // Get country name for each code.
            String name = locale.getDisplayCountry();
            if (name.equals(countryName)) {
                countryCode = code;
                break;
            }
        }
        return countryCode;
    }

    // Fetches the data from the RESTCountries API
    class fetchData extends Thread {

        @Override
        public void run() {
            super.run();

            String jsonFileString = CountryUtil.getJsonFromAssets(getActivity().getApplicationContext(), "countries.json");
            CountryUtil.getCountries(getActivity().getApplicationContext(), jsonFileString, countryList, countryAndLang);
        }
    }
}