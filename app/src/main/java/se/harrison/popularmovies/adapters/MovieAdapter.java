package se.harrison.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import se.harrison.popularmovies.activities.DetailActivity;
import se.harrison.popularmovies.models.Movie;

/**
 * Created by alex on 15/09/15.
 */
public class MovieAdapter extends BaseAdapter {

    private ArrayList<Movie> mMovies = new ArrayList<>();
    private Context mContext;

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        mContext = context;

        for (Movie movie: movies) {
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
        ImageView imageView;
        if (convertView != null) {
            imageView = (ImageView) convertView;
        } else {
            imageView = new ImageView(mContext);
            GridView.LayoutParams param = new GridView.LayoutParams(
                    GridView.LayoutParams.MATCH_PARENT,
                    GridView.LayoutParams.MATCH_PARENT
            );

            imageView.setLayoutParams(param);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);
            imageView.setBackgroundColor(Color.BLACK);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra("movie", getItem(position));
                    mContext.startActivity(intent);
                }
            });
        }

        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/" + getItem(position).posterPath).into(imageView);

        return imageView;
    }

    @Override
    public boolean isEmpty() {
        return mMovies.isEmpty();
    }

    public void addMovies(ArrayList<Movie> movies) {
        mMovies.clear();
        for (Movie movie: movies) {
            mMovies.add(movie);
        }
        notifyDataSetChanged();
    }
}
