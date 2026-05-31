package com.movies.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.movies.R;
import com.movies.activities.MovieDetailsActivity; // Make sure this path is correct
import com.movies.models.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movieList;
    private boolean isTv = false;

    public MovieAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    public MovieAdapter(Context context, List<Movie> movieList, boolean isTv) {
        this.context = context;
        this.movieList = movieList;
        this.isTv = isTv;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // We will use item_category_movie.xml for the clean poster look
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        // Load Image using Glide
        String imageUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgPoster);

        // Handle Click to open Details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailsActivity.class);

            // Pass all necessary data to the details screen
            intent.putExtra("movie_id", movie.getId());
            intent.putExtra("movie_title", movie.getTitle());
            intent.putExtra("movie_overview", movie.getOverview());
            intent.putExtra("movie_poster", movie.getPosterPath());
            intent.putExtra("movie_backdrop", movie.getBackdropPath()); // Important for the big background
            intent.putExtra("movie_rating", movie.getVoteAverage());
            intent.putExtra("movie_date", movie.getReleaseDate());
            intent.putExtra("is_tv", isTv);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            // Only finding the image view now
            imgPoster = itemView.findViewById(R.id.imgPoster);
        }
    }
}