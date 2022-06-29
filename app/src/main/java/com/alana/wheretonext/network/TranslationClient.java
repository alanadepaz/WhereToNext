package com.alana.wheretonext.network;

import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.alana.wheretonext.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TranslationClient {

    public static String getTranslation(String textToTranslate, String languageToTranslateTo) {
        String translationResponse = "";

        String url = "https://translation.googleapis.com/language/translate/v2?key=" + BuildConfig.GOOGLE_API_KEY;
        final MediaType JSON
                = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String requestBody = "{'q': '" + textToTranslate + "', 'target': '" + languageToTranslateTo + "'}";

        RequestBody body = RequestBody.create(requestBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            translationResponse = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return translationResponse;
    }
}
