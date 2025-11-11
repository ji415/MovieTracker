package com.movieapp.features;

import com.movieapp.models.Movie;
import com.movieapp.models.User;

import java.util.*;

public class RecommendationEngine {
    private Map<String, Movie> movieMap;

    public RecommendationEngine(Map<String, Movie> movieMap) {
        this.movieMap = movieMap;
    }

    public List<Movie> recommendByGenre(User user, int topN) {
        Map<String, Integer> countByGenre = new HashMap<>();
        for (String id : user.getHistoryIds()) {
            Movie m = movieMap.get(id);
            if (m != null) countByGenre.put(m.getGenre(), countByGenre.getOrDefault(m.getGenre(),0)+1);
        }
        if (countByGenre.isEmpty()) {
            for (String id : user.getWatchlistIds()) {
                Movie m = movieMap.get(id);
                if (m != null) countByGenre.put(m.getGenre(), countByGenre.getOrDefault(m.getGenre(),0)+1);
            }
        }
        List<String> genresSorted = new ArrayList<>(countByGenre.keySet());
        genresSorted.sort((a,b) -> countByGenre.get(b) - countByGenre.get(a));

        Set<String> seen = new HashSet<>();
        seen.addAll(user.getHistoryIds());
        seen.addAll(user.getWatchlistIds());

        List<Movie> candidates = new ArrayList<>();
        for (String g : genresSorted) {
            for (Movie m : movieMap.values()) {
                if (m.getGenre() != null && m.getGenre().equalsIgnoreCase(g) && !seen.contains(m.getId())) {
                    candidates.add(m);
                }
            }
        }
        candidates.sort((a,b) -> Double.compare(b.getRating(), a.getRating()));
        return candidates.size() > topN ? candidates.subList(0, topN) : candidates;
    }
}
