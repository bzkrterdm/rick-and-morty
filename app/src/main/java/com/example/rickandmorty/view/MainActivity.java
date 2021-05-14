package com.example.rickandmorty.view;

import android.app.Dialog;
import android.content.Intent;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rickandmorty.R;
import com.example.rickandmorty.adapter.CharacterItemAdapter;
import com.example.rickandmorty.data.PrefManager;
import com.example.rickandmorty.model.Character;
import com.example.rickandmorty.model.FilterType;
import com.example.rickandmorty.model.LayoutType;
import com.example.rickandmorty.model.Status;
import com.example.rickandmorty.viewmodel.CharacterItemViewModel;
import com.example.rickandmorty.model.FilterChanger;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FilterChanger {
    public static final String GLOBAL_PREF = "GLOBAL";
    public static final String CHARACTER_KEY = "character";
    public static final int DETAIL_REQUEST = 1;

    private CharacterItemAdapter adapter;
    private ImageView changeLayout;
    private ImageView closeSearch;
    private EditText searchText;
    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private CharacterItemViewModel characterItemViewModel;
    private Pair<FilterType, String> filterPair = new Pair<>(FilterType.None, "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.character_list);
        changeLayout = findViewById(R.id.change_layout);
        closeSearch = findViewById(R.id.close_search);
        searchText = findViewById(R.id.search_text);

        searchText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                changeFilter(new Pair<>(FilterType.Name, searchText.getText().toString()));
                return true;
            }
            return false;
        });

        adapter = new CharacterItemAdapter(this);
        PrefManager.getInstance().Initialize(getApplicationContext().getSharedPreferences(GLOBAL_PREF, MODE_PRIVATE));

        characterItemViewModel = new ViewModelProvider(this).get(CharacterItemViewModel.class);
        observeLiveData();

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void observeLiveData() {
        characterItemViewModel.getCharacterList().observe(this, characters -> adapter.submitList(characters));
        characterItemViewModel.getNetworkStateLiveData().observe(this, networkState -> adapter.setNetworkState(networkState));
    }

    public void onClickChangeLayout(View view) {
        LayoutType layoutType = adapter.layoutType;
        int firstVisibleItemPosition = findFirstVisibleItem(layoutType);
        adapter.changeListType();
        reloadRecyclerView();

        if (layoutType == LayoutType.List) {
            recyclerView.setLayoutManager(gridLayoutManager);
            changeLayout.setImageDrawable(getDrawable(R.drawable.list));
        } else {
            recyclerView.setLayoutManager(linearLayoutManager);
            changeLayout.setImageDrawable(getDrawable(R.drawable.grid));
        }
        recyclerView.scrollToPosition(firstVisibleItemPosition);
        adapter.notifyDataSetChanged();
    }

    private void reloadRecyclerView() {
        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(null);
        recyclerView.setAdapter(adapter);
    }

    private int findFirstVisibleItem(LayoutType layoutType) {
        if (layoutType == LayoutType.List) {
            return linearLayoutManager.findFirstVisibleItemPosition();
        } else {
            return gridLayoutManager.findFirstVisibleItemPosition();
        }
    }

    public void onClickFilter(View view) {
        showFilterDialog();
    }

    private void showFilterDialog() {
        onClickCloseSearch(null);
        final Dialog dialog = new Dialog(this);
        final String[] filterString = {null};
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.filter_dialog);

        Button dialogButton = dialog.findViewById(R.id.filter_ok_button);
        dialogButton.setOnClickListener(v -> {
            if (filterString[0] != null && !filterString[0].isEmpty()) {
                changeFilter(new Pair<>(FilterType.Status, filterString[0]));
            } else {
                changeFilter(new Pair<>(FilterType.None, ""));
            }
            dialog.dismiss();
        });

        RadioGroup radioGroup = dialog.findViewById(R.id.filter_radio_group);
        ((RadioButton) dialog.findViewById(R.id.all_radio)).setChecked(true);

        if (filterPair.first == FilterType.Status) {
            if (filterPair.second.equals(Status.Alive.toString())) {
                ((RadioButton) dialog.findViewById(R.id.alive_radio)).setChecked(true);
            } else if (filterPair.second.equals(Status.Dead.toString())) {
                ((RadioButton) dialog.findViewById(R.id.dead_radio)).setChecked(true);
            } else if (filterPair.second.equals(Status.Unknown.toString())) {
                ((RadioButton) dialog.findViewById(R.id.unknown_radio)).setChecked(true);
            }
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.alive_radio) {
                filterString[0] = Status.Alive.toString();
            } else if (checkedId == R.id.dead_radio) {
                filterString[0] = Status.Dead.toString();
            } else if (checkedId == R.id.unknown_radio) {
                filterString[0] = Status.Unknown.toString();
            }
        });
        dialog.show();
    }

    public void onClickSearch(View view) {
        searchText.setVisibility(View.VISIBLE);
        closeSearch.setVisibility(View.VISIBLE);
    }

    public void onClickCloseSearch(View view) {
        searchText.setVisibility(View.GONE);
        closeSearch.setVisibility(View.GONE);
        searchText.setText("");
        changeFilter(new Pair<>(FilterType.None, ""));
    }

    @Override
    public void changeFilter(Pair<FilterType, String> filterPair) {
        this.filterPair = filterPair;
        characterItemViewModel.changeFilter(filterPair);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == DETAIL_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Character character = (Character) data.getSerializableExtra(CHARACTER_KEY);
                for (Character characterItem : Objects.requireNonNull(characterItemViewModel.getCharacterList().getValue())) {
                    if (characterItem.getId().equals(character.getId())) {
                        characterItem.setFavorite(character.isFavorite());
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }
}