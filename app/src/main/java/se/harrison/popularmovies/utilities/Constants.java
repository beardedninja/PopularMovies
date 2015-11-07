package se.harrison.popularmovies.utilities;

/**
 * Created by alex on 23/09/15.
 */
public class Constants {
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String THUMBNAIL_URL = BASE_IMAGE_URL + "w185/";

    public static final String SORTING_POPULARITY = "Most popular";
    public static final String SORTING_HIGHEST_RATED = "Highest rated";
    public static final String FETCH_FAVORITES="Your favorites";

    public static final String API_SORTING_POPULARITY = "popularity.desc";
    public static final String API_SORTING_HIGHEST_RATED = "vote_count.desc";

    public static final String LOG_TAG = "PopularMovies";
    public static final String DISCOVER_MOVIE_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
    public static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String SORTING_PARAM = "sort_by";
    public static final String API_KEY_PARAM = "api_key";
    public static final String COUNT_FILTER = "vote_count.gte";
    public static final String PAGE_PARAM = "page";

    public static final String YOUTUBE_URL = "http://www.youtube.com/watch";
    public static final String VIDEO_PARAM = "v";
}
