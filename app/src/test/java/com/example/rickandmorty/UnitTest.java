package com.example.rickandmorty;

import android.util.Pair;
import com.example.rickandmorty.api.RetrofitClient;
import com.example.rickandmorty.api.RickAndMortyAPI;
import com.example.rickandmorty.data.CharacterDataSource;
import com.example.rickandmorty.data.CharacterDataSourceFactory;
import com.example.rickandmorty.model.FilterType;
import com.example.rickandmorty.viewmodel.CharacterItemViewModel;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnitTest {
    @Test
    public void testMVVM() {
        CharacterItemViewModel characterItemViewModel = new CharacterItemViewModel();
        assertNotNull(characterItemViewModel.getCharacterList());

        RickAndMortyAPI rickAndMortyAPI = RetrofitClient.getInstance().create(RickAndMortyAPI.class);
        CharacterDataSourceFactory characterDataSourceFactory = new CharacterDataSourceFactory(rickAndMortyAPI);
        assertNotNull(characterDataSourceFactory.getMutableLiveData());
        Pair<FilterType, String> filterPair = new Pair<>(FilterType.None, "");

        CharacterDataSource characterDataSource = new CharacterDataSource(rickAndMortyAPI, filterPair);
        assertNotNull(characterDataSource);
        characterDataSource.invalidate();
        assertTrue(characterDataSource.isInvalid());
    }
}