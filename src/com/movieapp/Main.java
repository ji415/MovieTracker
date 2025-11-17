package com.movieapp;

import com.movieapp.features.RecommendationEngine;
import com.movieapp.models.Movie;
import com.movieapp.models.User;
import com.movieapp.storage.DataManager;

import java.util.*;

public class Main {
    private static final String MOVIES_FILE = "movies.csv";
    private static final String USERS_FILE = "users.csv";

    private Map<String, Movie> movies;
    private Map<String, User> users;
    private DataManager dataManager;
    private final Scanner scanner = new Scanner(System.in);
    private User currentUser = null;

    public Main() {
        dataManager = new DataManager(MOVIES_FILE, USERS_FILE);
        movies = dataManager.loadMovies();
        users = dataManager.loadUsers();
        System.out.println("Loaded movies: " + movies.size() + ", users: " + users.size());
    }

    public void run() {
        while (true) {
            if (currentUser == null) showLoggedOutMenu();
            else showLoggedInMenu();
        }
    }

    private void showLoggedOutMenu() {
        System.out.println("\n=== Movie Tracker ===");
        System.out.println("1. Login");
        System.out.println("2. Register");  // register new user
        System.out.println("3. Exit");
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1": login(); break;
            case "2": register(); break;
            case "3": System.out.println("Bye!"); System.exit(0); break;
            default: System.out.println("Invalid choice");
        }
    }

    private void login() {
        System.out.print("Username: ");
        String u = scanner.nextLine().trim();
        System.out.print("Password: ");
        String p = scanner.nextLine().trim();
        User user = users.get(u);
        if (user != null && user.getPassword().equals(p)) {
            currentUser = user;
            System.out.println("Logged in as " + u);
        } else {
            System.out.println("Login failed.");
        }
    }

    private void showLoggedInMenu() {
        System.out.println("\n=== Menu (logged in as " + currentUser.getUsername() + ") ===");
        System.out.println("1. Browse movies");
        System.out.println("2. Add movie to watchlist");
        System.out.println("3. Remove movie from watchlist");
        System.out.println("4. View watchlist");
        System.out.println("5. Mark movie as watched");
        System.out.println("6. View history");
        System.out.println("7. Get recommendations");
        System.out.println("8. Logout");
        System.out.print("Choose: ");
        String c = scanner.nextLine().trim();
        RecommendationEngine engine = new RecommendationEngine(movies);
        switch (c) {
            case "1": browseMovies(); break;
            case "2": addToWatchlist(); break;
            case "3": removeFromWatchlist(); break;
            case "4": viewWatchlist(); break;
            case "5": markWatched(); break;
            case "6": viewHistory(); break;
            case "7":
                System.out.print("Top N? ");
                int n = parseIntSafe(scanner.nextLine().trim(), 5);
                List<Movie> recs = engine.recommendByGenre(currentUser, n);
                if (recs.isEmpty()) System.out.println("No recommendations available.");
                else for (Movie m : recs) System.out.println(m);
                break;
            case "8": changePassword(); break;
            case "9": logout(); break;
            default: System.out.println("Invalid choice");
        }
        dataManager.saveUsers(users);
    }

    private void browseMovies() {
        System.out.println("--- All movies ---");
        movies.values().stream().sorted(Comparator.comparing(Movie::getId)).forEach(System.out::println);
    }

    private void addToWatchlist() {
        System.out.print("Enter movie ID to add (e.g. M001): ");
        String id = scanner.nextLine().trim();
        if (!movies.containsKey(id)) { System.out.println("Movie not found."); return; }
        currentUser.addToWatchlist(id);
        System.out.println("Added.");
    }

    private void removeFromWatchlist() {
        System.out.print("Enter movie ID to remove: ");
        String id = scanner.nextLine().trim();
        currentUser.removeFromWatchlist(id);
        System.out.println("Removed if existed.");
    }

    private void viewWatchlist() {
        System.out.println("--- Watchlist ---");
        for (String id : currentUser.getWatchlistIds()) {
            Movie m = movies.get(id);
            if (m != null) System.out.println(m);
        }
    }

    private void markWatched() {
        System.out.print("Enter movie ID to mark watched: ");
        String id = scanner.nextLine().trim();
        if (!movies.containsKey(id)) { System.out.println("Movie not found."); return; }
        currentUser.markAsWatched(id);
        System.out.println("Marked as watched.");
    }

    private void viewHistory() {
        System.out.println("--- History ---");
        for (String id : currentUser.getHistoryIds()) {
            Movie m = movies.get(id);
            if (m != null) System.out.println(m);
        }
    }

    private void logout() {
        System.out.println("Logging out " + currentUser.getUsername());
        currentUser = null;
    }

    private int parseIntSafe(String s, int fallback) {
        try { return Integer.parseInt(s); } catch (Exception e) { return fallback; }
    }

    private void register() {
        System.out.println("\n--- Register New User ---");
        System.out.print("Username: ");
        String u = scanner.nextLine().trim();
        if (u.isEmpty()) { System.out.println("Username cannot be empty."); return; }
        if (users.containsKey(u)) { System.out.println("Username already exists."); return; }

        System.out.print("Password (min 4 chars): ");
        String p1 = scanner.nextLine();
        System.out.print("Confirm password: ");
        String p2 = scanner.nextLine();

        if (!p1.equals(p2)) { System.out.println("Passwords do not match."); return; }
        if (p1.length() < 4) { System.out.println("Password too short."); return; }

        User newUser = new User(u, p1);
        users.put(u, newUser);
        dataManager.saveUsers(users);
        System.out.println("Register success. You can login now.");
    }

    private void changePassword() {
        System.out.println("\n--- Change Password ---");
        System.out.print("Current password: ");
        String cur = scanner.nextLine();
        if (!currentUser.getPassword().equals(cur)) {
            System.out.println("Current password incorrect.");
            return;
        }
        System.out.print("New password (min 4 chars): ");
        String p1 = scanner.nextLine();
        System.out.print("Confirm new password: ");
        String p2 = scanner.nextLine();

        if (!p1.equals(p2)) { System.out.println("Passwords do not match."); return; }
        if (p1.length() < 4) { System.out.println("Password too short."); return; }

        currentUser.setPassword(p1);
        users.put(currentUser.getUsername(), currentUser); // 覆盖更新
        dataManager.saveUsers(users);
        System.out.println("Password updated.");
    }
    public static void main(String[] args) {
        new Main().run();
    }
}

