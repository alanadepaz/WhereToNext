package com.alana.wheretonext.network;

import static com.parse.Parse.getApplicationContext;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import com.alana.wheretonext.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Cache;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TranslationClient {

    public static final String TAG = "Translation Client";

    public static String getTranslation(String textToTranslate, String languageToTranslateTo) {

        String translationResponse = "";

        // TODO: CHECK IF THE TRANSLATIONS ARE ACTUALLY GETTING CACHED
        //File cacheDir = getApplicationContext().getCacheDir();
        //long cacheSize = 10L * 1024L * 1024L; // 10 MiB

        String url = "https://translation.googleapis.com/language/translate/v2?key=" + BuildConfig.GOOGLE_API_KEY;
        final MediaType JSON
                = MediaType.get("application/json; charset=utf-8");

//        OkHttpClient client = new OkHttpClient.Builder()
//                .cache(new Cache(cacheDir, cacheSize))
//                .build();

        OkHttpClient client = new OkHttpClient();

        String requestBody = "{'q': '" + textToTranslate + "', 'target': '" + languageToTranslateTo + "'}";

        RequestBody body = RequestBody.create(requestBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {

            Log.d(TAG, "Received response from server.");
            try {
                JSONObject translationObject = new JSONObject(response.body().string());
                JSONObject data = translationObject.getJSONObject("data");
                JSONArray translations = data.getJSONArray("translations");
                JSONObject textAndSourceLanguage = translations.getJSONObject(0);
                translationResponse = textAndSourceLanguage.getString("translatedText");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return translationResponse;
    }
}
