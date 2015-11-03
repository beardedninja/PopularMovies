package se.harrison.popularmovies.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 03/11/15.
 */
public class Trailer {
    @SerializedName("name")
    public String name;

    @SerializedName("size")
    public String size;

    @SerializedName("source")
    public String source;

    @SerializedName("type")
    public String type;
}