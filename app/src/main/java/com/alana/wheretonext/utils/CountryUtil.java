package com.alana.wheretonext.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CountryUtil {
    public static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }

    public static void getCountries(Context context, ArrayList<String> countryList, Map<String, String> countryAndLang) {
        // TODO: Continue trying to parse database: https://www.bezkoder.com/java-android-read-json-file-assets-gson/


        String jsonFileString = CountryUtil.getJsonFromAssets(context.getApplicationContext(), "countries.json");
        Log.i("data", jsonFileString);

        if (!jsonFileString.isEmpty()) {
            JSONArray countries = null;
            try {
                countries = new JSONArray(jsonFileString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            countryList.clear();
            countryAndLang.clear();

            for (int i = 0; i < countries.length(); i++) {
                JSONObject country = null;
                try {
                    country = countries.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String countryName = null;
                try {
                    countryName = country.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                countryList.add(countryName);

                JSONArray languages = null;
                try {
                    languages = country.getJSONArray("languages");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Grab the first language listed, as it is the most spoken OR official language
                String language = null;
                try {
                    language = languages.getJSONObject(0).getString("iso639_1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.i(TAG, "Country: " + countryName + ", Language: " + language);
                countryAndLang.put(countryName, language);
            }
        }
    }
}
