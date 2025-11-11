package com.movieapp.storage;

import com.movieapp.models.Movie;
import com.movieapp.models.User;

import java.io.*;
import java.util.*;

public class DataManager {
    private final String moviesFile;
    private final String usersFile;

    public DataManager(String moviesFile, String usersFile) {
        this.moviesFile = moviesFile;
        this.usersFile = usersFile;
    }

    public Map<String, Movie> loadMovies() {
        Map<String, Movie> movies = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(moviesFile))) {
            String header = br.readLine();
            if (header == null) return movies;
            String[] head = splitCSV(header);
            Map<String, Integer> idx = headerIndex(head);

            if (!idx.containsKey("id") || !idx.containsKey("title")) {
                System.err.println("Movies CSV 缺少列: id 或 title");
                return movies;
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = splitCSV(line);
                try {
                    String id = safeGet(parts, idx.get("id"));
                    String title = safeGet(parts, idx.get("title"));
                    String genre = safeGet(parts, idx.getOrDefault("genre", -1));
                    int year = idx.containsKey("year") ? Integer.parseInt(safeGet(parts, idx.get("year"))) : 0;
                    double rating = idx.containsKey("rating") && !safeGet(parts, idx.get("rating")).isEmpty()
                            ? Double.parseDouble(safeGet(parts, idx.get("rating"))) : 0.0;
                    movies.put(id, new Movie(id, title, genre, year, rating));
                } catch (Exception ex) {
                    System.err.println("跳过异常行: " + line + "  原因: " + ex.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Movies file not found: " + moviesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public Map<String, User> loadUsers() {
        Map<String, User> users = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(usersFile))) {
            String header = br.readLine();
            if (header == null) return users;
            String[] head = splitCSV(header);
            Map<String, Integer> idx = headerIndex(head);

            if (!idx.containsKey("username") || !idx.containsKey("password")) {
                System.err.println("Users CSV 缺少列: username 或 password");
                return users;
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = splitCSV(line);
                try {
                    String username = safeGet(parts, idx.get("username"));
                    String password = safeGet(parts, idx.get("password"));
                    User u = new User(username, password);
                    String w = idx.containsKey("watchlist") ? safeGet(parts, idx.get("watchlist")) : "";
                    String h = idx.containsKey("history") ? safeGet(parts, idx.get("history")) : "";
                    for (String id : parseIdList(w)) u.getWatchlistIds().add(id);
                    for (String id : parseIdList(h)) u.getHistoryIds().add(id);
                    users.put(username, u);
                } catch (Exception ex) {
                    System.err.println("跳过异常行: " + line + "  原因: " + ex.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Users file not found: " + usersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void saveUsers(Map<String, User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(usersFile, false))) {
            bw.write("username,password,watchlist,history");
            bw.newLine();
            for (User u : users.values()) {
                String watch = joinIds(u.getWatchlistIds());
                String hist = joinIds(u.getHistoryIds());
                String wField = needsQuote(watch) ? ("\"" + watch + "\"") : watch;
                String hField = needsQuote(hist) ? ("\"" + hist + "\"") : hist;
                String line = String.format("%s,%s,%s,%s",
                        u.getUsername(), u.getPassword(), wField, hField);
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 工具方法（同原实现） */
    private String[] splitCSV(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"'); i++;
                } else { inQuotes = !inQuotes; }
            } else if (c == ',' && !inQuotes) {
                parts.add(cur.toString());
                cur.setLength(0);
            } else cur.append(c);
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }

    private Map<String, Integer> headerIndex(String[] header) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < header.length; i++) map.put(header[i].trim().toLowerCase(), i);
        return map;
    }
    private String safeGet(String[] arr, int idx) {
        if (idx < 0 || idx >= arr.length) return "";
        return arr[idx] == null ? "" : arr[idx].trim();
    }

    private List<String> parseIdList(String field) {
        List<String> ids = new ArrayList<>();
        if (field == null) return ids;
        String f = field.trim();
        if (f.startsWith("\"") && f.endsWith("\"") && f.length() >= 2) f = f.substring(1, f.length()-1);
        if (f.isEmpty()) return ids;
        f = f.replace(';', ',').replace('|', ',');
        for (String token : f.split(",")) {
            String t = token.trim();
            if (!t.isEmpty()) ids.add(t);
        }
        return ids;
    }
    private String joinIds(List<String> ids) {
        return String.join(";", ids);
    }
    private boolean needsQuote(String s) {
        return s.contains(",") || s.contains(";") || s.contains("\"");
    }
}
