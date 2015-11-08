package se.harrison.popularmovies.utilities;

import android.content.Context;

import se.harrison.popularmovies.models.Movie;

/**
 * Created by alex on 07/11/15.
 */
public interface MovieReceiver {
    void setMovie(Movie movie);

    Context getContext();
}