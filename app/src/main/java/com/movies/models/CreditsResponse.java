package com.movies.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreditsResponse {
    @SerializedName("cast")
    private List<Cast> cast;

    public List<Cast> getCast() {
        return cast;
    }

    public static class Cast {
        @SerializedName("name")
        private String name;
        @SerializedName("profile_path")
        private String profilePath;
        @SerializedName("character")
        private String character;

        public String getName() { return name; }
        public String getProfilePath() { return profilePath; }
        public String getCharacter() { return character; }
    }
}