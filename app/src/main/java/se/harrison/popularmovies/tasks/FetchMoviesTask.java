package se.harrison.popularmovies.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

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
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import se.harrison.popularmovies.R;
import se.harrison.popularmovies.activities.MainActivity;
import se.harrison.popularmovies.models.MovieResult;
import se.harrison.popularmovies.utilities.Constants;

/**
 * Created by alex on 30/10/15.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, MovieResult> {

    ProgressDialog mDialog;
    Handler mHandler;
    Runnable mDialogRunnable;
    WeakReference<Activity> mActivityReference;

    public FetchMoviesTask(Activity activity) {
        mActivityReference = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        mHandler = new Handler();
        if (mActivityReference.get() != null) {
            mDialogRunnable = new Runnable() {
                @Override
                public void run() {
                    Context context = mActivityReference.get();
                    if (context != null) {
                        mDialog = ProgressDialog.show(
                                context,
                                context.getResources().getString(R.string.please_wait),
                                context.getResources().getString(R.string.loading),
                                true, false);
                    }
                }
            };
            mHandler.postDelayed(mDialogRunnable, 100);
        }
    }

    @Override
    protected MovieResult doInBackground(String... params) {

        if (params.length == 0) return null;

        if (mActivityReference.get() == null) return null;

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
                    .appendQueryParameter(Constants.API_KEY_PARAM, mActivityReference.get().getResources().getString(R.string.themoviedb_api_key))
                    .appendQueryParameter(Constants.PAGE_PARAM, params[2])
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d(Constants.LOG_TAG, "URL: " + url.toString());

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
        if (movieResults != null && mActivityReference.get() != null) {
            ((MainActivity) mActivityReference.get()).setMovieResult(movieResults);
        }

        mHandler.removeCallbacks(mDialogRunnable);
        if (mDialog != null) mDialog.dismiss();
    }
}
