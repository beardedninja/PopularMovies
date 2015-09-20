package se.harrison.popularmovies.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;

import java.util.ArrayList;

import se.harrison.popularmovies.R;
import se.harrison.popularmovies.adapters.MovieAdapter;
import se.harrison.popularmovies.models.Movie;
import se.harrison.popularmovies.models.MovieResult;

/**
 * A placeholder fragment containing a simple view.
 */
public class PosterFragment extends Fragment implements AbsListView.OnScrollListener {

    private MovieAdapter mMovieAdapter;
    private GridView mGrid;

    public PosterFragment() {
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_poster, container, false);

        mMovieAdapter = new MovieAdapter(view.getContext(), new ArrayList<Movie>());
        mMovieAdapter.notifyDataSetChanged();

        mGrid = (GridView) view.findViewById(R.id.gridView);
        mGrid.setAdapter(mMovieAdapter);

        mGrid.setOnScrollListener(this);

        if (getResources().getBoolean(R.bool.is_landscape)) {
            mGrid.setNumColumns(3);
        }

        return view;
    }

    public void addMovies(ArrayList<Movie> movies) {
        mMovieAdapter.addMovies(movies);
        mMovieAdapter.notifyDataSetChanged();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.d("ONSCROLL", "FIRST VISIBLE ITEM:" + firstVisibleItem);
        Log.d("ONSCROLL", "VISIBLE ITEM COUNT:" + visibleItemCount);
    }
}