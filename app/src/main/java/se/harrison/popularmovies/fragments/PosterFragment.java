package se.harrison.popularmovies.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import se.harrison.popularmovies.R;
import se.harrison.popularmovies.adapters.MovieAdapter;
import se.harrison.popularmovies.models.Movie;
import se.harrison.popularmovies.models.MovieResult;
import se.harrison.popularmovies.tasks.FetchMoviesTask;
import se.harrison.popularmovies.utilities.EndlessScrollListener;
import se.harrison.popularmovies.utilities.FavoriteMovieStorage;
import se.harrison.popularmovies.utilities.MovieResultReceiver;

import static se.harrison.popularmovies.utilities.Constants.API_SORTING_HIGHEST_RATED;
import static se.harrison.popularmovies.utilities.Constants.API_SORTING_POPULARITY;
import static se.harrison.popularmovies.utilities.Constants.FETCH_FAVORITES;
import static se.harrison.popularmovies.utilities.Constants.SORTING_HIGHEST_RATED;
import static se.harrison.popularmovies.utilities.Constants.SORTING_POPULARITY;

public class PosterFragment extends Fragment implements MovieResultReceiver {

    private MovieAdapter mMovieAdapter;
    private GridView mGrid;

    private String mSorting = API_SORTING_POPULARITY;
    private String mCountFilter = "0";
    private Integer mPage = 1;
    private Integer mSelectedMovieIndex = 0;
    private MovieResult mMovieResult;

    public interface Callback {
        void onItemSelected(Movie movie, int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poster, container, false);

        mMovieAdapter = new MovieAdapter(view.getContext(), new ArrayList<Movie>());

        mGrid = (GridView) view.findViewById(R.id.gridView);
        mGrid.setAdapter(mMovieAdapter);
        mGrid.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (mSorting.equals(FETCH_FAVORITES)) return false;
                mPage = page;
                updateMovies();
                return true;
            }
        });

        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((Callback) getActivity())
                        .onItemSelected(mMovieAdapter.getItem(position), position);
                view.setSelected(true);
            }
        });

        if (getResources().getBoolean(R.bool.is_landscape)) {
            mGrid.setNumColumns(3);
        }

        restoreState(savedInstanceState);

        return view;
    }

    public void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mMovieResult = savedInstanceState.getParcelable("mMovieResult");
            mSorting = savedInstanceState.getString("mSorting", API_SORTING_POPULARITY);
            mCountFilter = savedInstanceState.getString("mCountFilter", "0");
            mSelectedMovieIndex = savedInstanceState.getInt("mSelectedMovieIndex", 0);
            mPage = savedInstanceState.getInt("mPage", 1);

            if (mMovieResult != null) {
                setupMovies(mMovieResult.getResults());
                return;
            }
        } else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            mSorting = prefs.getString("sorting", API_SORTING_POPULARITY);
            mCountFilter = prefs.getString("count_filter", "0");
            mSelectedMovieIndex = prefs.getInt("selected_movie_index", 0);
            mPage = prefs.getInt("page", 1);
        }

        updateMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("mMovieResult", mMovieResult);
        outState.putString("mSorting", mSorting);
        outState.putString("mCountFilter", mCountFilter);
        outState.putInt("mSelectedIndex", mSelectedMovieIndex);
        outState.putInt("mPage", mPage);

        super.onSaveInstanceState(outState);
    }

    public void changeSorting(String selection) {
        switch (selection) {
            case SORTING_POPULARITY:
                mSorting = API_SORTING_POPULARITY;
                mCountFilter = "0";
                break;
            case SORTING_HIGHEST_RATED:
                mSorting = API_SORTING_HIGHEST_RATED;
                mCountFilter = "10";
                break;
            case FETCH_FAVORITES:
                mSorting = FETCH_FAVORITES;
                mCountFilter = "0";
                break;
            default:
                mSorting = API_SORTING_POPULARITY;
                mCountFilter = "0";
        }

        mPage = 1;
        mSelectedMovieIndex = 0;
        mMovieResult = null;
        updateMovies();
    }

    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit()
                .putString("sorting", mSorting)
                .putString("count_filter", mCountFilter)
                .putInt("page", mPage)
                .apply();

        if (mSorting.equals(FETCH_FAVORITES)) {
            setupMovies(FavoriteMovieStorage.getInstance(getContext()).getFavoriteMovies());
        } else {
            new FetchMoviesTask(this).execute(mSorting, mCountFilter, String.valueOf(mPage));
        }
    }

    public void setupMovies(ArrayList<Movie> movies) {
        mMovieAdapter.setupMovies(movies);
    }

    public void addMovies(ArrayList<Movie> movies) {
        mMovieAdapter.addMovies(movies);
    }

    public void setSelection(int position) {
        mGrid.setSelection(position);
    }

    public void setMovieResult(MovieResult movieResults) {
        mMovieResult = movieResults;

        if (mPage == 1) {
            mSelectedMovieIndex = 0;
            setupMovies(movieResults.getResults());
        } else {
            addMovies(movieResults.getResults());
            setSelection(mSelectedMovieIndex);
        }
    }
}