package se.harrison.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import se.harrison.popularmovies.R;
import se.harrison.popularmovies.models.Movie;
import se.harrison.popularmovies.models.Review;
import se.harrison.popularmovies.models.Trailer;
import se.harrison.popularmovies.utilities.Constants;

/**
 * Created by alex on 05/11/15.
 */
public class MovieDetailsAdapter implements ListAdapter {
    private Movie mMovie;
    private Context mContext;
    private int reviewCount = 0;
    private int trailerCount = 0;

    public static final int MOVIE_DETAILS = 0;
    public static final int MOVIE_TRAILERS = 1;
    public static final int MOVIE_REVIEWS = 2;

    public MovieDetailsAdapter(Movie movie, Context context) {
        mMovie = movie;
        mContext = context;
        if (movie.review_result != null && movie.review_result.results != null) {
            reviewCount = movie.review_result.results.size();
        }

        if (movie.trailers != null && movie.trailers.get("youtube") != null) {
            trailerCount = movie.trailers.get("youtube").size();
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return 1 + trailerCount + reviewCount;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return mMovie;
        } else if (position > 0 && position <= trailerCount && trailerCount > 0){
            return mMovie.trailers.get("youtube").get(position-1);
        } else if (position > 0 && position <= trailerCount + reviewCount && reviewCount > 0) {
            return mMovie.review_result.results.get(position - 1 - trailerCount);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view;
        switch(getItemViewType(position)) {
            case MOVIE_DETAILS:
                view = inflateDetails(inflater);
                break;
            case MOVIE_TRAILERS:
                view = inflateTrailer(inflater, position);
                break;
            case MOVIE_REVIEWS:
                view = inflateReview(inflater, position);
                break;
            default:
                view = convertView;
                Log.e(Constants.LOG_TAG, "Unrecognisable view type in MovieDetailsAdapter");
        }
        return view;
    }

    private View inflateReview(LayoutInflater inflater, Integer position) {
        Review review = (Review) getItem(position);
        View reviewView = inflater.inflate(R.layout.movie_review, null);

        if (getItemViewType(position - 1) != MOVIE_REVIEWS) {
            TextView headline = (TextView) reviewView.findViewById(R.id.reviewsHeadline);
            headline.setVisibility(View.VISIBLE);
        }

        TextView reviewContents = (TextView) reviewView.findViewById(R.id.reviewTextView);
        reviewContents.setText(review.content);

        TextView author = (TextView) reviewView.findViewById(R.id.authorTextView);
        author.setText(review.author);


        return reviewView;
    }

    private View inflateTrailer(LayoutInflater inflater, Integer position) {
        Trailer trailer = (Trailer) getItem(position);
        View trailerView = inflater.inflate(R.layout.movie_trailer, null);

        if (getItemViewType(position - 1) == MOVIE_DETAILS) {
            TextView headline = (TextView) trailerView.findViewById(R.id.trailersHeadline);
            headline.setVisibility(View.VISIBLE);
        }

        TextView title = (TextView) trailerView.findViewById(R.id.trailerTitleTextView);
        title.setText(
                String.format(
                        mContext.getResources().getString(R.string.trailer_tagline),
                        trailer.name, trailer.size
                )
        );

        return trailerView;
    }

    private View inflateDetails(LayoutInflater inflater) {
        View detailView = inflater.inflate(R.layout.movie_details, null);

        TextView year = (TextView) detailView.findViewById(R.id.yearTextView);
        year.setText(mMovie.getReleaseYear());

        TextView voteAverage = (TextView) detailView.findViewById(R.id.ratingTextView);
        voteAverage.setText(
                String.format(
                        mContext.getResources().getString(R.string.vote_score),
                        mMovie.voteAverage
                )
        );

        TextView synopsis = (TextView) detailView.findViewById(R.id.synopsisTextView);
        synopsis.setText(mMovie.synopsis);

        if (mMovie.runTime != null) {
            TextView runTime = (TextView) detailView.findViewById(R.id.runTimeTextView);
            runTime.setText(String.format(mContext.getString(R.string.runtime),mMovie.runTime));
        }


        ImageView poster = (ImageView) detailView.findViewById(R.id.posterImageView);
        Picasso.with(detailView.getContext()).load(Constants.THUMBNAIL_URL + mMovie.posterPath).into(poster);

        return detailView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return MOVIE_DETAILS;
        } else if (position > 0 && position <= trailerCount && trailerCount > 0){
            return MOVIE_TRAILERS;
        } else if (position > 0 && position <= trailerCount + reviewCount && reviewCount > 0) {
            return MOVIE_REVIEWS;
        }
        return MOVIE_DETAILS;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
