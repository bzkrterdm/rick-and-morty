package com.example.rickandmorty.data;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class PrefManager {
    private static final String FAVORITE_KEY = "favorite_list";
    private static PrefManager prefManager = null;
    private SharedPreferences sharedPreferences;

    public static PrefManager getInstance() {
        if (prefManager == null) {
            prefManager = new PrefManager();
        }

        return prefManager;
    }

    public void Initialize(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public boolean isCharacterFavorite(int id) {
        Set<String> favoriteSet = sharedPreferences.getStringSet(FAVORITE_KEY, new HashSet<>());
        return favoriteSet.contains(String.valueOf(id));
    }

    public boolean changeCharacterFavorite(int id) {
        Set<String> favoriteSet = sharedPreferences.getStringSet(FAVORITE_KEY, new HashSet<>());
        String idString = String.valueOf(id);
        boolean isFavorite = favoriteSet.contains(idString);
        if (isFavorite) {
            favoriteSet.remove(idString);
        } else {
            favoriteSet.add(idString);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(FAVORITE_KEY);
        editor.apply();
        editor.putStringSet(FAVORITE_KEY, favoriteSet);
        editor.apply();
        return !isFavorite;
    }
}
