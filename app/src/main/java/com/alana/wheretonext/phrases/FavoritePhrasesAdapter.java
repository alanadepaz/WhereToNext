package com.alana.wheretonext.phrases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alana.wheretonext.R;
import com.alana.wheretonext.models.FavoritePhrase;
import com.alana.wheretonext.models.Phrase;
import com.parse.ParseUser;

import java.util.List;

public class FavoritePhrasesAdapter extends RecyclerView.Adapter<FavoritePhrasesAdapter.ViewHolder> {

    public static final String TAG = "FavoritePhrasesAdapter";
    private Context context;
    private List<FavoritePhrase> favoritePhrases;
    private List<String> translations;

    public FavoritePhrasesAdapter(Context context, List<FavoritePhrase> favoritePhrases, List<String> translations) {
        this.context = context;
        this.favoritePhrases = favoritePhrases;
        this.translations = translations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_phrase, parent, false);
        return new FavoritePhrasesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoritePhrase favePhrase = favoritePhrases.get(position);
        String translation = translations.get(position);
        holder.bind(favePhrase, translation);
    }

    @Override
    public int getItemCount() {
        return favoritePhrases.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTranslatedText;
        public TextView tvPhrase;
        public Button btnFavePhrase;

        public ViewHolder(@NonNull View itemView) {
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
}
