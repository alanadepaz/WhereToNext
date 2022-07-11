package com.alana.wheretonext.data.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CountriesClient {
    public static final String COUNTRY_URL = "https://restcountries.com/v2/all?fields=name,languages";


    public static void getCountries(String data, ArrayList<String> countryList, Map<String, String> countryAndLang) {
        try {
            String url = COUNTRY_URL;

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
    }
}
