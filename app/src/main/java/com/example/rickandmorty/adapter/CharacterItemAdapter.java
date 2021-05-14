package com.example.rickandmorty.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.rickandmorty.R;
import com.example.rickandmorty.model.Character;
import com.example.rickandmorty.model.LayoutType;
import com.example.rickandmorty.model.NetworkState;
import com.example.rickandmorty.view.CharacterDetailActivity;
import com.example.rickandmorty.view.MainActivity;

import java.util.Objects;

public class CharacterItemAdapter extends PagedListAdapter<Character, RecyclerView.ViewHolder> {
    private final Context context;
    private NetworkState networkState;
    public LayoutType layoutType = LayoutType.List;
    public static final int CHARACTER_ITEM_VIEW_TYPE = 1;
    public static final int LOAD_ITEM_VIEW_TYPE = 0;

    public CharacterItemAdapter(Context context) {
        super(Character.DIFF_CALL);
        this.context = context;
    }


    @Override
    public int getItemViewType(int position) {
        //If fetching data then we show progress bar
        return (isLoadingData() && position == getItemCount() - 1) ? LOAD_ITEM_VIEW_TYPE : CHARACTER_ITEM_VIEW_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == CHARACTER_ITEM_VIEW_TYPE) {
            //We can show items as grid or list
            int resource = layoutType == LayoutType.List ? R.layout.character_item : R.layout.character_grid;
            view = inflater.inflate(resource, parent, false);
            return new CharacterViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.load_progress_item, parent, false);
            return new ProgressViewHolder(view);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CharacterViewHolder) {
            CharacterViewHolder characterViewHolder = (CharacterViewHolder) holder;
            Character character = getItem(position);
            if (character != null) {
                characterViewHolder.bind(character, context);
                characterViewHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, CharacterDetailActivity.class);
                    intent.putExtra(MainActivity.CHARACTER_KEY, Objects.requireNonNull(getCurrentList()).get(position));
                    ((MainActivity) context).startActivityForResult(intent, MainActivity.DETAIL_REQUEST);
                });
            }
        }
    }

    public void setNetworkState(NetworkState networkState) {
        boolean wasLoading = isLoadingData();
        this.networkState = networkState;
        boolean willLoad = isLoadingData();
        if (wasLoading != willLoad) {
            if (wasLoading) notifyItemRemoved(getItemCount());
            else notifyItemInserted(getItemCount());
        }
    }

    public void changeListType() {
        if (layoutType == LayoutType.List) {
            layoutType = LayoutType.Grid;
        } else {
            layoutType = LayoutType.List;
        }
    }

    public boolean isLoadingData() {
        return (networkState != null && networkState != NetworkState.LOADED);
    }

    private static class CharacterViewHolder extends RecyclerView.ViewHolder {
        TextView charName;
        TextView charStatus;
        TextView charSpecies;
        ImageView charImage;
        ImageView favIcon;

        public CharacterViewHolder(View itemView) {
            super(itemView);
            charName = itemView.findViewById(R.id.char_name);
            charImage = itemView.findViewById(R.id.char_image);
            charStatus = itemView.findViewById(R.id.char_status);
            charSpecies = itemView.findViewById(R.id.char_species);
            favIcon = itemView.findViewById(R.id.favorite_icon);
        }

        public void bind(Character character, Context context) {
            charName.setText(character.getName());
            charStatus.setText(character.getStatus());
            charSpecies.setText(character.getSpecies());
            favIcon.setImageDrawable(character.isFavorite() ? context.getDrawable(R.drawable.fav_inline) : context.getDrawable(R.drawable.fav_outline));
            Glide.with(context).load(character.getImage()).into(charImage);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }
}