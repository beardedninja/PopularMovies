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
}
