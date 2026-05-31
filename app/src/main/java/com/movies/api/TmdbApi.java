package com.movies.api;

import com.movies.models.MovieResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Path;

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

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(
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
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("trending/tv/week")
    Call<MovieResponse> getTrendingTvShows(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("movie/{movie_id}/videos")
    Call<com.movies.models.VideoResponse> getMovieVideos(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("tv/{tv_id}/videos")
    Call<com.movies.models.VideoResponse> getTvVideos(
            @Path("tv_id") int tvId,
            @Query("api_key") String apiKey
    );

    @GET("tv/{tv_id}/credits")
    Call<com.movies.models.CreditsResponse> getTvCredits(
            @Path("tv_id") int tvId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/reviews")
    Call<com.movies.models.ReviewResponse> getMovieReviews(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("tv/{tv_id}/reviews")
    Call<com.movies.models.ReviewResponse> getTvReviews(
            @Path("tv_id") int tvId,
            @Query("api_key") String apiKey
    );

    @GET("tv/top_rated")
    Call<MovieResponse> getTopRatedTvShows(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("discover/movie")
    Call<MovieResponse> getMoviesByGenre(
            @Query("api_key") String apiKey,
            @Query("with_genres") String genreId,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("discover/movie")
    Call<MovieResponse> getMoviesByLanguage(
            @Query("api_key") String apiKey,
            @Query("with_original_language") String languageCode,
            @Query("language") String language,
            @Query("page") int page
    );
}