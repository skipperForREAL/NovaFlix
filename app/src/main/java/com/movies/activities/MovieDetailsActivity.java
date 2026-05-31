package com.movies.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.movies.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.movies.adapters.CastAdapter;
import com.movies.adapters.ReviewAdapter;
import com.movies.api.RetrofitClient;
import com.movies.models.CreditsResponse;
import com.movies.models.ReviewResponse;
import com.movies.models.VideoResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private RecyclerView rvCast, rvReviews;
    private CastAdapter castAdapter;
    private ReviewAdapter reviewAdapter;
    private final List<CreditsResponse.Cast> castList = new ArrayList<>();
    private final List<ReviewResponse.Review> reviewList = new ArrayList<>();
    private TextView txtTitle, txtOverview, txtRating, txtYear, txtDuration, txtGenre;
    private TextView tabAbout, tabCast, tabReviews;
    private int movieId;
    private boolean isTv;
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
        rvReviews = findViewById(R.id.rvReviews);
        tabAbout = findViewById(R.id.tabAbout);
        tabCast = findViewById(R.id.tabCast);
        tabReviews = findViewById(R.id.tabReviews);
        com.google.android.material.button.MaterialButton btnPlayTrailer = findViewById(R.id.btnPlayTrailer);

        btnBack.setOnClickListener(v -> finish());
        btnPlayTrailer.setOnClickListener(v -> loadAndPlayTrailer());

        // Get Data
        Intent intent = getIntent();
        movieId = intent.getIntExtra("movie_id", -1);
        isTv = intent.getBooleanExtra("is_tv", false);
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
        setupRecyclerViews();
        loadCast();
        loadReviews();

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
            rvReviews.setVisibility(View.GONE);
            tabAbout.setTextColor(getResources().getColor(R.color.red));
            tabCast.setTextColor(getResources().getColor(R.color.gray));
            tabReviews.setTextColor(getResources().getColor(R.color.gray));
        });

        tabCast.setOnClickListener(v -> {
            txtOverview.setVisibility(View.GONE);
            rvCast.setVisibility(View.VISIBLE);
            rvReviews.setVisibility(View.GONE);
            tabCast.setTextColor(getResources().getColor(R.color.red));
            tabAbout.setTextColor(getResources().getColor(R.color.gray));
            tabReviews.setTextColor(getResources().getColor(R.color.gray));
        });

        tabReviews.setOnClickListener(v -> {
            txtOverview.setVisibility(View.GONE);
            rvCast.setVisibility(View.GONE);
            rvReviews.setVisibility(View.VISIBLE);
            tabReviews.setTextColor(getResources().getColor(R.color.red));
            tabAbout.setTextColor(getResources().getColor(R.color.gray));
            tabCast.setTextColor(getResources().getColor(R.color.gray));
        });
    }

    private void setupRecyclerViews() {
        rvCast.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        castAdapter = new CastAdapter(this, castList);
        rvCast.setAdapter(castAdapter);

        rvReviews.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(this, reviewList);
        rvReviews.setAdapter(reviewAdapter);
    }

    private void loadCast() {
        if (movieId == -1) return;

        Call<CreditsResponse> call;
        if (isTv) {
            call = RetrofitClient.getInstance().getTmdbApi().getTvCredits(movieId, API_KEY);
        } else {
            call = RetrofitClient.getInstance().getTmdbApi().getMovieCredits(movieId, API_KEY);
        }

        call.enqueue(new Callback<CreditsResponse>() {
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

    private void loadReviews() {
        if (movieId == -1) return;

        Call<ReviewResponse> call;
        if (isTv) {
            call = RetrofitClient.getInstance().getTmdbApi().getTvReviews(movieId, API_KEY);
        } else {
            call = RetrofitClient.getInstance().getTmdbApi().getMovieReviews(movieId, API_KEY);
        }

        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ReviewResponse.Review> reviews = response.body().getResults();
                    if (reviews != null) {
                        reviewList.clear();
                        reviewList.addAll(reviews);
                        reviewAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                // Silent failure
            }
        });
    }

    private void loadAndPlayTrailer() {
        if (movieId == -1) return;

        Log.d("MovieDetails", "Loading trailer for " + (isTv ? "TV" : "Movie") + " ID: " + movieId);

        Call<VideoResponse> call;
        if (isTv) {
            call = RetrofitClient.getInstance().getTmdbApi().getTvVideos(movieId, API_KEY);
        } else {
            call = RetrofitClient.getInstance().getTmdbApi().getMovieVideos(movieId, API_KEY);
        }

        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<VideoResponse.Video> videos = response.body().getResults();
                    String youtubeKey = null;
                    
                    if (videos == null || videos.isEmpty()) {
                        Toast.makeText(MovieDetailsActivity.this, "No videos found for this content", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Priority 1: Official Trailer
                    for (VideoResponse.Video v : videos) {
                        if ("YouTube".equalsIgnoreCase(v.getSite()) && "Trailer".equalsIgnoreCase(v.getType())) {
                            youtubeKey = v.getKey();
                            break;
                        }
                    }

                    // Priority 2: Teaser
                    if (youtubeKey == null) {
                        for (VideoResponse.Video v : videos) {
                            if ("YouTube".equalsIgnoreCase(v.getSite()) && "Teaser".equalsIgnoreCase(v.getType())) {
                                youtubeKey = v.getKey();
                                break;
                            }
                        }
                    }

                    // Priority 3: Any YouTube video
                    if (youtubeKey == null) {
                        for (VideoResponse.Video v : videos) {
                            if ("YouTube".equalsIgnoreCase(v.getSite())) {
                                youtubeKey = v.getKey();
                                break;
                            }
                        }
                    }

                    if (youtubeKey != null) {
                        Log.d("MovieDetails", "Playing YouTube video: " + youtubeKey);
                        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.youtube.com/watch?v=" + youtubeKey));
                        startActivity(intent);
                    } else {
                        Toast.makeText(MovieDetailsActivity.this, "No YouTube trailer available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("MovieDetails", "API Error: " + response.code());
                    Toast.makeText(MovieDetailsActivity.this, "Trailer not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Log.e("MovieDetails", "Network Error: " + t.getMessage());
                Toast.makeText(MovieDetailsActivity.this, "Error loading trailer", Toast.LENGTH_SHORT).show();
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
}