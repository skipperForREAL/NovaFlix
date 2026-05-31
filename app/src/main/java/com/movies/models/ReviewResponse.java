package com.movies.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReviewResponse {
    @SerializedName("results")
    private List<Review> results;

    public List<Review> getResults() {
        return results;
    }

    public static class Review {
        @SerializedName("author")
        private String author;
        @SerializedName("content")
        private String content;
        @SerializedName("author_details")
        private AuthorDetails authorDetails;

        public String getAuthor() { return author; }
        public String getContent() { return content; }
        public AuthorDetails getAuthorDetails() { return authorDetails; }
    }

    public static class AuthorDetails {
        @SerializedName("rating")
        private Double rating;
        @SerializedName("avatar_path")
        private String avatarPath;

        public Double getRating() { return rating; }
        public String getAvatarPath() { return avatarPath; }
    }
}