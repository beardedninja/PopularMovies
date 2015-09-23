package se.harrison.popularmovies.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import se.harrison.popularmovies.R;
import se.harrison.popularmovies.models.Movie;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();
        bundle.setClassLoader(Movie.class.getClassLoader());
        Movie movie = bundle.getParcelable("movie");

        if (movie == null) {
            finish();
        } else {
            setupToolbar();
        }

        ImageView backdrop = (ImageView) findViewById(R.id.backdropImageView);
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + movie.backdropPath).into(backdrop);

        TextView title = (TextView) findViewById(R.id.titleTextView);
        title.setText(movie.title);

        TextView year = (TextView) findViewById(R.id.yearTextView);
        year.setText(movie.getReleaseYear());

        TextView voteAverage = (TextView) findViewById(R.id.ratingTextView);
        voteAverage.setText(movie.voteAverage + "/10");

        TextView synopsis = (TextView) findViewById(R.id.synopsisTextView);
        synopsis.setText(movie.synopsis);

        ImageView poster = (ImageView) findViewById(R.id.posterImageView);
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + movie.posterPath).into(poster);
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
