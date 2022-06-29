package com.alana.wheretonext.models;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.utils.EmptyViewHolder;

import com.alana.wheretonext.R;

public class CountrySection extends Section {

    private List<FavoritePhrase> favePhrasesList;
    private List<String> translations;
    private String countryName;

    public CountrySection(@NonNull List<FavoritePhrase> favePhrasesList, List<String> translations, @NonNull String countryName) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_phrase)
                .headerResourceId(R.layout.section_header)
                .build());

        this.favePhrasesList = favePhrasesList;
        this.translations = translations;
        this.countryName = countryName;
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
        public Button btnFavePhrase;

        public FavePhraseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTranslatedText = itemView.findViewById(R.id.tvTranslatedText);
            tvPhrase = itemView.findViewById(R.id.tvPhrase);
            btnFavePhrase = itemView.findViewById(R.id.btnFavePhrase);

        }

        public void bind(FavoritePhrase favePhrase, String translation) {
            // Bind the post data to the view elements
            tvPhrase.setText(favePhrase.getFavoritePhrase().getPhrase());
            tvTranslatedText.setText(translation);

            btnFavePhrase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unFavoritePhrase();
                }
            });
        }

        private void unFavoritePhrase() {

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
