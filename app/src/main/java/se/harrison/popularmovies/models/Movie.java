package se.harrison.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

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

/*
{
  "adult": false,
  "backdrop_path": "\/tbhdm8UJAb4ViCTsulYFL3lxMCd.jpg",
  "genre_ids": [
    53,
    28,
    12
  ],
  "id": 76341,
  "original_language": "en",
  "original_title": "Mad Max: Fury Road",
  "overview": "An apocalyptic story set in the furthest reaches of our planet, in a stark desert landscape where humanity is broken, and most everyone is crazed fighting for the necessities of life. Within this world exist two rebels on the run who just might be able to restore order. There's Max, a man of action and a man of few words, who seeks peace of mind following the loss of his wife and child in the aftermath of the chaos. And Furiosa, a woman of action and a woman who believes her path to survival may be achieved if she can make it across the desert back to her childhood homeland.",
  "release_date": "2015-05-15",
  "poster_path": "\/kqjL17yufvn9OVLyXYpvtyrFfak.jpg",
  "popularity": 51.988475,
  "title": "Mad Max: Fury Road",
  "video": false,
  "vote_average": 7.6,
  "vote_count": 2214
}
 */