package se.harrison.popularmovies.activities;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import se.harrison.popularmovies.R;
import se.harrison.popularmovies.models.MovieResult;
import se.harrison.popularmovies.fragments.PosterFragment;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    static final String SORTING_POPULARITY = "Most popular";
    static final String SORTING_HIGHEST_RATED = "Highest rated";

    private PosterFragment mPosterFragment;
    private MovieResult mMovieResult;

    private Spinner mSpinner;
    private Toolbar mToolbar;
    private String mSorting;
    private String mCountFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            mPosterFragment = new PosterFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mPosterFragment, "POSTER_FRAGMENT")
                    .commit();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mSpinner = (Spinner) findViewById(R.id.spinner_sort);
        List<String> sortOptions = Arrays.asList(getResources().getStringArray(R.array.sort_array));
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item,sortOptions);
        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(this);

        mSorting = "popularity.desc";
        mCountFilter = "0";
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("mMovieResult", mMovieResult);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mMovieResult = savedInstanceState.getParcelable("mMovieResult");
        mPosterFragment = (PosterFragment) getSupportFragmentManager().findFragmentByTag("POSTER_FRAGMENT");

        if (mPosterFragment != null && mMovieResult != null) {
            mPosterFragment.addMovies(mMovieResult.getResults());
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        updateMovies();
        super.onStart();
    }

    private void updateMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        Log.d("Wibble", "Updating movies with: " + mSorting);
        fetchMoviesTask.execute(mSorting, mCountFilter);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selection = (String) parent.getAdapter().getItem(position);
        Log.d("Wibble", "Updating sorting with: " + selection + " ");
        switch(selection) {
            case SORTING_POPULARITY:
                mSorting = "popularity.desc";
                mCountFilter = "0";
                break;
            case SORTING_HIGHEST_RATED:
                mSorting = "vote_average.desc";
                mCountFilter = "10";
                break;
            default:
                mSorting = "popularity.desc";
                mCountFilter = "0";
        }

        updateMovies();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieResult> {

        final String LOG_TAG = FetchMoviesTask.class.getName();
        final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
        final String SORTING_PARAM = "sort_by";
        final String API_KEY_PARAM = "api_key";
        final String COUNT_FILTER = "vote_count.gte";

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
                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORTING_PARAM, params[0])
                        .appendQueryParameter(COUNT_FILTER, params[1])
                        .appendQueryParameter(API_KEY_PARAM, getResources().getString(R.string.themoviedb_api_key))
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
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            Log.d(LOG_TAG, moviesJsonStr);
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
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
                }
            }
        }
    }
}
