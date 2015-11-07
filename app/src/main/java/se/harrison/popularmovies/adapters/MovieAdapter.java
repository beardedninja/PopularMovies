package se.harrison.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import se.harrison.popularmovies.R;
import se.harrison.popularmovies.activities.DetailActivity;
import se.harrison.popularmovies.activities.MainActivity;
import se.harrison.popularmovies.models.Movie;
import se.harrison.popularmovies.utilities.Constants;

/**
 * Created by alex on 15/09/15.
 */
public class MovieAdapter extends BaseAdapter {

    private ArrayList<Movie> mMovies = new ArrayList<>();
    private Context mContext;

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        mContext = context;

        for (Movie movie : movies) {
            mMovies.add(movie);
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
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Movie getItem(int position) {
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mMovies.get(position).id;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View posterView;
        if (convertView != null) {
            posterView = convertView;
        } else {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            posterView = inflater.inflate(R.layout.poster_thumbnail, null);
        }

        Movie movie = getItem(position);

        TextView titleView = (TextView) posterView.findViewById(R.id.posterTitleView);
        titleView.setText(movie.title);

        ImageView imageView = (ImageView) posterView.findViewById(R.id.posterImageView);

        Picasso.with(mContext)
                .load(Constants.THUMBNAIL_URL + movie.posterPath)
                .placeholder(R.drawable.poster_empty)
                .into(imageView);

        return posterView;
    }

    @Override
    public boolean isEmpty() {
        return mMovies.isEmpty();
    }

    public void setupMovies(ArrayList<Movie> movies) {
        mMovies.clear();
        addMovies(movies);
    }

    public void addMovies(ArrayList<Movie> movies) {
        for (Movie movie : movies) {
            mMovies.add(movie);
        }
        notifyDataSetChanged();
    }
}
