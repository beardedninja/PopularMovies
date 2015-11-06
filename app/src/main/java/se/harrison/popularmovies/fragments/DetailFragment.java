package se.harrison.popularmovies.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import se.harrison.popularmovies.R;
import se.harrison.popularmovies.adapters.MovieAdapter;
import se.harrison.popularmovies.adapters.MovieDetailsAdapter;
import se.harrison.popularmovies.models.Movie;
import se.harrison.popularmovies.models.Review;
import se.harrison.popularmovies.models.ReviewsResult;
import se.harrison.popularmovies.models.Trailer;
import se.harrison.popularmovies.tasks.FetchMovieTask;
import se.harrison.popularmovies.utilities.Constants;
import se.harrison.popularmovies.utilities.EndlessScrollListener;

/**
 * Created by alex on 30/10/15.
 */
public class DetailFragment extends Fragment {

    public static String MOVIE = "movie";

    private Movie mMovie;
    private MovieDetailsAdapter mMovieDetailsAdapter;
    private ListView mMovieListView;

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

        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        titleTextView.setText(mMovie.title);

        mMovieListView = (ListView) view.findViewById(R.id.movieListView);

        mMovieDetailsAdapter = new MovieDetailsAdapter(mMovie, getContext());
        mMovieListView.setAdapter(mMovieDetailsAdapter);

        mMovieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mMovieDetailsAdapter.getItemViewType(position) == MovieDetailsAdapter.MOVIE_TRAILERS) {
                    String source = ((Trailer) mMovieDetailsAdapter.getItem(position)).source;
                    Uri builtUri = Uri.parse(Constants.YOUTUBE_URL).buildUpon()
                            .appendQueryParameter(Constants.VIDEO_PARAM, source)
                            .build();
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, builtUri));
                }
            }
        });

        return view;
    }

    public void setExtraMovieDetail(Movie movie) {
        // Base movie information replaced with the more detailed information from the FetchMovieTask
        mMovie = movie;

        mMovieDetailsAdapter = new MovieDetailsAdapter(movie, getContext());
        mMovieListView.setAdapter(mMovieDetailsAdapter);
    }
}
