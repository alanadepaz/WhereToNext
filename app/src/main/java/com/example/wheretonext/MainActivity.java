package com.example.wheretonext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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
import java.sql.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private Button btnToMap;
    ArrayList<String> countryList;
    ArrayAdapter<String> listAdapter;
    Handler mainHandler = new Handler();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new fetchData().start();
        initializeCountryList();

        btnToMap = findViewById(R.id.btnToMap);

        btnToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "btnToMap clicked");
                goMapActivity();
            }
        });
    }

    private void initializeCountryList() {

        countryList = new ArrayList<>();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countryList);
        ListView listView = (ListView) findViewById(R.id.countryList);
        listView.setAdapter(listAdapter);

        listAdapter.addAll(countryList);
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

    private void goMapActivity() {
        Intent i = new Intent(this, MapActivity.class);
        startActivity(i);
    }

    class fetchData extends Thread{

        String data = "";

        @Override
        public void run() {
            super.run();

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(MainActivity.this);
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