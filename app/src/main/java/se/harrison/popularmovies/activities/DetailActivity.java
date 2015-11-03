package se.harrison.popularmovies.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import se.harrison.popularmovies.R;
import se.harrison.popularmovies.fragments.DetailFragment;
import se.harrison.popularmovies.models.Movie;
import se.harrison.popularmovies.models.Review;
import se.harrison.popularmovies.models.Trailer;
import se.harrison.popularmovies.tasks.FetchMovieTask;
import se.harrison.popularmovies.tasks.FetchMoviesTask;
import se.harrison.popularmovies.utilities.Constants;

public class DetailActivity extends AppCompatActivity {
    private DetailFragment mDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.MOVIE, getIntent().getExtras().getParcelable("movie"));

            Movie movie = getIntent().getExtras().getParcelable("movie");
            if (movie != null && movie.id > 0) {
                new FetchMovieTask(this).execute("" + movie.id);
            }

            mDetailFragment = new DetailFragment();
            mDetailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, mDetailFragment)
                    .commit();
        }

        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setMovie(Movie movie) {
        Log.d(Constants.LOG_TAG, "Movie! Trailers: - " + movie.trailers.get("youtube").size());
        ArrayList<Trailer> youtubeTrailers = movie.trailers.get("youtube");
        for (Trailer trailer : youtubeTrailers) {
            Log.d(Constants.LOG_TAG, "Name: " + trailer.name + ", Type: " + trailer.type + ", Source: " + trailer.source);
        }

        ArrayList<Review> reviews = movie.review_result.results;

        for (Review review : reviews) {
            Log.d(Constants.LOG_TAG, "Author: " + review.author + ", Contents:\n" + review.content);
        }
    }
}
