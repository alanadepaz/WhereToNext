package com.alana.wheretonext;

import androidx.appcompat.app.AppCompatActivity;

//TODO: DELETE THIS CLASS WHEN DONE
public class MainActivity extends AppCompatActivity {

    /*
    private EditText inputToTranslate;
    private TextView translatedTv;
    private String originalText;
    private String translatedText;
    private boolean connected;
    Translate translate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputToTranslate = findViewById(R.id.inputToTranslate);
        translatedTv = findViewById(R.id.translatedTv);
        Button translateButton = findViewById(R.id.translateButton);

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if (checkInternetConnection()) {

                    //If there is internet connection, get translate service and start translation:
                    getTranslateService();
                    translate();

                //} else {

                    //If not, display "no connection" warning:
                //    translatedTv.setText(getResources().getString(R.string.no_connection));
                //}

            }
        });
    }

    public void getTranslateService() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.credentials)) {

            //Get credentials:
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }

    public void translate() {

        //Get input text to be translated:
        originalText = inputToTranslate.getText().toString();
        Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage("tr"), Translate.TranslateOption.model("base"));
        translatedText = translation.getTranslatedText();

        //Translated text and original text are set to TextViews:
        translatedTv.setText(translatedText);

    }

    /*
    public boolean checkInternetConnection() {

        //Check internet connection:
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Means that we are connected to a network (mobile or wi-fi)
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }

     */
}