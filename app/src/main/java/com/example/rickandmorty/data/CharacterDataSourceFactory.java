package com.example.rickandmorty.data;

import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import com.example.rickandmorty.api.RickAndMortyAPI;
import com.example.rickandmorty.model.Character;
import com.example.rickandmorty.model.FilterType;
import com.example.rickandmorty.model.FilterChanger;

public class CharacterDataSourceFactory extends DataSource.Factory<Long, Character> implements FilterChanger {
    CharacterDataSource characterDataSource;
    MutableLiveData<CharacterDataSource> mutableLiveData;
    RickAndMortyAPI rickAndMortyAPI;
    Pair<FilterType, String> filterPair = new Pair<>(FilterType.None, null);

    public CharacterDataSourceFactory(RickAndMortyAPI rickAndMortyAPI) {
        this.rickAndMortyAPI = rickAndMortyAPI;
        mutableLiveData = new MutableLiveData<>();
    }

    @NonNull
    @Override
    public DataSource<Long, Character> create() {
        characterDataSource = new CharacterDataSource(rickAndMortyAPI, filterPair);
        mutableLiveData.postValue(characterDataSource);
        return characterDataSource;
    }

    public MutableLiveData<CharacterDataSource> getMutableLiveData() {
        return mutableLiveData;
    }

    @Override
    public void changeFilter(Pair<FilterType, String> filterPair) {
        //When change filter, invalidating data source. Like reload.
        this.filterPair = filterPair;
        characterDataSource.invalidate();
    }
}
