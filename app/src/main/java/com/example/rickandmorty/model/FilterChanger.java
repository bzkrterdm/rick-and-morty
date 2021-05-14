package com.example.rickandmorty.model;

import android.util.Pair;

public interface FilterChanger {
    default void changeFilter(Pair<FilterType, String> filterPair) {
    }
}
