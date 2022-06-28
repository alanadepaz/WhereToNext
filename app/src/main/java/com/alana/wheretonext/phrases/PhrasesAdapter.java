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
import com.alana.wheretonext.models.FavoritePhrase;
import com.alana.wheretonext.models.Phrase;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
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
                    favoritePhrase(ParseUser.getCurrentUser(), countryName, language, phrase);
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
    }
}
