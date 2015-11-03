package se.harrison.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by alex on 14/09/15.
 */
public class Movie implements Parcelable {
    public int id;

    @SerializedName("poster_path")
    public String posterPath;

    @SerializedName("backdrop_path")
    public String backdropPath;

    @SerializedName("release_date")
    public Date releaseDate;

    @SerializedName("vote_average")
    public double voteAverage;

    @SerializedName("overview")
    public String synopsis;

    @SerializedName("title")
    public String title;

    @SerializedName("trailers")
    public HashMap<String, ArrayList<Trailer>> trailers;

    @SerializedName("reviews")
    public ReviewsResult review_result;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.posterPath);
        dest.writeString(this.backdropPath);
        dest.writeLong(releaseDate != null ? releaseDate.getTime() : -1);
        dest.writeDouble(this.voteAverage);
        dest.writeString(this.synopsis);
        dest.writeString(this.title);
    }

    public Movie() {
    }

    protected Movie(Parcel in) {
        this.id = in.readInt();
        this.posterPath = in.readString();
        this.backdropPath = in.readString();
        long tmpReleaseDate = in.readLong();
        this.releaseDate = tmpReleaseDate == -1 ? null : new Date(tmpReleaseDate);
        this.voteAverage = in.readDouble();
        this.synopsis = in.readString();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getReleaseYear() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        return df.format(releaseDate);
    }

}