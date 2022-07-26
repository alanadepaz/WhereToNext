package com.alana.wheretonext.ui.phrases;

import android.content.Context;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

import com.alana.wheretonext.R;
import com.alana.wheretonext.data.models.FavoritePhrase;

public class PhrasesSection extends Section {

    public static final String TAG = "PhrasesSection";
    private Context context;
    private List<FavoritePhrase> favePhrasesList;
    private List<String> translations;
    private String countryName;
    private TextToSpeech tts;
    private String language;

    public PhrasesSection(@NonNull String countryName, @NonNull List<FavoritePhrase> favePhrasesList, List<String> translations, String language, TextToSpeech tts) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_phrase)
                .headerResourceId(R.layout.section_header)
                .build());

        this.favePhrasesList = favePhrasesList;
        this.translations = translations;
        this.countryName = countryName;
        this.language = language;
        this.tts = tts;
    }

    public void addFavePhraseAndTranslation(FavoritePhrase favoritePhrase, String translation) {
        favePhrasesList.add(favoritePhrase);
        translations.add(translation);
    }

    public boolean removeFavePhraseAndTranslation(FavoritePhrase favoritePhrase) {

        int i = 0;
        for (i = 0; i < favePhrasesList.size(); i++) {
            FavoritePhrase currFavePhrase = favePhrasesList.get(i);
            if (favoritePhrase.getFavoritePhrase().equals(currFavePhrase.getFavoritePhrase())
            && favoritePhrase.getCountryName().equals(currFavePhrase.getCountryName())
            && favoritePhrase.getLanguageCode().equals(currFavePhrase.getLanguageCode())) {
                break;
            }
        }

        favePhrasesList.remove(i);
        translations.remove(i);

        return (favePhrasesList.size() == 0);
    }

    public String getCountryName() {
        return countryName;
    }

    @Override
    public int getContentItemsTotal() {
        return favePhrasesList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new FavePhraseViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        FavePhraseViewHolder favePhraseViewHolder = (FavePhraseViewHolder) holder;

        FavoritePhrase favePhrase = favePhrasesList.get(position);
        String translation = translations.get(position);
        ((FavePhraseViewHolder) holder).bind(favePhrase, translation);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.tvCountryHeader.setText(countryName);
    }

    private class FavePhraseViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTranslatedText;
        public TextView tvPhrase;
        public ToggleButton btnFavePhrase;
        public ImageButton btnAudio;

        public FavePhraseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTranslatedText = itemView.findViewById(R.id.tvTranslatedText);
            tvPhrase = itemView.findViewById(R.id.tvPhrase);
            btnFavePhrase = itemView.findViewById(R.id.btnFavePhrase);
            btnAudio = itemView.findViewById(R.id.btnAudio);
        }

        public void bind(FavoritePhrase favePhrase, String translation) {
            // Bind the post data to the view elements
            tvPhrase.setText(favePhrase.getFavoritePhrase());
            tvTranslatedText.setText(translation);

            btnFavePhrase.setVisibility(View.GONE);

            btnAudio.setEnabled(true);
            btnAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLikeAnimation(v);
                    speak(language);
                }
            });
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

        private void startLikeAnimation(final View view){
            Animation animation = AnimationUtils.loadAnimation(btnAudio.getContext(), R.anim.scale);
            view.startAnimation(animation);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCountryHeader;

        public HeaderViewHolder(View view) {
            super(view);

            tvCountryHeader = view.findViewById(R.id.tvCountryHeader);
        }
    }
}
