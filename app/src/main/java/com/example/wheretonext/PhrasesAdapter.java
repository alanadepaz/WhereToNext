package com.example.wheretonext;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseObject;
import com.parse.ParseRelation;

import java.util.List;

public class PhrasesAdapter extends RecyclerView.Adapter<PhrasesAdapter.ViewHolder> {

    private Context context;
    private List<Phrase> phrases;
    private Button btnFavePhrase;

    public PhrasesAdapter(Context context, List<Phrase> phrases) {
        this.context = context;
        this.phrases = phrases;
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
        holder.bind(phrase);
    }

    @Override
    public int getItemCount() {
        return phrases.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvPhrase;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPhrase = itemView.findViewById(R.id.tvPhrase);
            btnFavePhrase = itemView.findViewById(R.id.btnFavePhrase);
        }

        public void bind(Phrase phrase) {
            // Bind the post data to the view elements
            tvPhrase.setText(phrase.getPhrase());

            btnFavePhrase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(),"Phrase favorited!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
