package com.example.rickandmorty.viewmodel;

import android.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import com.example.rickandmorty.api.RetrofitClient;
import com.example.rickandmorty.api.RickAndMortyAPI;
import com.example.rickandmorty.data.CharacterDataSourceFactory;
import com.example.rickandmorty.data.CharacterDataSource;
import com.example.rickandmorty.model.Character;
import com.example.rickandmorty.model.FilterChanger;
import com.example.rickandmorty.model.FilterType;
import com.example.rickandmorty.model.NetworkState;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CharacterItemViewModel extends ViewModel implements FilterChanger {

    private final LiveData<NetworkState> networkStateLiveData;
    private final LiveData<PagedList<Character>> characterList;
    private final CharacterDataSourceFactory factory;

    public CharacterItemViewModel() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        RickAndMortyAPI rickAndMortyAPI = RetrofitClient.getInstance().create(RickAndMortyAPI.class);
        factory = new CharacterDataSourceFactory(rickAndMortyAPI);

        networkStateLiveData = Transformations.switchMap(factory.getMutableLiveData(), CharacterDataSource::getNetworkState);

        PagedList.Config pageConfig = (new PagedList.Config.Builder()).setPageSize(20).build();
        characterList = (new LivePagedListBuilder<>(factory, pageConfig)).setFetchExecutor(executor).build();
    }

    public LiveData<PagedList<Character>> getCharacterList() {
        return characterList;
    }

    public LiveData<NetworkState> getNetworkStateLiveData() {
        return networkStateLiveData;
    }

    @Override
    public void changeFilter(Pair<FilterType, String> filterPair) {
        factory.changeFilter(filterPair);
    }
}

