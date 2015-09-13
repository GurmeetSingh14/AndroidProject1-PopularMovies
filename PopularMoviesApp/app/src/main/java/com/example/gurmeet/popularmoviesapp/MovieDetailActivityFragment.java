package com.example.gurmeet.popularmoviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gurmeet.popularmoviesapp.data.MovieAppContract;
import com.example.gurmeet.popularmoviesapp.data.MovieDbHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayList<MovieTrailersObject> mMovieTrailerObject = null;
    private ArrayList<MovieReviewsObject> mMovieReviewObject = null;

    private MovieTrailerAdapter mMovieTrailerAdapter = null;
    private MovieReviewAdapter mMovieReviewAdapter = null;
    private int m_nMovieId;

    private static final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();
    private static final String SHARE_INTENT_STRING_BASE = "Checkout the %s trailer at: " +
            " https://www.youtube.com/watch?v=%s #PopularMovieApp";

    private ImageButton m_favButton;
    private static boolean m_bFavoriteEnabled = false;

    private String m_strMovieTitle;
    private String mPopularMovieStr;
    private String m_strMoviePosterFullPath;
    private String m_strMovieUserRating;
    private String m_strMovieReleaseDate;
    private String m_strMoviePlot;

    private String m_strTrailerName, m_strTrailerSource, m_strTrailerType;
    private String m_strReviewAuthor, m_strReviewContent, m_strReviewURL;

    private static MovieDbHelper m_FavoriteMovieDbHelper = null;

    private MovieTrailersReviewsObject mMovieTrailersReviewsObject = null;

    private ShareActionProvider mShareActionProvider = null;
    static final String DETAIL_URI = "URI";

    public MovieDetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Bundle arguments = getArguments();
        Intent movieIntent = null;
        if (arguments != null) {
            movieIntent = arguments.getParcelable(DETAIL_URI);
        }

        if (movieIntent != null) {

            m_strMovieTitle = movieIntent.getStringExtra("movieTitle");
            m_strMoviePosterFullPath = movieIntent.getStringExtra("moviePosterFullPath");
            m_strMovieUserRating = movieIntent.getStringExtra("movieUserRating");
            m_strMovieReleaseDate = movieIntent.getStringExtra("movieReleaseDate");
            m_strMoviePlot = movieIntent.getStringExtra("moviePlot");
            m_nMovieId = movieIntent.getIntExtra("movieId", 0);

            ImageView moviePosterImageView = (ImageView) rootView.findViewById(R.id.imageView_moviePoster);
            Picasso.with(getActivity()).load(m_strMoviePosterFullPath)
                    .error(R.drawable.placeholder_poster_small)
                    .into(moviePosterImageView);

            TextView movieTitleTextView = (TextView) rootView.findViewById(R.id.textView_movieTitle);
            movieTitleTextView.setText(m_strMovieTitle);

            TextView movieUserRatingTextView = (TextView) rootView.findViewById(R.id.textView_movieRating);
            movieUserRatingTextView.setText(m_strMovieUserRating);

            TextView movieReleaseDateTextView = (TextView) rootView.findViewById(R.id.textView_movieReleaseDate);
            movieReleaseDateTextView.setText(m_strMovieReleaseDate);

            TextView moviePlotTextView = (TextView) rootView.findViewById(R.id.textView_moviePlot);
            moviePlotTextView.setText(m_strMoviePlot);

            mMovieTrailerObject = new ArrayList<MovieTrailersObject>();
            mMovieReviewObject = new ArrayList<MovieReviewsObject>();

            FetchMovieTrailersAndReviews movie = new FetchMovieTrailersAndReviews();
            movie.execute(m_nMovieId);

            //Set adapter for MovieTrailerListView
            ListView mMovieTrailerListView = (ListView) rootView.findViewById(R.id.listview_movie_trailers);
            mMovieTrailerAdapter = new MovieTrailerAdapter(getActivity(), R.layout.movie_trailer_item);
            mMovieTrailerListView.setAdapter(mMovieTrailerAdapter);

            mMovieTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String youtubeURL = "https://www.youtube.com/watch?v=" + mMovieTrailerObject.get(position).m_strTrailerSource;
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL));
                    startActivity(i);
                }
            });

            if (m_FavoriteMovieDbHelper == null) {
                m_FavoriteMovieDbHelper = new MovieDbHelper(getActivity());
            }

            //Set adapter for MovieReviewsListView
            ListView mMovieReviewListView = (ListView) rootView.findViewById(R.id.listview_movie_reviews);
            mMovieReviewAdapter = new MovieReviewAdapter(getActivity(), R.layout.movie_review_item);
            mMovieReviewListView.setAdapter(mMovieReviewAdapter);

            mMovieReviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String reviewURL = mMovieReviewObject.get(position).m_strReviewURL;
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(reviewURL));
                    startActivity(i);
                }
            });

            m_bFavoriteEnabled = isFavoriteMovie();
            setFavoriteIcon(rootView);
        }
        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Fix of Scroll Issue when ListView is present in the layout
        //For More info, checkout below link:
        //http://stackoverflow.com/questions/4119441/how-to-scroll-to-top-of-long-scrollview-layout
        ScrollView detail_scrollview = (ScrollView) view.findViewById(R.id.scrollView);
        detail_scrollview.smoothScrollTo(0, 0);
    }

    private void setFavoriteIcon(View view) {
        m_favButton = (ImageButton) view.findViewById(R.id.imageButton_favourite);

        if (m_bFavoriteEnabled) {
            m_favButton.setImageResource(R.drawable.fav_enabled);
        } else {
            m_favButton.setImageResource(R.drawable.fav_normal);
        }

        m_favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_bFavoriteEnabled) {
                    m_bFavoriteEnabled = false;
                    deleteFavoriteMovieDetailsFromDB();
                    m_favButton.setImageResource(R.drawable.fav_normal);
                } else {
                    m_bFavoriteEnabled = true;
                    insertFavoriteMovieDetailsToDB();
                    m_favButton.setImageResource(R.drawable.fav_enabled);
                }
            }
        });
    }

    private void insertFavoriteMovieDetailsToDB() {

        ContentValues favorite_movie_values = getFavoriteMovieValues();
        ContentValues favorite_movie_trailer_values = getFavoriteMovieTrailerValues();
        ContentValues favorite_movie_review_values = getFavoriteMovieReviewsValues();

        //Insert Favorite Movie details, trailers and review information to database
        if (favorite_movie_values != null) {
            Uri insertedMovieDetailsUri = getActivity().getContentResolver().insert(
                    MovieAppContract.MovieDetailsEntry.CONTENT_URI,
                    favorite_movie_values);
            Log.e("GS_APP_PROVIDER", "New Row insertedMovieDetailsUri Inserted " + insertedMovieDetailsUri);
        }

        if (favorite_movie_trailer_values != null) {

            Uri insertedMovieTrailerUri = getActivity().getContentResolver().insert(
                    MovieAppContract.MovieTrailerEntry.CONTENT_URI,
                    favorite_movie_trailer_values
            );
            Log.e("GS_APP_PROVIDER", "New Row insertedMovieTrailerUri Inserted " + insertedMovieTrailerUri);
        }

        if (favorite_movie_review_values != null) {
           /* Uri insertedMovieReviewUri = getActivity().getContentResolver().insert(
                    MovieAppContract.MovieReviewsEntry.CONTENT_URI,
                    favorite_movie_review_values
            );*/
            Log.e("GS_APP_REVIEW","Review :" + favorite_movie_review_values.getAsString("trailer_source"));
            Log.e("GS_APP_REVIEW","Review :" + favorite_movie_review_values.getAsString("trailer_name"));
            Log.e("GS_APP_REVIEW","Review :" + favorite_movie_review_values.getAsString("trailer_type"));
            Log.e("GS_APP_REVIEW","Review :" + favorite_movie_review_values.getAsString("movie_id"));
            //Log.e("GS_APP_PROVIDER", "New Row insertedMovieReviewUri Inserted: " + insertedMovieReviewUri);
        }


    }


    private void deleteFavoriteMovieDetailsFromDB() {

        int detailRowCount = getActivity().getContentResolver().delete(MovieAppContract.MovieDetailsEntry.CONTENT_URI,
                MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_ID + "= ?",
                new String[]{Integer.toString(m_nMovieId)});
        Log.e("GS_APP_PROVIDER", "Movie Detail Row Deletion Count: " + Integer.toString(detailRowCount));
        int trailerRowCount = getActivity().getContentResolver().delete(MovieAppContract.MovieTrailerEntry.CONTENT_URI,
                MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_ID + "= ?",
                new String[]{Integer.toString(m_nMovieId)});
        Log.e("GS_APP_PROVIDER", "Movie Trailers Row Deletion Count: " + Integer.toString(trailerRowCount));

        int reviewRowCount = getActivity().getContentResolver().delete(MovieAppContract.MovieReviewsEntry.CONTENT_URI,
                MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_ID + "= ?",
                new String[]{Integer.toString(m_nMovieId)});
        Log.e("GS_APP_PROVIDER", "Movie Review Row Deletion Count: " + Integer.toString(reviewRowCount));


    }


    private boolean isFavoriteMovie() {
        boolean bFavoriteMovie = false;

        SQLiteDatabase db = m_FavoriteMovieDbHelper.getReadableDatabase();
        String sqliteQuery = "SELECT * FROM " + MovieAppContract.MovieDetailsEntry.TABLE_NAME +
                " WHERE " + MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_ID + " = "
                + Integer.toString(m_nMovieId);

        Cursor cursor = db.rawQuery(sqliteQuery, null);

        if (cursor.getCount() > 0) {
            bFavoriteMovie = true;
            Log.e("GS_APP_DB", "This movie is my favorite !!");
        }

        cursor.close();
        db.close();

        return bFavoriteMovie;
    }

    private ContentValues getFavoriteMovieValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_ID, m_nMovieId);
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_PLOT, m_strMoviePlot);
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_POSTER_PATH, m_strMoviePosterFullPath);
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_RELEASE_DATE, m_strMovieReleaseDate);
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_TITLE, m_strMovieTitle);
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_USER_RATING, m_strMovieUserRating);

        return contentValues;
    }

    private ContentValues getFavoriteMovieTrailerValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieAppContract.MovieTrailerEntry.COLUMN_MOVIE_KEY, m_nMovieId);
        contentValues.put(MovieAppContract.MovieTrailerEntry.COLUMN_TRAILER_NAME, m_strTrailerName);
        contentValues.put(MovieAppContract.MovieTrailerEntry.COLUMN_TRAILER_TYPE, m_strTrailerType);
        contentValues.put(MovieAppContract.MovieTrailerEntry.COLUMN_TRAILER_SOURCE, m_strTrailerSource);

        return contentValues;
    }

    private ContentValues getFavoriteMovieReviewsValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieAppContract.MovieReviewsEntry.COLUMN_MOVIE_KEY, m_nMovieId);
        contentValues.put(MovieAppContract.MovieReviewsEntry.COLUMN_REVIEW_AUTHOR, m_strReviewAuthor);
        contentValues.put(MovieAppContract.MovieReviewsEntry.COLUMN_REVIEW_CONTENT, m_strReviewContent);
        contentValues.put(MovieAppContract.MovieReviewsEntry.COLUMN_REVIEW_URL, m_strReviewURL);
        return contentValues;
    }

    private Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        if (mMovieTrailerObject != null && mMovieTrailerObject.size() > 0) {
            mPopularMovieStr = String.format(SHARE_INTENT_STRING_BASE, m_strMovieTitle
                    , (mMovieTrailerObject.get(0)).m_strTrailerSource);
        }

        shareIntent.putExtra(Intent.EXTRA_TEXT, mPopularMovieStr);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        } else {
            Log.e(LOG_TAG, "NULL share action provider");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    public class MovieTrailersReviewsObject {
        @SuppressWarnings("CanBeFinal")
        final
        ArrayList<MovieTrailersObject> m_movieTrailerObject;
        final ArrayList<MovieReviewsObject> m_movieReviewsObjet;

        MovieTrailersReviewsObject(ArrayList<MovieTrailersObject> trailers, ArrayList<MovieReviewsObject> reviews) {
            m_movieTrailerObject = trailers;
            m_movieReviewsObjet = reviews;
        }
    }

    public class MovieTrailersObject implements Parcelable {

        String m_strTrailerName = null;
        String m_strTrailerSource = null;
        String m_strTrailerType = null;

        MovieTrailersObject(String trailerName, String trailerSource, String trailerType) {
            m_strTrailerName = trailerName;
            m_strTrailerSource = trailerSource;
            m_strTrailerType = trailerType;
        }

        private MovieTrailersObject(Parcel parcel) {
            m_strTrailerName = parcel.readString();
            m_strTrailerSource = parcel.readString();
            m_strTrailerType = parcel.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(m_strTrailerName);
            dest.writeString(m_strTrailerSource);
            dest.writeString(m_strTrailerType);
        }

        public final Parcelable.Creator<MovieTrailersObject> CREATOR = new Parcelable.ClassLoaderCreator<MovieTrailersObject>() {

            @Override
            public MovieTrailersObject createFromParcel(Parcel source) {
                return new MovieTrailersObject(source);
            }

            @Override
            public MovieTrailersObject[] newArray(int size) {
                return new MovieTrailersObject[size];
            }

            @Override
            public MovieTrailersObject createFromParcel(Parcel source, ClassLoader loader) {
                return null;
            }
        };
    }


    //Movie Adapter class for GridView
    public class MovieTrailerAdapter extends ArrayAdapter<MovieTrailersObject> {
        private final Context mContext;
        private int mImageCount;

        public MovieTrailerAdapter(Context context, int resource) {
            super(context, resource);
            mContext = context;
        }

        @Override
        public int getCount() {
            int count = 0;
            if (mMovieTrailerObject != null) {
                count = mMovieTrailerObject.size();
            }
            return count;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.movie_trailer_item, parent, false);
            MovieTrailersObject tempMovieTrailerObject = mMovieTrailerObject.get(position);
            TextView trailerName = (TextView) row.findViewById(R.id.textview_movie_trailer_name);
            trailerName.setText(tempMovieTrailerObject.m_strTrailerName);

            TextView trailerType = (TextView) row.findViewById(R.id.textview_movie_trailer_type);
            trailerType.setText(tempMovieTrailerObject.m_strTrailerType);

            return row;
        }
    }


    //Movie Adapter class for GridView
    public class MovieReviewAdapter extends ArrayAdapter<MovieReviewsObject> {
        private final Context mContext;
        private int mImageCount;

        public MovieReviewAdapter(Context context, int resource) {
            super(context, resource);
            mContext = context;
        }

        @Override
        public int getCount() {
            int count = 0;
            if (mMovieReviewObject != null) {
                count = mMovieReviewObject.size();
            }
            return count;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.movie_review_item, parent, false);

            MovieReviewsObject tempMovieReviewsObject = mMovieReviewObject.get(position);

            TextView reviewContent = (TextView) row.findViewById(R.id.textViewReview);
            reviewContent.setText(tempMovieReviewsObject.m_strReviewContent);

            TextView reviewAuthor = (TextView) row.findViewById(R.id.textViewAuthor);
            reviewAuthor.setText("Reviewed By: " + tempMovieReviewsObject.m_strReviewAuthor);

            TextView reviewURL = (TextView) row.findViewById(R.id.textViewURL);
            reviewURL.setText("Review URL: " + tempMovieReviewsObject.m_strReviewURL);
            return row;
        }
    }


    public class MovieReviewsObject implements Parcelable {

        String m_strReviewAuthor = null;
        String m_strReviewContent = null;
        String m_strReviewURL = null;

        MovieReviewsObject(String reviewAuthor, String reviewContent, String reviewURL) {
            m_strReviewAuthor = reviewAuthor;
            m_strReviewContent = reviewContent;
            m_strReviewURL = reviewURL;
        }

        private MovieReviewsObject(Parcel parcel) {
            m_strReviewAuthor = parcel.readString();
            m_strReviewContent = parcel.readString();
            m_strReviewURL = parcel.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(m_strReviewAuthor);
            dest.writeString(m_strReviewContent);
            dest.writeString(m_strReviewURL);
        }

        public final Parcelable.Creator<MovieReviewsObject> CREATOR = new Parcelable.ClassLoaderCreator<MovieReviewsObject>() {

            @Override
            public MovieReviewsObject createFromParcel(Parcel source) {
                return new MovieReviewsObject(source);
            }

            @Override
            public MovieReviewsObject[] newArray(int size) {
                return new MovieReviewsObject[size];
            }

            @Override
            public MovieReviewsObject createFromParcel(Parcel source, ClassLoader loader) {
                return null;
            }
        };
    }

    private class FetchMovieTrailersAndReviews extends AsyncTask {

        @Override
        protected MovieTrailersReviewsObject doInBackground(Object[] params) {

            String movieDBJSONString = null;
            HttpURLConnection urlConnection;
            BufferedReader reader = null;
            String LOG_TAG = FetchMovieTrailersAndReviews.class.getSimpleName();

            String strMovieId = (params[0]).toString();

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String MOVIE_URL = BASE_URL + strMovieId;
                String APIKEY_PARAM = "api_key";
                String APPEND_PARAM = "append_to_response";

                String api_Key = "eff5e06e071bf6e65d367677e3368ea9";
                String appendTrailersReviews = "trailers,reviews";


                Uri builtURI = Uri.parse(MOVIE_URL).buildUpon()
                        .appendQueryParameter(APIKEY_PARAM, api_Key)
                        .appendQueryParameter(APPEND_PARAM, appendTrailersReviews)
                        .build();

                URL url = new URL(builtURI.toString());

                Log.v(LOG_TAG, "BuildURL:" + url);
                if (isNetworkAvailable()) {

                    //Create the request to TheMovieDB and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    //Read the input stream into a string
                    InputStream movieInputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (movieInputStream == null) {
                        Log.v(LOG_TAG, "The Movie DB returned NULL");
                        return null;
                    }

                    Log.v(LOG_TAG, "The Movie DB did not returned NULL");

                    reader = new BufferedReader(new InputStreamReader(movieInputStream));

                    String line;

                    while ((line = reader.readLine()) != null) {
                        Log.v(LOG_TAG, "Line: " + line);
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    movieDBJSONString = buffer.toString();
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing reader stream ", e);
                }
            }

            try {
                return getMoviesTrailersReviewsfromJSON(movieDBJSONString);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result != null) {
                mMovieTrailersReviewsObject = (MovieTrailersReviewsObject) result;
                for (int i = 0; i < mMovieTrailersReviewsObject.m_movieTrailerObject.size(); ++i) {
                    mMovieTrailerAdapter.add(mMovieTrailerObject.get(i));
                }

                for (int i = 0; i < mMovieTrailersReviewsObject.m_movieReviewsObjet.size(); ++i) {
                    mMovieReviewAdapter.add(mMovieReviewObject.get(i));
                }

                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareTrailerIntent());
                } else {
                    Log.e(LOG_TAG, "Share Action Provider is NULL");
                }
            }
        }
    }


    //This method checks if network connection is available or not
    private boolean isNetworkAvailable() {
        ConnectivityManager connectionManager = (ConnectivityManager) getActivity().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo ntwkInfo = connectionManager.getActiveNetworkInfo();
        return ((ntwkInfo != null) && ntwkInfo.isConnected());
    }


    private MovieTrailersReviewsObject getMoviesTrailersReviewsfromJSON(String movieDBJSONString) throws JSONException {
        final String TMDB_TRAILERS = "trailers";
        final String TMDB_YOUTUBE = "youtube";

        final String TMDB_REVIEWS = "reviews";
        final String TMDB_RESULTS = "results";

        JSONObject movieDBJSONObject = new JSONObject(movieDBJSONString);

        JSONObject movieDBTrailersObject = movieDBJSONObject.getJSONObject(TMDB_TRAILERS);
        JSONArray movieDBYouTubeTrailersArray = movieDBTrailersObject.getJSONArray(TMDB_YOUTUBE);

        int movieYouTubeTrailersArrayLength = movieDBYouTubeTrailersArray.length();

        for (int i = 0; i < movieYouTubeTrailersArrayLength; ++i) {
            JSONObject movieYouTubeTrailer = movieDBYouTubeTrailersArray.getJSONObject(i);
            m_strTrailerName = movieYouTubeTrailer.getString("name");
            m_strTrailerSource = movieYouTubeTrailer.getString("source");
            m_strTrailerType = movieYouTubeTrailer.getString("type");
            mMovieTrailerObject.add(new MovieTrailersObject(m_strTrailerName, m_strTrailerSource,
                    m_strTrailerType));
        }

        JSONObject movieDBReviewssObject = movieDBJSONObject.getJSONObject(TMDB_REVIEWS);
        JSONArray movieDBReviewResultsArray = movieDBReviewssObject.getJSONArray(TMDB_RESULTS);

        int movieReviewsArrayLength = movieDBReviewResultsArray.length();

        for (int i = 0; i < movieReviewsArrayLength; ++i) {
            JSONObject movieReview = movieDBReviewResultsArray.getJSONObject(i);
            m_strReviewAuthor = movieReview.getString("author");
            m_strReviewContent = movieReview.getString("content");
            m_strReviewURL = movieReview.getString("url");
            mMovieReviewObject.add(new MovieReviewsObject(m_strReviewAuthor, m_strReviewContent,
                    m_strReviewURL));
        }
        mMovieTrailersReviewsObject = new MovieTrailersReviewsObject(mMovieTrailerObject, mMovieReviewObject);

        return mMovieTrailersReviewsObject;
    }
}
