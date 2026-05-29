package com.movies.api;

import com.movies.models.MovieResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TmdbApi {

    // Base URL is https://api.themoviedb.org/3/

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("search/movie")
    Call<MovieResponse> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("language") String language,
            @Query("page") int page
    );
    @GET("movie/{movie_id}/credits")
    Call<com.movies.models.CreditsResponse> getMovieCredits(
            @retrofit2.http.Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("trending/tv/week")
    Call<MovieResponse> getTrendingTvShows(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );
}