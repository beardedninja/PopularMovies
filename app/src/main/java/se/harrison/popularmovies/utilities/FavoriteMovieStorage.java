package se.harrison.popularmovies.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

import se.harrison.popularmovies.models.Movie;

/**
 * Created by alex on 07/11/15.
 *
 * A singleton for keeping track of movie favorites in shared preferences. A second ArrayList
 * of movie ids called mFavoriteLookup is implemented as an index to stop entire scans of primary
 * ArrayList and remove edge cases when Movies get replaced with more detailed Movies with reviews
 * and trailers.
 *
 */
public class FavoriteMovieStorage {
    private static final String FAVORITES_KEY = "favorites";

    private static FavoriteMovieStorage singleton;

    ArrayList<Movie> mFavoriteMovies;
    ArrayList<Integer> mFavoriteLookup;

    // Prevents instantiation
    protected FavoriteMovieStorage(){}

    protected FavoriteMovieStorage(Context context){
        loadFavorites(context);
    }

    public static FavoriteMovieStorage getInstance(Context context) {
        if (singleton == null) {
            singleton = new FavoriteMovieStorage(context);
        }
        return singleton;
    }

    private void loadFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(FAVORITES_KEY,
                Context.MODE_PRIVATE);

        Type type = new TypeToken<ArrayList<Movie>>() {}.getType();
        Gson gson = new Gson();
        mFavoriteMovies = gson.fromJson(prefs.getString("favorite_movies", ""), type);

        Type lookupType = new TypeToken<ArrayList<Integer>>() {}.getType();
        mFavoriteLookup = gson.fromJson(prefs.getString("favorite_movies_lookup", ""), lookupType);
    }

    public void storeFavorites(Context context) {
        if (mFavoriteMovies == null || mFavoriteMovies.isEmpty()) return;

        SharedPreferences prefs = context.getSharedPreferences(FAVORITES_KEY,
                Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String favoriteMovies = gson.toJson(mFavoriteMovies);
        prefs.edit().putString("favorite_movies", favoriteMovies).apply();

        String favoriteLookup = gson.toJson(mFavoriteLookup);
        prefs.edit().putString("favorite_movies_lookup", favoriteLookup).apply();
    }

    private void ensureStorage() {
        if (mFavoriteMovies == null) {
            mFavoriteMovies = new ArrayList<>();
        }

        if (mFavoriteLookup == null) {
            mFavoriteLookup = new ArrayList<>();
        }
    }

    public void addMovie(Movie movie) {
        ensureStorage();

        if (!mFavoriteLookup.contains(movie.id)) {
            mFavoriteMovies.add(movie);
            mFavoriteLookup.add(movie.id);
        }
    }

    public void removeMovie(Movie movie) {
        ensureStorage();

        if (mFavoriteLookup.size() > 0 && mFavoriteLookup.contains(movie.id)) {
            int index = mFavoriteLookup.indexOf(movie.id);
            mFavoriteMovies.remove(index);
            mFavoriteLookup.remove(index);
        }
    }

    public boolean isFavorited(Movie movie) {
        ensureStorage();

        return mFavoriteLookup.contains(movie.id);
    }

    public ArrayList<Movie> getFavoriteMovies() {
        return mFavoriteMovies;
    }
}