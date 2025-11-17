package com.movieapp.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private List<String> watchlistIds; // 存 String id
    private List<String> historyIds;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.watchlistIds = new ArrayList<>();
        this.historyIds = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public void setPassword(String newPwd) { this.password = newPwd; }  // change password

    public List<String> getWatchlistIds() { return watchlistIds; }
    public List<String> getHistoryIds() { return historyIds; }

    public void addToWatchlist(String movieId) {
        if (!watchlistIds.contains(movieId)) watchlistIds.add(movieId);
    }
    public void removeFromWatchlist(String movieId) {
        watchlistIds.remove(movieId);
    }
    public void markAsWatched(String movieId) {
        if (!historyIds.contains(movieId)) historyIds.add(movieId);
        removeFromWatchlist(movieId);
    }
}
