package com.example.rickandmorty.view;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.example.rickandmorty.R;
import com.example.rickandmorty.api.RetrofitClient;
import com.example.rickandmorty.api.RickAndMortyAPI;
import com.example.rickandmorty.data.PrefManager;
import com.example.rickandmorty.model.Character;
import com.example.rickandmorty.model.Episode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CharacterDetailActivity extends AppCompatActivity {
    private Character character;
    private Episode episode;
    private ImageView favIcon;
    private ImageView charImage;
    private TextView name;
    private TextView status;
    private TextView species;
    private TextView episodesNumber;
    private TextView gender;
    private TextView origin;
    private TextView location;
    private TextView lastEpisodeName;
    private TextView lastEpisodeDate;
    private LinearLayout episodeNameLayout;
    private LinearLayout episodeDateLayout;

    private int resultCode = Activity.RESULT_CANCELED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detail);

        character = (Character) getIntent().getSerializableExtra(MainActivity.CHARACTER_KEY);

        installViews();
        visualizeScreen();
        fetchEpisode();
    }

    private void installViews() {
        favIcon = findViewById(R.id.favorite_icon);
        charImage = findViewById(R.id.char_image);
        name = findViewById(R.id.char_name);
        status = findViewById(R.id.char_status);
        species = findViewById(R.id.char_species);
        episodesNumber = findViewById(R.id.episodes_number);
        gender = findViewById(R.id.gender);
        origin = findViewById(R.id.origin);
        location = findViewById(R.id.location);
        lastEpisodeName = findViewById(R.id.episode_name);
        lastEpisodeDate = findViewById(R.id.air_date);
        episodeNameLayout = findViewById(R.id.episode_name_layout);
        episodeDateLayout = findViewById(R.id.episode_date_layout);
    }

    private void fetchEpisode() {
        RickAndMortyAPI rickAndMortyAPI = RetrofitClient.getInstance().create(RickAndMortyAPI.class);
        rickAndMortyAPI.singleEpisode(getLastEpisodeId()).enqueue(new Callback<Episode>() {
            @Override
            public void onResponse(@NonNull Call<Episode> call, @NonNull Response<Episode> response) {
                if (response.isSuccessful()) {
                    episode = response.body();
                    episodeNameLayout.setVisibility(View.VISIBLE);
                    episodeDateLayout.setVisibility(View.VISIBLE);
                    lastEpisodeName.setText(episode.getName());
                    lastEpisodeDate.setText(episode.getAirDate());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Episode> call, @NonNull Throwable t) {

            }
        });
    }

    private void visualizeScreen() {
        showFavIcon(character.isFavorite());
        name.setText(character.getName());
        status.setText(character.getStatus());
        species.setText(character.getSpecies());
        episodesNumber.setText(String.valueOf(character.getEpisode().size()));
        gender.setText(character.getGender());
        origin.setText(character.getOrigin().getName());
        location.setText(character.getLocation().getName());
        Glide.with(this).load(character.getImage()).into(charImage);
    }

    private int getLastEpisodeId() {
        String lastEpisodeLink = character.getEpisode().get(character.getEpisode().size() - 1);
        return Integer.parseInt(lastEpisodeLink.substring(lastEpisodeLink.lastIndexOf("/") + 1));
    }

    public void onClickFav(View view) {
        resultCode = Activity.RESULT_OK;
        boolean isFavorite = PrefManager.getInstance().changeCharacterFavorite(character.getId());
        character.setFavorite(isFavorite);
        showFavIcon(isFavorite);
    }

    private void showFavIcon(boolean isFavorite) {
        favIcon.setImageDrawable(isFavorite ? getDrawable(R.drawable.fav_inline) : getDrawable(R.drawable.fav_outline));
    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        prepareResult();
        super.onBackPressed();
    }

    private void prepareResult() {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.CHARACTER_KEY, character);
        setResult(resultCode, intent);
    }
}