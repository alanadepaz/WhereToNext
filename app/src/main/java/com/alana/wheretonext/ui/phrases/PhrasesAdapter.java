package com.alana.wheretonext.ui.phrases;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alana.wheretonext.data.models.FavoritePhrase;
import com.alana.wheretonext.service.PhraseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.alana.wheretonext.R;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class PhrasesAdapter extends RecyclerView.Adapter<PhrasesAdapter.ViewHolder> {

    public static final String TAG = "PhrasesAdapter";

    private PhraseService phraseService = new PhraseService();

    private Context context;
    private List<String> phrases;
    private ToggleButton btnFavePhrase;

    private String countryName;
    private String language;

    private List<String> translations;

    private SectionedRecyclerViewAdapter favePhrasesAdapter;

    private static TextToSpeech tts;

    public PhrasesAdapter(Context context,
                          List<String> phrases,
                          String countryName,
                          String language,
                          List<String> translations,
                          SectionedRecyclerViewAdapter favePhrasesAdapter) {
        this.context = context;
        this.phrases = phrases;
        this.countryName = countryName;
        this.language = language;
        this.translations = translations;
        this.favePhrasesAdapter = favePhrasesAdapter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_phrase, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String phrase = phrases.get(position);
        String translation = translations.get(position);
        Log.i(TAG, "Phrase before bind: " + phrase);
        Log.i(TAG, "Translation before bind: " + translation);

        holder.bind(phrase, translation);
    }


    @Override
    public int getItemCount() {
        return phrases.size();
    }

    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    public TextToSpeech setUpTTS() {
        // Set up Text To Speech method
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale currLocale;
                    if (language != null) {
                        currLocale = new Locale(language);
                    }
                    else {
                        currLocale = Locale.getDefault();
                    }

                    int result = tts.setLanguage(currLocale);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        openDialog();
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        return tts;
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Proper audio pronunciation unavailable for this language. Would you like to try to download the language off the Google Play Store?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent installIntent = new Intent();
                        installIntent.setAction(
                                TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                        context.startActivity(installIntent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTranslatedText;
        public TextView tvPhrase;

        private ImageButton btnAudio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTranslatedText = itemView.findViewById(R.id.tvTranslatedText);
            tvPhrase = itemView.findViewById(R.id.tvPhrase);
            btnFavePhrase = itemView.findViewById(R.id.btnFavePhrase);
            btnAudio = itemView.findViewById(R.id.btnAudio);

        }

        public void bind(String phrase, String translation) {
            Log.d(TAG, "In bind method");
            // Bind the post data to the view elements
            tvPhrase.setText(phrase);
            tvTranslatedText.setText(translation);
            
            // If the language of the country to travel to is the same as the one the user speaks
            if (language == null || Locale.getDefault().getLanguage().equals(language)) {
                tvTranslatedText.setText(phrase);
                tvPhrase.setVisibility(View.GONE);
                tvTranslatedText.setTextSize(20);
                tvTranslatedText.setGravity(Gravity.CENTER_VERTICAL);
            }

            btnAudio.setEnabled(true);
            btnAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Audio button clicked");
                    speak(language);
                }
            });

            SharedPreferences sharedPrefs = context.getSharedPreferences("com.alana.wheretonext", Context.MODE_PRIVATE);
            Log.d(TAG, phrase + countryName);

            btnFavePhrase.setChecked(sharedPrefs.getBoolean(phrase + countryName, false));

            btnFavePhrase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        SharedPreferences.Editor editor = context.getSharedPreferences("com.alana.wheretonext", MODE_PRIVATE).edit();
                        editor.putBoolean(phrase + countryName, true);
                        editor.commit();

                        FavoritePhrase favePhrase = new FavoritePhrase(countryName, language, phrase);
                        phraseService.favoritePhrase(favePhrase);

                        addToFavePhrasePanel(favePhrase, translation);

                    } else {
                        SharedPreferences.Editor editor = context.getSharedPreferences("com.alana.wheretonext", MODE_PRIVATE).edit();
                        editor.putBoolean(phrase + countryName, false);
                        editor.commit();

                        FavoritePhrase favePhrase = phraseService.getFavoritePhrase(countryName, phrase);

                        removeFromFavePhrasePanel(favePhrase);

                        phraseService.unFavoritePhrase(favePhrase);
                    }
                    Log.d(TAG, String.valueOf(sharedPrefs.getBoolean(phrase + countryName, false)));
                }
            });
        }

        private void addToFavePhrasePanel(FavoritePhrase favePhrase, String translation) {

            PhrasesSection phrasesSection = getPhrasesSection();
            if (phrasesSection != null)
            {
                phrasesSection.addFavePhraseAndTranslation(favePhrase, translation);
            }
            else {

                List<FavoritePhrase> favePhrases = new ArrayList<>();
                favePhrases.add(favePhrase);

                List<String> faveTranslations = new ArrayList<>();
                faveTranslations.add(translation);

                favePhrasesAdapter.addSection(new PhrasesSection(countryName, favePhrases, faveTranslations, language, tts));
            }
        }

        private void removeFromFavePhrasePanel(FavoritePhrase favePhrase) {
            Log.d(TAG, "Country: " + favePhrase.getCountryName());
            PhrasesSection phrasesSection = getPhrasesSection();

            if (phrasesSection != null)
            {
                boolean listEmpty = phrasesSection.removeFavePhraseAndTranslation(favePhrase);
                if (listEmpty) {
                    // TODO: remove header

                }
            }
        }

        private PhrasesSection getPhrasesSection() {
            for (int i = 0; i < favePhrasesAdapter.getSectionCount(); i++) {
                PhrasesSection phrasesSection = (PhrasesSection) favePhrasesAdapter.getSection(i);

                // Section exists
                if (phrasesSection.getCountryName().equals(countryName)) { return phrasesSection; }
            }
            return null;
        }

        private void speak(String language) {
            String text = tvTranslatedText.getText().toString();
            float pitch = (float) 1;
            float speed = (float) 1;

            Locale currLocale;
            if (language != null) {
                currLocale = new Locale(language);
            } else {
                currLocale = Locale.getDefault();
            }

            tts.setLanguage(currLocale);

            tts.setPitch(pitch);
            tts.setSpeechRate(speed);
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
