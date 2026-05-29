// File: com/expense/models/UserModel.java
package com.movies.models;

public class User {
    private String userId;
    private String username;
    private String email;
    private String provider; // "email", "google", "apple"
    private long createdAt;
    private boolean isEmailVerified;

    // Required for Firestore
    public User() {}

    public User(String userId, String username, String email, String provider) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.provider = provider;
        this.createdAt = System.currentTimeMillis();
        this.isEmailVerified = false;
    }

    // Getters & Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isEmailVerified() { return isEmailVerified; }
    public void setEmailVerified(boolean verified) { isEmailVerified = verified; }
}