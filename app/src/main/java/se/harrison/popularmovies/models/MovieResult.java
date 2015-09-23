package se.harrison.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by alex on 14/09/15.
 */
public class MovieResult implements Parcelable {
    public int page;
    public ArrayList<Movie> results;

    public ArrayList<Movie> getResults() {
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.page);
        dest.writeList(this.results);
    }

    protected MovieResult(Parcel in) {
        this.page = in.readInt();
        this.results = new ArrayList<Movie>();
        in.readList(this.results, ArrayList.class.getClassLoader());
    }

    public static final Parcelable.Creator<MovieResult> CREATOR = new Parcelable.Creator<MovieResult>() {
        public MovieResult createFromParcel(Parcel source) {
            return new MovieResult(source);
        }

        public MovieResult[] newArray(int size) {
            return new MovieResult[size];
        }
    };
}
