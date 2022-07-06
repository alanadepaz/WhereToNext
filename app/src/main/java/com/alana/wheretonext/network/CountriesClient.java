package com.alana.wheretonext.network;

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
    public static void getTranslation(String data, ArrayList<String> countryList, Map<String, String> countryAndLang) {

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
    }
}
