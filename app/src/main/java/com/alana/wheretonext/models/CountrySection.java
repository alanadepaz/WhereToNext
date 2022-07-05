package com.alana.wheretonext.models;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

import com.alana.wheretonext.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class CountrySection extends Section {

    public static final String TAG = "CountrySection";
    private List<FavoritePhrase> favePhrasesList;
    private List<String> translations;
    private String countryName;

    private Context context;

    public CountrySection(@NonNull String countryName, @NonNull List<FavoritePhrase> favePhrasesList, List<String> translations) {
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
            tvPhrase.setText(favePhrase.getFavoritePhrase().getPhrase());
            tvTranslatedText.setText(translation);

            SharedPreferences sharedPrefs = itemView.getContext().getSharedPreferences("com.alana.wheretonext", Context.MODE_PRIVATE);
            Log.d(TAG, favePhrase.getFavoritePhrase().getPhrase() + countryName);
            btnFavePhrase.setChecked(sharedPrefs.getBoolean(favePhrase.getFavoritePhrase().getPhrase() + countryName, true));

            btnFavePhrase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    unFavoritePhrase(ParseUser.getCurrentUser(), countryName, favePhrase.getFavoritePhrase());

                    SharedPreferences.Editor editor = itemView.getContext().getSharedPreferences("com.alana.wheretonext", MODE_PRIVATE).edit();
                    editor.putBoolean(favePhrase.getFavoritePhrase().getPhrase() + countryName, false);
                    editor.commit();
                }
            });
        }

        private void unFavoritePhrase(ParseUser currentUser, String countryName, Phrase phrase) {
            ParseQuery<FavoritePhrase> query = ParseQuery.getQuery("FavoritePhrase");
            query.whereEqualTo("user", currentUser);
            query.whereEqualTo("countryName", countryName);
            query.whereEqualTo("favoritePhrase", phrase);
            query.findInBackground(new FindCallback<FavoritePhrase>() {
                @Override
                public void done(List<FavoritePhrase> objects, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issue with getting phrases", e);
                        return;
                    }

                    for (FavoritePhrase phraseToUnfavorite : objects) {
                        try {
                            phraseToUnfavorite.delete();
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        phraseToUnfavorite.saveInBackground();
                    }
                }
            });
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
