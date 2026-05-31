package com.movies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.appbar.MaterialToolbar;
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

public class TvShowsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Movie> tvShowList;
    private ProgressBar progressBar;
    private static final String API_KEY = "d0f5f5e68c231452308dfec598891cc4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_shows);

        recyclerView = findViewById(R.id.rvTvShows);
        progressBar = findViewById(R.id.progressBar);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_tv_shows);

        tvShowList = new ArrayList<>();
        // Reusing MovieAdapter as TV shows have similar fields (name/title, poster, overview)
        adapter = new MovieAdapter(this, tvShowList, true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        
        // Add Spacing between items
        int spacingInPixels = getResources().getDimensionPixelSize(androidx.cardview.R.dimen.cardview_default_elevation) * 4; // Approx 16dp
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));

        recyclerView.setAdapter(adapter);

        setupBottomNavigation(bottomNavigationView);
        
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        loadTrendingTvShows();
    }

    private void setupBottomNavigation(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_tv_shows) {
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

    private void loadTrendingTvShows() {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance().getTmdbApi()
                .getTrendingTvShows(API_KEY, "en-US", 1)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> tvShows = response.body().getResults();
                            if (tvShows != null) {
                                tvShowList.clear();
                                tvShowList.addAll(tvShows);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Log.e("TvShowsActivity", "Error: " + t.getMessage());
                        Toast.makeText(TvShowsActivity.this, "Failed to load TV shows", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}