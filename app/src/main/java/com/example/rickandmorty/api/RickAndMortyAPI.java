package com.example.rickandmorty.api;

import com.example.rickandmorty.model.Episode;
import com.example.rickandmorty.model.CharacterPage;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RickAndMortyAPI {
    @GET(Constants.CHARACTER)
    Call<CharacterPage> characterPage(@Query(Constants.PAGE) int page);

    @GET(Constants.CHARACTER)
    Call<CharacterPage> characterFilterByName(@Query(Constants.NAME) String name, @Query(Constants.PAGE) int page);

    @GET(Constants.CHARACTER)
    Call<CharacterPage> characterFilterByStatus(@Query(Constants.STATUS) String status, @Query(Constants.PAGE) int page);

    @GET(Constants.EPISODE + Constants.ID_SELECTOR)
    Call<Episode> singleEpisode(@Path(Constants.ID) int id);
}
