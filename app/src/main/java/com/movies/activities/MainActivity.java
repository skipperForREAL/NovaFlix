package com.movies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.movies.R;
import com.movies.adapters.GenreAdapter;
import com.movies.adapters.HeroAdapter;
import com.movies.adapters.MovieAdapter;
import com.movies.api.RetrofitClient;
import com.movies.models.Genre;
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
    private List<Movie> topRatedList;
    private List<Movie> heroTvList;
    private List<Genre> genreList;
    private MovieAdapter adapter;
    private MovieAdapter upcomingAdapter;
    private MovieAdapter topRatedAdapter;
    private HeroAdapter heroAdapter;
    private GenreAdapter genreAdapter;
    private ProgressBar progressBar;

    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private int currentHeroPosition = 0;
    private RecyclerView rvHero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.rvPopular);
        rvHero = findViewById(R.id.rvHero);
        RecyclerView rvLatest = findViewById(R.id.rvLatest);
        progressBar = findViewById(R.id.progressBar);

        rvHero.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        heroTvList = new ArrayList<>();
        heroAdapter = new HeroAdapter(this, heroTvList, true);
        rvHero.setAdapter(heroAdapter);

        // Add SnapHelper for smooth hero scrolling
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvHero);

        // Setup Latest RecyclerView
        rvLatest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        movieList = new ArrayList<>();
        adapter = new MovieAdapter(this, movieList);
        rvLatest.setAdapter(adapter);

        // Setup Upcoming RecyclerView (mapped to rvPopular ID for now to avoid layout changes)
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        upcomingList = new ArrayList<>();
        upcomingAdapter = new MovieAdapter(this, upcomingList);
        recyclerView.setAdapter(upcomingAdapter);

        // Setup Top Rated RecyclerView
        RecyclerView rvTopRated = findViewById(R.id.rvTopRated);
        rvTopRated.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        topRatedList = new ArrayList<>();
        topRatedAdapter = new MovieAdapter(this, topRatedList);
        rvTopRated.setAdapter(topRatedAdapter);

        // Setup Genres RecyclerView
        RecyclerView rvGenres = findViewById(R.id.rvGenres);
        rvGenres.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        genreList = new ArrayList<>();
        genreAdapter = new GenreAdapter(this, genreList);
        rvGenres.setAdapter(genreAdapter);

        // Setup Search
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = etSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, GenreMoviesActivity.class);
                    intent.putExtra("search_query", query);
                    intent.putExtra("genre_name", "Results for: " + query);
                    startActivity(intent);
                }
                return true;
            }
            return false;
        });

        setupBottomNavigation();
        loadMovies();
    }

    private void loadMovies() {
        loadPopularMovies();
        loadUpcomingMovies();
        loadTopRatedMovies();
        loadHeroTvShows();
        loadGenres();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_tv_shows) {
                startActivity(new Intent(this, TvShowsActivity.class));
                return true;
            } else if (id == R.id.nav_search) {
                EditText etSearch = findViewById(R.id.etSearch);
                if (etSearch != null) {
                    etSearch.requestFocus();
                }
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
            Toast.makeText(this, "NovaFlix v1.0", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.help) {
            Toast.makeText(this, "Contact support@novaflix.com", Toast.LENGTH_SHORT).show();
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

    private void loadTopRatedMovies() {
        RetrofitClient.getInstance().getTmdbApi()
                .getTopRatedMovies(API_KEY, "en-US", 1)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            if (movies != null && !movies.isEmpty()) {
                                topRatedList.clear();
                                topRatedList.addAll(movies);
                                topRatedAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Log.e(TAG, "Top Rated Failure: " + t.getMessage());
                    }
                });
    }

    private void startHeroAutoScroll() {
        if (autoScrollHandler != null) return; // Already running

        autoScrollHandler = new Handler(Looper.getMainLooper());
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (heroAdapter.getItemCount() > 0) {
                    currentHeroPosition = (currentHeroPosition + 1) % heroAdapter.getItemCount();
                    rvHero.smoothScrollToPosition(currentHeroPosition);
                }
                autoScrollHandler.postDelayed(this, 3000);
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, 3000);
    }

    private void loadHeroTvShows() {
        RetrofitClient.getInstance().getTmdbApi()
                .getTopRatedTvShows(API_KEY, "en-US", 1)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> tvShows = response.body().getResults();
                            if (tvShows != null && !tvShows.isEmpty()) {
                                heroTvList.clear();
                                heroTvList.addAll(tvShows);
                                heroAdapter.notifyDataSetChanged();
                                startHeroAutoScroll();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Log.e(TAG, "Hero TV Failure: " + t.getMessage());
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
            autoScrollHandler = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (heroTvList != null && !heroTvList.isEmpty()) {
            startHeroAutoScroll();
        }
    }

    private void loadGenres() {
        genreList.clear();
        // Genres from TMDB: Horror (27), Comedy (35), Action (28), Drama (18), Thriller (53), Romance (10749)
        // Language: Hindi (hi)
        genreList.add(new Genre("28", "Action", null));
        genreList.add(new Genre("35", "Comedy", null));
        genreList.add(new Genre("27", "Horror", null));
        genreList.add(new Genre("18", "Drama", null));
        genreList.add(new Genre("53", "Thriller", null));
        genreList.add(new Genre("10749", "Romance", null));
        genreList.add(new Genre(null, "Hindi", "hi"));

        genreAdapter.notifyDataSetChanged();

        // Load a representative poster for each genre
        for (Genre g : genreList) {
            fetchPosterForGenre(g);
        }
    }

    private void fetchPosterForGenre(Genre g) {
        Call<MovieResponse> call;
        if (g.getLanguageCode() != null) {
            call = RetrofitClient.getInstance().getTmdbApi().getMoviesByLanguage(API_KEY, g.getLanguageCode(), "en-US", 1);
        } else {
            call = RetrofitClient.getInstance().getTmdbApi().getMoviesByGenre(API_KEY, g.getId(), "en-US", 1);
        }

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getResults().isEmpty()) {
                    g.setPosterPath(response.body().getResults().get(0).getPosterPath());
                    genreAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) { }
        });
    }
}