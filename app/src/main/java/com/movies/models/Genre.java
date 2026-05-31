package com.movies.models;

public class Genre {
    private String id;
    private String name;
    private String posterPath;
    private String languageCode;

    public Genre(String id, String name, String languageCode) {
        this.id = id;
        this.name = name;
        this.languageCode = languageCode;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    public String getLanguageCode() { return languageCode; }
}