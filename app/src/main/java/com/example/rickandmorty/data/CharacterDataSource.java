package com.example.rickandmorty.data;

import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;
import com.example.rickandmorty.api.RickAndMortyAPI;
import com.example.rickandmorty.model.Character;
import com.example.rickandmorty.model.CharacterPage;
import com.example.rickandmorty.model.FilterType;
import com.example.rickandmorty.model.NetworkState;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

/**
 * This class for managing data source of Rick And Morty API
 * @see #page is using to know next page
 * @see #filterPair is using for filtering data correctly
 * @see androidx.paging.PageKeyedDataSource
 */

public class CharacterDataSource extends PageKeyedDataSource<Long, Character> {
    private final RickAndMortyAPI rickAndMortyAPI;
    private final MutableLiveData<NetworkState> networkState;
    private final Pair<FilterType, String> filterPair;
    private int page = 1;
    private Integer totalPage;

    public CharacterDataSource(RickAndMortyAPI rickAndMortyAPI, Pair<FilterType, String> filterPair) {
        this.filterPair = filterPair;
        this.rickAndMortyAPI = rickAndMortyAPI;
        this.networkState = new MutableLiveData<>();
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull final LoadInitialCallback<Long, Character> callback) {
        networkState.postValue(NetworkState.LOADING);
        //We should check current filter status for every initial or after loading tasks
        if (filterPair.first == FilterType.None) {
            rickAndMortyAPI.characterPage(page).enqueue(onInitialCallbackResult(callback));
        } else if (filterPair.first == FilterType.Status) {
            rickAndMortyAPI.characterFilterByStatus(filterPair.second, page).enqueue(onInitialCallbackResult(callback));
        } else if (filterPair.first == FilterType.Name) {
            rickAndMortyAPI.characterFilterByName(filterPair.second, page).enqueue(onInitialCallbackResult(callback));
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Character> callback) {

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Long> params, @NonNull final LoadCallback<Long, Character> callback) {
        //If we fetched all pages, we won't fetch data anymore
        if (page > totalPage) {
            return;
        }

        networkState.postValue(NetworkState.LOADING);
        if (filterPair.first == FilterType.None) {
            rickAndMortyAPI.characterPage(page).enqueue(onAfterCallbackResult(callback));
        } else if (filterPair.first == FilterType.Status) {
            rickAndMortyAPI.characterFilterByStatus(filterPair.second, page).enqueue(onAfterCallbackResult(callback));
        } else if (filterPair.first == FilterType.Name) {
            rickAndMortyAPI.characterFilterByName(filterPair.second, page).enqueue(onAfterCallbackResult(callback));
        }
    }

    private Callback<CharacterPage> onAfterCallbackResult(LoadCallback<Long, Character> callback) {
        return new Callback<CharacterPage>() {
            @Override
            public void onResponse(@NonNull Call<CharacterPage> call, @NonNull Response<CharacterPage> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    callback.onResult(arrangeCharacterList(response), (long) (page));
                    page++;
                } else {
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CharacterPage> call, @NonNull Throwable t) {
                showError(t);
            }
        };
    }

    private Callback<CharacterPage> onInitialCallbackResult(LoadInitialCallback<Long, Character> callback) {
        return new Callback<CharacterPage>() {
            @Override
            public void onResponse(@NonNull Call<CharacterPage> call, @NonNull Response<CharacterPage> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    callback.onResult(arrangeCharacterList(response), null, (long) 2);
                    page++;
                } else {
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CharacterPage> call, @NonNull Throwable t) {
                showError(t);
            }
        };
    }

    private void showError(Throwable t) {
        String errorMessage = t.getMessage();
        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
    }


    private List<Character> arrangeCharacterList(Response<CharacterPage> response) {
        List<Character> characterList;
        networkState.postValue(NetworkState.LOADED);
        CharacterPage characterPage = response.body();
        assert characterPage != null;
        characterList = characterPage.getResults();
        totalPage = characterPage.getInfo().getPages();
        //Mapping favorite characters
        for (Character character : characterList) {
            character.setFavorite(PrefManager.getInstance().isCharacterFavorite(character.getId()));
        }
        return characterList;
    }
}