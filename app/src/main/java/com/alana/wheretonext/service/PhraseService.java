package com.alana.wheretonext.service;

import android.util.Log;
import android.widget.Toast;

import com.alana.wheretonext.data.db.models.ParseFavoritePhrase;
import com.alana.wheretonext.data.db.models.ParsePhrase;
import com.alana.wheretonext.data.models.FavoritePhrase;
import com.alana.wheretonext.ui.phrases.PhrasesSection;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhraseService {

    public FavoritePhrase getFavoritePhrase(String countryName, String phrase) {

        try {
            ParsePhrase parsePhrase = getParsePhrase(phrase);

            ParseQuery<ParseFavoritePhrase> query = ParseQuery.getQuery("FavoritePhrase");
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.whereEqualTo("countryName", countryName);
            query.whereEqualTo("favoritePhrase", parsePhrase);

            List<ParseFavoritePhrase> favePhraseList = query.find();

            if (favePhraseList.size() > 0) {
                ParseFavoritePhrase firstFavePhrase = favePhraseList.get(0);
                FavoritePhrase favoritePhrase = new FavoritePhrase(firstFavePhrase.getCountryName(),
                        firstFavePhrase.getLanguageCode(),
                        firstFavePhrase.getFavoritePhrase().getPhrase());

                return favoritePhrase;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<FavoritePhrase> getFavoritePhrases(String countryName) {
        // Specify which class to query
        ParseQuery<ParseFavoritePhrase> query = ParseQuery.getQuery(ParseFavoritePhrase.class);

        query.setLimit(20);
        // Get all the favorite phrases from one user
        query.whereEqualTo(ParseFavoritePhrase.KEY_USER, ParseUser.getCurrentUser());
        query.orderByAscending("countryName");

        List<FavoritePhrase> favoritePhraseList = new ArrayList<>();

        List<ParseFavoritePhrase> parseFavoritePhraseList = null;
        try {
            parseFavoritePhraseList = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (ParseFavoritePhrase parseFavoritePhrase : parseFavoritePhraseList) {
            FavoritePhrase favoritePhrase = new FavoritePhrase(parseFavoritePhrase.getCountryName(),
                    parseFavoritePhrase.getLanguageCode(),
                    parseFavoritePhrase.getFavoritePhrase().getPhrase());

            favoritePhraseList.add(favoritePhrase);
        }

        return favoritePhraseList;
    }



    public List<String> getPhrases() {
        List<String> allPhrases = new ArrayList<>();

        // Specify which class to query
        ParseQuery<ParsePhrase> query = ParseQuery.getQuery(ParsePhrase.class);

        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");

        try {
            List<ParsePhrase> phraseList = query.find();

            for (ParsePhrase phrase : phraseList) {
                allPhrases.add(phrase.getPhrase());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return allPhrases;
    }

    private ParsePhrase getParsePhrase(String phrase) throws ParseException {

        // Specify which class to query
        ParseQuery<ParsePhrase> query = ParseQuery.getQuery(ParsePhrase.class);

        // order posts by creation date (newest first)
        query.whereEqualTo("phrase", phrase);

        ParsePhrase parsePhrase = query.getFirst();
        return parsePhrase;
    }

    public void favoritePhrase(FavoritePhrase phraseToFavorite) {
        ParseFavoritePhrase parseFavePhrase = new ParseFavoritePhrase();
        parseFavePhrase.setUser(ParseUser.getCurrentUser());
        parseFavePhrase.setCountryName(phraseToFavorite.getCountryName());
        parseFavePhrase.setLanguageCode(phraseToFavorite.getLanguageCode());

        try {
            parseFavePhrase.setFavoritePhrase(getParsePhrase(phraseToFavorite.getFavoritePhrase()));
            parseFavePhrase.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void unFavoritePhrase(FavoritePhrase phraseToUnfavorite) {
        ParseQuery<ParseFavoritePhrase> query = ParseQuery.getQuery("FavoritePhrase");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("countryName", phraseToUnfavorite.getCountryName());

        try {
            query.whereEqualTo("favoritePhrase", getParsePhrase(phraseToUnfavorite.getFavoritePhrase()));
            List<ParseFavoritePhrase> parseFavoritePhrases = query.find();

            for (ParseFavoritePhrase phraseToRemove : parseFavoritePhrases) {
                phraseToRemove.delete();
                phraseToRemove.save();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
