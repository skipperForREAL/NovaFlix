package com.movies.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.movies.R;
import com.movies.activities.GenreMoviesActivity;
import com.movies.models.Genre;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private final Context context;
    private final List<Genre> genreList;

    public GenreAdapter(Context context, List<Genre> genreList) {
        this.context = context;
        this.genreList = genreList;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        Genre genre = genreList.get(position);
        holder.txtName.setText(genre.getName());

        if (genre.getPosterPath() != null) {
            String imageUrl = "https://image.tmdb.org/t/p/w500" + genre.getPosterPath();
            Glide.with(context).load(imageUrl).into(holder.imgPoster);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GenreMoviesActivity.class);
            intent.putExtra("genre_id", genre.getId());
            intent.putExtra("genre_name", genre.getName());
            intent.putExtra("language_code", genre.getLanguageCode());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView txtName;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgGenrePoster);
            txtName = itemView.findViewById(R.id.txtGenreName);
        }
    }
}