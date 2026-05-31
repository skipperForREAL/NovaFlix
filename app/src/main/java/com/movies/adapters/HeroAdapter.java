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
import com.movies.activities.MovieDetailsActivity;
import com.movies.models.Movie;

import java.util.List;

public class HeroAdapter extends RecyclerView.Adapter<HeroAdapter.HeroViewHolder> {

    private final Context context;
    private final List<Movie> movieList;
    private final boolean isTv;

    public HeroAdapter(Context context, List<Movie> movieList) {
        this(context, movieList, false);
    }

    public HeroAdapter(Context context, List<Movie> movieList, boolean isTv) {
        this.context = context;
        this.movieList = movieList;
        this.isTv = isTv;
    }

    @NonNull
    @Override
    public HeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hero_movie, parent, false);
        return new HeroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeroViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        holder.txtHeroTitle.setText(movie.getTitle());

        // Use Backdrop for the big cards
        String imageUrl = "https://image.tmdb.org/t/p/w780" + movie.getBackdropPath();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.movie_poster)
                .into(holder.imgHero);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailsActivity.class);
            intent.putExtra("movie_id", movie.getId());
            intent.putExtra("movie_title", movie.getTitle());
            intent.putExtra("movie_overview", movie.getOverview());
            intent.putExtra("movie_poster", movie.getPosterPath());
            intent.putExtra("movie_backdrop", movie.getBackdropPath());
            intent.putExtra("movie_rating", movie.getVoteAverage());
            intent.putExtra("movie_date", movie.getReleaseDate());
            intent.putExtra("is_tv", isTv);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return Math.min(movieList.size(), 5); // Limit hero to 5 items
    }

    public static class HeroViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHero;
        TextView txtHeroTitle;

        public HeroViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHero = itemView.findViewById(R.id.imgHero);
            txtHeroTitle = itemView.findViewById(R.id.txtHeroTitle);
        }
    }
}