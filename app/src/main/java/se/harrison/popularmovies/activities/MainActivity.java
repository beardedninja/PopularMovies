package se.harrison.popularmovies.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import se.harrison.popularmovies.R;
import se.harrison.popularmovies.fragments.PosterFragment;
import se.harrison.popularmovies.models.Movie;
import se.harrison.popularmovies.models.MovieResult;
import se.harrison.popularmovies.tasks.FetchMoviesTask;
import se.harrison.popularmovies.utilities.Constants;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, PosterFragment.PagingListener {

    private PosterFragment mPosterFragment;
    private MovieResult mMovieResult;

    private String mSorting;
    private String mCountFilter;
    private int mSelectedMovieIndex;
    private int mPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String mDefaultSorting = Constants.API_SORTING_POPULARITY;
        String mDefaultCountFilter = "0";
        mSelectedMovieIndex = 0;

        if (savedInstanceState == null) {
            mPosterFragment = new PosterFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mPosterFragment, "POSTER_FRAGMENT")
                    .commit();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            mSorting = prefs.getString("sorting", mDefaultSorting);
            mCountFilter = prefs.getString("count_filter", mDefaultCountFilter);
            mSelectedMovieIndex = prefs.getInt("selected_movie_index", 0);
            mPage = prefs.getInt("page", 1);
        } else {
            mSorting = mDefaultSorting;
            mCountFilter = mDefaultCountFilter;
            mPage = 1;
            restoreState(savedInstanceState);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner mSpinner = (Spinner) findViewById(R.id.spinner_sort);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item,
                        Arrays.asList(getResources().getStringArray(R.array.sort_array)));

        mSpinner.setAdapter(adapter);
        sortValueToIndex();
        mSpinner.setSelection(sortValueToIndex(), false);
        mSpinner.setOnItemSelectedListener(MainActivity.this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("mMovieResult", mMovieResult);
        outState.putString("mSorting", mSorting);
        outState.putString("mCountFilter", mCountFilter);
        outState.putInt("mSelectedIndex", mSelectedMovieIndex);
        outState.putInt("mPage", mPage);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        restoreState(savedInstanceState);

        super.onRestoreInstanceState(savedInstanceState);
    }

    public void restoreState(Bundle savedInstanceState) {
        mMovieResult = savedInstanceState.getParcelable("mMovieResult");
        mPosterFragment = (PosterFragment) getSupportFragmentManager().findFragmentByTag("POSTER_FRAGMENT");
        mSorting = savedInstanceState.getString("mSorting");
        mCountFilter = savedInstanceState.getString("mCountFilter");
        mSelectedMovieIndex = savedInstanceState.getInt("mSelectedMovieIndex", 0);
        mPage = savedInstanceState.getInt("mPage", 1);

        if (mPosterFragment != null && mMovieResult != null) {
            mPosterFragment.addMovies(mMovieResult.getResults());
        }
    }

    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit()
                .putString("sorting", mSorting)
                .putString("count_filter", mCountFilter)
                .putInt("page", mPage)
                .apply();

        new FetchMoviesTask(this).execute(mSorting, mCountFilter, "" + mPage);
    }

    public void setMovieResult(MovieResult movieResults) {
        if (mPosterFragment != null) {
            if (mPage == 1) {
                mSelectedMovieIndex = 0;
                mPosterFragment.setupMovies(movieResults.getResults());
            } else {
                mPosterFragment.addMovies(movieResults.getResults());
                mPosterFragment.setSelection(mSelectedMovieIndex);
            }

        }
    }

    public void setSelectedMovie(int position) {
        mSelectedMovieIndex = position;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putInt("selected_movie_index", mSelectedMovieIndex).apply();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selection = (String) parent.getAdapter().getItem(position);
        String currentSorting = mSorting;

        switch (selection) {
            case Constants.SORTING_POPULARITY:
                mSorting = Constants.API_SORTING_POPULARITY;
                mCountFilter = "0";
                break;
            case Constants.SORTING_HIGHEST_RATED:
                mSorting = Constants.API_SORTING_HIGHEST_RATED;
                mCountFilter = "10";
                break;
            default:
                mSorting = Constants.API_SORTING_POPULARITY;
                mCountFilter = "0";
        }

        if (!currentSorting.equals(mSorting) || mMovieResult == null) {
            mPage = 1;
            mSelectedMovieIndex = 0;
            updateMovies();
        }

    }

    public int sortValueToIndex() {
        switch (mSorting) {
            case Constants.API_SORTING_POPULARITY:
                return 0;
            case Constants.API_SORTING_HIGHEST_RATED:
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean loadMore(int page) {
        mPage = page;
        updateMovies();
        return true;
    }
}
