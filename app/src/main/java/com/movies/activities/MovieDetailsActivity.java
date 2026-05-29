package com.movies.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.movies.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.movies.adapters.CastAdapter;
import com.movies.api.RetrofitClient;
import com.movies.models.CreditsResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private RecyclerView rvCast;
    private CastAdapter castAdapter;
    private final List<CreditsResponse.Cast> castList = new ArrayList<>();
    private TextView txtTitle, txtOverview, txtRating, txtYear, txtDuration, txtGenre;
    private TextView tabAbout, tabCast;
    private int movieId;
    private static final String API_KEY = "d0f5f5e68c231452308dfec598891cc4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Views
        ImageView imgBackdrop = findViewById(R.id.imgBackdrop);
        ImageView imgPoster = findViewById(R.id.imgDetailPoster);
        txtTitle = findViewById(R.id.txtDetailTitle);
        txtOverview = findViewById(R.id.txtDetailOverview);
        txtRating = findViewById(R.id.txtRating);
        txtYear = findViewById(R.id.txtYear);
        txtDuration = findViewById(R.id.txtDuration);
        txtGenre = findViewById(R.id.txtGenre);
        ImageView btnBack = findViewById(R.id.btnBack);
        rvCast = findViewById(R.id.rvCast);
        tabAbout = findViewById(R.id.tabAbout);
        tabCast = findViewById(R.id.tabCast);

        btnBack.setOnClickListener(v -> finish());

        // Get Data
        Intent intent = getIntent();
        movieId = intent.getIntExtra("movie_id", -1);
        String title = intent.getStringExtra("movie_title");
        String overview = intent.getStringExtra("movie_overview");
        String posterPath = intent.getStringExtra("movie_poster");
        String backdropPath = intent.getStringExtra("movie_backdrop"); // Make sure you pass this!
        double rating = intent.getDoubleExtra("movie_rating", 0.0);
        String date = intent.getStringExtra("movie_date");

        // Set Data
        txtTitle.setText(title);
        txtOverview.setText(overview);
        txtRating.setText(String.format("%.1f", rating));

        setupTabs();
        setupCastRecyclerView();
        loadCast();

        // Extract Year from Date (e.g., "2022-05-20" -> "2022")
        if (date != null && date.length() > 4) {
            txtYear.setText(date.substring(0, 4));
        }

        // Load Images
        if (posterPath != null) {
            Glide.with(this).load("https://image.tmdb.org/t/p/w500" + posterPath).into(imgPoster);
        }

        if (backdropPath != null) {
            Glide.with(this).load("https://image.tmdb.org/t/p/original" + backdropPath).into(imgBackdrop);
        } else {
            // Fallback if no backdrop
            imgBackdrop.setImageResource(R.drawable.movie_poster);
        }

        setupBottomNavigation();
    }

    private void setupTabs() {
        tabAbout.setOnClickListener(v -> {
            txtOverview.setVisibility(View.VISIBLE);
            rvCast.setVisibility(View.GONE);
            tabAbout.setTextColor(getResources().getColor(R.color.red));
            tabCast.setTextColor(getResources().getColor(R.color.gray));
        });

        tabCast.setOnClickListener(v -> {
            txtOverview.setVisibility(View.GONE);
            rvCast.setVisibility(View.VISIBLE);
            tabCast.setTextColor(getResources().getColor(R.color.red));
            tabAbout.setTextColor(getResources().getColor(R.color.gray));
        });
    }

    private void setupCastRecyclerView() {
        rvCast.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        castAdapter = new CastAdapter(this, castList);
        rvCast.setAdapter(castAdapter);
    }

    private void loadCast() {
        if (movieId == -1) return;

        RetrofitClient.getInstance().getTmdbApi()
                .getMovieCredits(movieId, API_KEY)
                .enqueue(new Callback<CreditsResponse>() {
                    @Override
                    public void onResponse(Call<CreditsResponse> call, Response<CreditsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<CreditsResponse.Cast> cast = response.body().getCast();
                            if (cast != null) {
                                castList.clear();
                                castList.addAll(cast);
                                castAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CreditsResponse> call, Throwable t) {
                        // Silent failure
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
            }
            return false;
        });
    }
}