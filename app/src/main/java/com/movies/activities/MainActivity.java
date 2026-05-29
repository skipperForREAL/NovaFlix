package com.movies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.movies.R;
import com.movies.adapters.HeroAdapter;
import com.movies.adapters.MovieAdapter;
import com.movies.api.RetrofitClient;
import com.movies.models.Movie;
import com.movies.models.MovieResponse;
import com.movies.utils.FirebaseAuthManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String API_KEY = "d0f5f5e68c231452308dfec598891cc4";

    private List<Movie> movieList;
    private List<Movie> upcomingList;
    private MovieAdapter adapter;
    private MovieAdapter upcomingAdapter;
    private HeroAdapter heroAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.rvPopular);
        RecyclerView rvHero = findViewById(R.id.rvHero);
        RecyclerView rvLatest = findViewById(R.id.rvLatest);
        progressBar = findViewById(R.id.progressBar);

        rvHero.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        movieList = new ArrayList<>();
        heroAdapter = new HeroAdapter(this, movieList);
        rvHero.setAdapter(heroAdapter);

        // Setup Latest RecyclerView
        rvLatest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new MovieAdapter(this, movieList);
        rvLatest.setAdapter(adapter);

        // Setup Upcoming RecyclerView (mapped to rvPopular ID for now to avoid layout changes)
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        upcomingList = new ArrayList<>();
        upcomingAdapter = new MovieAdapter(this, upcomingList);
        recyclerView.setAdapter(upcomingAdapter);

        // Setup Search
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = etSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchMovies(query);
                }
                return true;
            }
            return false;
        });

        setupBottomNavigation();
        loadPopularMovies();
        loadUpcomingMovies();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_tv_shows) {
                startActivity(new Intent(this, TvShowsActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dropdown, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            FirebaseAuthManager.signOut(this);
            Intent intent = new Intent(MainActivity.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.about) {
            Toast.makeText(this, "Movies Zone v1.0", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.help) {
            Toast.makeText(this, "Contact support@movieszone.com", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPopularMovies() {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance().getTmdbApi()
                .getPopularMovies(API_KEY, "en-US", 1)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            if (movies != null && !movies.isEmpty()) {
                                movieList.clear();
                                movieList.addAll(movies);
                                adapter.notifyDataSetChanged();
                                heroAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MainActivity.this, "No movies found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "Response Error: " + response.code());
                            Toast.makeText(MainActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "Failure: " + t.getMessage());
                        Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUpcomingMovies() {
        RetrofitClient.getInstance().getTmdbApi()
                .getUpcomingMovies(API_KEY, "en-US", 1)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            if (movies != null && !movies.isEmpty()) {
                                upcomingList.clear();
                                upcomingList.addAll(movies);
                                upcomingAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Log.e(TAG, "Upcoming Failure: " + t.getMessage());
                    }
                });
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
                            if (movies != null && !movies.isEmpty()) {
                                movieList.clear();
                                movieList.addAll(movies);
                                adapter.notifyDataSetChanged();
                                heroAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MainActivity.this, "No results for: " + query, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Search Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}