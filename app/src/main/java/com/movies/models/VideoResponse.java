package com.movies.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VideoResponse {
    @SerializedName("results")
    private List<Video> results;

    public List<Video> getResults() {
        return results;
    }

    public static class Video {
        @SerializedName("key")
        private String key;
        @SerializedName("site")
        private String site;
        @SerializedName("type")
        private String type;

        public String getKey() { return key; }
        public String getSite() { return site; }
        public String getType() { return type; }
    }
}