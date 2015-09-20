package se.harrison.popularmovies.activities;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import se.harrison.popularmovies.R;
import se.harrison.popularmovies.models.MovieResult;
import se.harrison.popularmovies.fragments.PosterFragment;

public class MainActivity extends AppCompatActivity {

    PosterFragment mPosterFragment;
    MovieResult mMovieResult;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        updateMovies();
        super.onStart();
    }

    private void updateMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute("popularity.desc");
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieResult> {

        final String LOG_TAG = FetchMoviesTask.class.getName();
        final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
        final String SORTING_PARAM = "sort_by";
        final String API_KEY_PARAM = "api_key";

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

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
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