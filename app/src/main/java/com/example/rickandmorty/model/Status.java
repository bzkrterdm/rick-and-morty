package com.example.rickandmorty.model;

import androidx.annotation.NonNull;

public enum Status {
    Alive("alive"),
    Dead("dead"),
    Unknown("unknown");

    private final String text;

    Status(final String text) {
        this.text = text;
    }

    @NonNull
    @Override
    public String toString() {
        return text;
    }
}
