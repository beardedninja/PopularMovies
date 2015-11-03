package se.harrison.popularmovies.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 03/11/15.
 */
public class Review {
    @SerializedName("id")
    public String id;

    @SerializedName("author")
    public String author;

    @SerializedName("content")
    public String content;

    @SerializedName("url")
    public String url;
}