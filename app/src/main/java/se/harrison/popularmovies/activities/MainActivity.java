package se.harrison.popularmovies.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import se.harrison.popularmovies.models.MovieResult;
import se.harrison.popularmovies.utilities.Constants;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private PosterFragment mPosterFragment;
    private MovieResult mMovieResult;

    private String mSorting;
    private String mCountFilter;
    private int mSelectedMovieIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String mDefaultSorting = "popularity.desc";
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
        } else {
            mSorting = mDefaultSorting;
            mCountFilter = mDefaultSorting;
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

        if (mPosterFragment != null && mMovieResult != null) {
            mPosterFragment.addMovies(mMovieResult.getResults());
        }
    }

    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit()
                .putString("sorting", mSorting)
                .putString("count_filter", mCountFilter)
                .apply();

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute(mSorting, mCountFilter);
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

        if (!currentSorting.equals(mSorting) || mMovieResult == null) updateMovies();

    }

    public int sortValueToIndex() {
        switch (mSorting) {
            case "popularity.desc":
                return 0;
            case "vote_average.desc":
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieResult> {

        ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            mDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Loading", true, false);
        }

        @Override
        protected MovieResult doInBackground(String... params) {

            if (params.length == 0) return null;

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                Uri builtUri = Uri.parse(Constants.MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(Constants.SORTING_PARAM, params[0])
                        .appendQueryParameter(Constants.COUNT_FILTER, params[1])
                        .appendQueryParameter(Constants.API_KEY_PARAM, getResources().getString(R.string.themoviedb_api_key))
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(Constants.LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                @Override
                public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
                        throws JsonParseException {
                    try {
                        return df.parse(json.getAsString());
                    } catch (ParseException e) {
                        return null;
                    }
                }
            }).create();

            return gson.fromJson(moviesJsonStr, MovieResult.class);
        }

        @Override
        protected void onPostExecute(MovieResult movieResults) {
            if (movieResults != null) {
                mMovieResult = movieResults;
                if (mPosterFragment != null) {
                    mPosterFragment.addMovies(movieResults.getResults());
                    mPosterFragment.setSelection(mSelectedMovieIndex);
                }
            }

            if (mDialog != null) mDialog.dismiss();
        }
    }
}
