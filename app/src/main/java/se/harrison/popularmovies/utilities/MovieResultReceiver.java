package se.harrison.popularmovies.utilities;

import android.content.Context;

import se.harrison.popularmovies.models.MovieResult;

/**
 * Created by alex on 07/11/15.
 */
public interface MovieResultReceiver {
    void setMovieResult(MovieResult results);

    Context getContext();
}