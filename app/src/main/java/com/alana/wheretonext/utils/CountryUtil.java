package com.alana.wheretonext.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

        String jsonFileString = CountryUtil.getJsonFromAssets(context.getApplicationContext(), "countries.json");

        if (!jsonFileString.isEmpty()) {

            countryList.clear();
            countryAndLang.clear();

            try {
                JSONArray countries = new JSONArray(jsonFileString);

                for (int i = 0; i < countries.length(); i++) {
                    JSONObject country = countries.getJSONObject(i);
                    String countryName = country.getString("name");
                    countryList.add(countryName);

                    JSONArray languages = country.getJSONArray("languages");

                    // Grab the first language listed, as it is the most spoken OR official language
                    String language = languages.getJSONObject(0).getString("iso639_1");

                    countryAndLang.put(countryName, language);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
