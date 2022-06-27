package com.alana.wheretonext.phrases;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alana.wheretonext.models.Country;
import com.alana.wheretonext.models.Phrase;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import com.alana.wheretonext.R;

public class PhrasesAdapter extends RecyclerView.Adapter<PhrasesAdapter.ViewHolder> {

    public static final String TAG = "PhrasesAdapter";
    private Context context;
    private List<Phrase> phrases;
    private Button btnFavePhrase;

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
        //holder.bind(phrase);
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

            btnFavePhrase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(),"Phrase favorited!", Toast.LENGTH_LONG).show();
                    favoritePhrase(phrase);
                }
            });
        }

        private void favoritePhrase(Phrase phrase)
        {
            ParseQuery<Country> query = ParseQuery.getQuery(Country.class);

            query.whereEqualTo(Country.KEY_COUNTRY_NAME, countryName);
            query.getFirstInBackground(new GetCallback<Country>() {
                @Override
                public void done(Country country, ParseException e) {
                    if (e == null) {
                        // Country exists. Simply add phrase.
                        country.addFavePhrase(phrase);
                        country.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e(TAG, "Error while saving", e);
                                    Toast.makeText(context, "Error while saving!", Toast.LENGTH_SHORT).show();
                                }
                                Log.i(TAG, "Favorite country and phrase save was successful!");
                            }
                        });
                    }
                    else {
                        if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                            // Country doesn't exist. Create a new country.
                            // Save the phrase to a country
                            country = new Country();
                            country.setCountryName(countryName);
                            country.setLanguage(language);
                            country.addFavePhrase(phrase);
                            country.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        Log.e(TAG, "Error while saving", e);
                                        Toast.makeText(context, "Error while saving!", Toast.LENGTH_SHORT).show();
                                    }
                                    Log.i(TAG, "Favorite country and phrase save was successful!");
                                }
                            });
                        }
                        else {
                            Log.e(TAG, "Unknown error!");
                        }
                    }
                }
            });

            // TODO: Must also save the country to the user through a many-to-many relation.

        }
    }
}
