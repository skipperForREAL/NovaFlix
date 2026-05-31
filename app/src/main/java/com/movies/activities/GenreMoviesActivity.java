package com.movies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.movies.R;
import com.movies.adapters.MovieAdapter;
import com.movies.api.RetrofitClient;
import com.movies.models.Movie;
import com.movies.models.MovieResponse;
import com.movies.utils.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreMoviesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Movie> movieList;
    private ProgressBar progressBar;
    private static final String API_KEY = "d0f5f5e68c231452308dfec598891cc4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_movies);

        String genreId = getIntent().getStringExtra("genre_id");
        String genreName = getIntent().getStringExtra("genre_name");
        String languageCode = getIntent().getStringExtra("language_code");
        String searchQuery = getIntent().getStringExtra("search_query");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(genreName);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.rvGenreMovies);
        progressBar = findViewById(R.id.progressBar);

        movieList = new ArrayList<>();
        adapter = new MovieAdapter(this, movieList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        int spacingInPixels = getResources().getDimensionPixelSize(androidx.cardview.R.dimen.cardview_default_elevation) * 4;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
        recyclerView.setAdapter(adapter);

        setupBottomNavigation();

        if (searchQuery != null) {
            searchMovies(searchQuery);
        } else if (languageCode != null) {
            loadMoviesByLanguage(languageCode);
        } else {
            loadMoviesByGenre(genreId);
        }
    }

    private void searchMovies(String query) {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance().getTmdbApi()
                .searchMovies(API_KEY, query, "en-US", 1)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            if (movies != null) {
                                movieList.clear();
                                movieList.addAll(movies);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(GenreMoviesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMoviesByGenre(String genreId) {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance().getTmdbApi()
                .getMoviesByGenre(API_KEY, genreId, "en-US", 1)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            if (movies != null) {
                                movieList.clear();
                                movieList.addAll(movies);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(GenreMoviesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_tv_shows) {
                startActivity(new Intent(this, TvShowsActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_search) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

    private void loadMoviesByLanguage(String languageCode) {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance().getTmdbApi()
                .getMoviesByLanguage(API_KEY, languageCode, "en-US", 1)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            if (movies != null) {
                                movieList.clear();
                                movieList.addAll(movies);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(GenreMoviesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}