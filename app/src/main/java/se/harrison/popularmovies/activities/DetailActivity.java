package se.harrison.popularmovies.activities;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import se.harrison.popularmovies.R;
import se.harrison.popularmovies.models.Movie;

public class DetailActivity extends AppCompatActivity {
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();
        mMovie = bundle.getParcelable("movie");

        if (mMovie == null) {
            finish();
        } else {
            setupToolbar();
        }

        ImageView backdrop = (ImageView) findViewById(R.id.backdropImageView);
        backdrop.setScaleType(ImageView.ScaleType.FIT_XY);

        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + mMovie.backdropPath).into(backdrop);

        TextView title = (TextView) findViewById(R.id.titleTextView);
        title.setText(mMovie.title);

        TextView year = (TextView) findViewById(R.id.yearTextView);
        year.setText(mMovie.getReleaseYear());

        TextView voteAverage = (TextView) findViewById(R.id.ratingTextView);
        voteAverage.setText(mMovie.voteAverage + "/10");

        TextView synopsis = (TextView) findViewById(R.id.synopsisTextView);
        synopsis.setText(mMovie.synopsis);

        ImageView poster = (ImageView) findViewById(R.id.posterImageView);
        poster.setScaleType(ImageView.ScaleType.FIT_CENTER);
        poster.setAdjustViewBounds(true);

        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + mMovie.posterPath).into(poster);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
}
