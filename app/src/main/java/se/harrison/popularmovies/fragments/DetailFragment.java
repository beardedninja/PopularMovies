package se.harrison.popularmovies.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import se.harrison.popularmovies.R;
import se.harrison.popularmovies.adapters.MovieAdapter;
import se.harrison.popularmovies.models.Movie;
import se.harrison.popularmovies.utilities.Constants;
import se.harrison.popularmovies.utilities.EndlessScrollListener;

/**
 * Created by alex on 30/10/15.
 */
public class DetailFragment extends Fragment {

    public static String MOVIE = "movie";

    private Movie mMovie;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = arguments.getParcelable(DetailFragment.MOVIE);
        }

        View view =  inflater.inflate(R.layout.fragment_detail, container, false);

        ImageView backdrop = (ImageView) view.findViewById(R.id.backdropImageView);
        Picasso.with(getContext()).load(Constants.THUMBNAIL_URL + mMovie.backdropPath).into(backdrop);

        TextView title = (TextView) view.findViewById(R.id.titleTextView);
        title.setText(mMovie.title);

        TextView year = (TextView) view.findViewById(R.id.yearTextView);
        year.setText(mMovie.getReleaseYear());

        TextView voteAverage = (TextView) view.findViewById(R.id.ratingTextView);
        voteAverage.setText(mMovie.voteAverage + "/10");

        TextView synopsis = (TextView) view.findViewById(R.id.synopsisTextView);
        synopsis.setText(mMovie.synopsis);

        ImageView poster = (ImageView) view.findViewById(R.id.posterImageView);
        Picasso.with(getContext()).load(Constants.THUMBNAIL_URL + mMovie.posterPath).into(poster);

        return view;
    }
}
