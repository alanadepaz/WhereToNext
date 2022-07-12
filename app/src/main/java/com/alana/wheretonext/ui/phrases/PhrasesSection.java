package com.alana.wheretonext.ui.phrases;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

import com.alana.wheretonext.R;
import com.alana.wheretonext.data.models.FavoritePhrase;

public class PhrasesSection extends Section {

    public static final String TAG = "PhrasesSection";
    private List<FavoritePhrase> favePhrasesList;
    private List<String> translations;
    private String countryName;

    public PhrasesSection(@NonNull String countryName, @NonNull List<FavoritePhrase> favePhrasesList, List<String> translations) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_phrase)
                .headerResourceId(R.layout.section_header)
                .build());

        this.favePhrasesList = favePhrasesList;
        this.translations = translations;
        this.countryName = countryName;
    }

    public void addFavePhraseAndTranslation(FavoritePhrase favoritePhrase, String translation) {
        favePhrasesList.add(favoritePhrase);
        translations.add(translation);
    }

    public boolean removeFavePhraseAndTranslation(FavoritePhrase favoritePhrase) {
        Log.d(TAG, "Length: " + favePhrasesList.size());

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

        public FavePhraseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTranslatedText = itemView.findViewById(R.id.tvTranslatedText);
            tvPhrase = itemView.findViewById(R.id.tvPhrase);
            btnFavePhrase = itemView.findViewById(R.id.btnFavePhrase);

        }

        public void bind(FavoritePhrase favePhrase, String translation) {
            Log.d(TAG, "In favorites bind method.");
            // Bind the post data to the view elements
            tvPhrase.setText(favePhrase.getFavoritePhrase());
            tvTranslatedText.setText(translation);

            Log.d(TAG, favePhrase.getFavoritePhrase() + countryName);
            btnFavePhrase.setVisibility(View.GONE);
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
