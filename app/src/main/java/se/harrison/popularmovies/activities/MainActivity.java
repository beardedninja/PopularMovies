package se.harrison.popularmovies.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;

import se.harrison.popularmovies.R;
import se.harrison.popularmovies.fragments.DetailFragment;
import se.harrison.popularmovies.fragments.PosterFragment;
import se.harrison.popularmovies.models.Movie;
import se.harrison.popularmovies.utilities.Constants;
import se.harrison.popularmovies.utilities.FavoriteMovieStorage;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        PosterFragment.Callback {

    private String mSorting = Constants.SORTING_POPULARITY;
    private int mSelectedMovieIndex = 0;
    private boolean mTwoPane;

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSelectedMovieIndex = 0;

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner mSpinner = (Spinner) findViewById(R.id.spinner_sort);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item,
                        Arrays.asList(getResources().getStringArray(R.array.sort_array)));

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }

        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(sortValueToIndex(), false);
        mSpinner.setOnItemSelectedListener(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {

            @Override
            public void run() {
                FavoriteMovieStorage.getInstance(getApplicationContext());
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        FavoriteMovieStorage.getInstance(this).storeFavorites(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("mSorting", mSorting);
        outState.putInt("mSelectedIndex", mSelectedMovieIndex);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        restoreState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void restoreState(Bundle savedInstanceState) {
        mSorting = savedInstanceState.getString("mSorting", Constants.SORTING_POPULARITY);
        mSelectedMovieIndex = savedInstanceState.getInt("mSelectedMovieIndex", 0);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selection = (String) parent.getAdapter().getItem(position);

        if (!mSorting.equals(selection)) {
            mSelectedMovieIndex = 0;
            mSorting = selection;
            PosterFragment posterFragment = (PosterFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_poster);
            posterFragment.changeSorting(mSorting);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                    .commit();
        }
    }

    public int sortValueToIndex() {
        switch (mSorting) {
            case Constants.API_SORTING_POPULARITY:
                return 0;
            case Constants.API_SORTING_HIGHEST_RATED:
                return 1;
            case Constants.FETCH_FAVORITES:
                return 2;
            default:
                return 0;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemSelected(Movie movie, int position) {
        mSelectedMovieIndex = position;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putInt("selected_movie_index", mSelectedMovieIndex).apply();

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable("movie", movie);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
        }
    }
}