package com.alana.wheretonext.phrases;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alana.wheretonext.models.FavoritePhrase;
import com.alana.wheretonext.models.Phrase;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Locale;

import com.alana.wheretonext.R;

public class PhrasesAdapter extends RecyclerView.Adapter<PhrasesAdapter.ViewHolder> {

    public static final String TAG = "PhrasesAdapter";
    private Context context;
    private List<Phrase> phrases;
    private ToggleButton btnFavePhrase;

    private String countryName;
    private String language;

    private List<String> translations;


    public PhrasesAdapter(Context context, List<Phrase> phrases, String countryName, String language, List<String> translations) {
        this.context = context;
        this.phrases = phrases;
        this.countryName = countryName;
        this.language = language;
        this.translations = translations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_phrase, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Phrase phrase = phrases.get(position);
        String translation = translations.get(position);
        holder.bind(phrase, translation);
    }

    @Override
    public int getItemCount() {
        return phrases.size();
    }

    public List<Phrase> getPhrases() {
        return phrases;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTranslatedText;
        public TextView tvPhrase;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTranslatedText = itemView.findViewById(R.id.tvTranslatedText);
            tvPhrase = itemView.findViewById(R.id.tvPhrase);
            btnFavePhrase = itemView.findViewById(R.id.btnFavePhrase);
        }

        public void bind(Phrase phrase, String translation) {
            // Bind the post data to the view elements
            tvPhrase.setText(phrase.getPhrase());
            tvTranslatedText.setText(translation);

            // If the language of the country to travel to is the same as the one the user speaks
            if (language == null || Locale.getDefault().getLanguage().equals(language)) {
                tvTranslatedText.setText(phrase.getPhrase());
                tvPhrase.setVisibility(View.GONE);
                tvTranslatedText.setTextSize(20);
                tvTranslatedText.setGravity(Gravity.CENTER_VERTICAL);
            }

            SharedPreferences sharedPrefs = context.getSharedPreferences("com.alana.wheretonext", Context.MODE_PRIVATE);
            btnFavePhrase.setChecked(sharedPrefs.getBoolean(phrase.getPhrase() + countryName, false));

            btnFavePhrase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                    {
                        SharedPreferences.Editor editor = context.getSharedPreferences("com.alana.wheretonext", MODE_PRIVATE).edit();
                        editor.putBoolean(phrase.getPhrase() + countryName, true);
                        editor.commit();

                        favoritePhrase(ParseUser.getCurrentUser(), countryName, language, phrase);
                    }
                    else
                    {
                        SharedPreferences.Editor editor = context.getSharedPreferences("com.alana.wheretonext", MODE_PRIVATE).edit();
                        editor.putBoolean(phrase.getPhrase() + countryName, false);
                        editor.commit();

                        unFavoritePhrase(ParseUser.getCurrentUser(), countryName, phrase);
                    }
                    Log.d(TAG, String.valueOf(sharedPrefs.getBoolean(phrase.getPhrase() + countryName, false)));
                }
            });
        }

        private void favoritePhrase(ParseUser currentUser, String countryName, String languageCode, Phrase phrase) {
            FavoritePhrase favePhrase = new FavoritePhrase();
            favePhrase.setUser(currentUser);
            favePhrase.setCountryName(countryName);
            favePhrase.setLanguageCode(languageCode);
            favePhrase.setFavoritePhrase(phrase);

            favePhrase.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while saving", e);
                        Toast.makeText(context, "Error while saving!", Toast.LENGTH_SHORT).show();
                    }
                    Log.i(TAG, "Post save was successful!");
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
}
