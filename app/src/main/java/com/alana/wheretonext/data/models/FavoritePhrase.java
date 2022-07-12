package com.alana.wheretonext.data.models;

public class FavoritePhrase {

    private String countryName;
    private String languageCode;
    private String favoritePhrase;

    public FavoritePhrase(String countryName, String languageCode, String favoritePhrase) {
        this.countryName = countryName;
        this.languageCode = languageCode;
        this.favoritePhrase = favoritePhrase;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getFavoritePhrase() {
        return favoritePhrase;
    }

    public void setFavoritePhrase(String favoritePhrase) {
        this.favoritePhrase = favoritePhrase;
    }
}
