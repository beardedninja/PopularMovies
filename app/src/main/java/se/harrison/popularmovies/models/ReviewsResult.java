package se.harrison.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by alex on 03/11/15.
 */
public class ReviewsResult {
    @SerializedName("page")
    public Integer page;

    @SerializedName("total_pages")
    public Integer total_pages;

    @SerializedName("total_results")
    public Integer total_results;

    @SerializedName("results")
    public ArrayList<Review> results;
}