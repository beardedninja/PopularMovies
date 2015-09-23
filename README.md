# PopularMovies

A movie trailer android application that queries TheMovieDatabase's (https://www.themoviedb.org/)
API for the most popular or highest rated movies currently available. Movies posters
are shown in a GridView (and loaded using the Picaso image library) and allows the
user to change sorting or select a move to show a detail view which shows
information on a particular movie.

## Notable features:

State (sort order, vote count filter, position, current results) is stored both
using SharedPreferences and SavedInstanceState.

Models using Parcelable interface passed between fragment/activities.

Gson Json library used to avoid manual json parsing and structure traversal using
JSONObject etc.

Loading dialog with loading spinner when sort order is changed or on application
start.

## Installing

Obtain an API key from TheMovieDatabase (https://www.themoviedb.org/documentation/api),
copy/rename file secrets.xml.sample to secrets.xml in the values folder and replace
"ENTER_YOUR_API_KEY_HERE" with your key.

Build apk with Android Studio.



