package com.example.gurmeet.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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
public class MainActivityFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayList<MovieDetailsObject> mMovieDetailsArrayList = null;
    private PopularMovieAdapter mMovieAdapter = null;
    private String mSortQuery = null;

    private static final int MENU_SELECTED_POPULARITY = 1;
    private static final int MENU_SELECTED_RATING = 2;
    private static final int MENU_SELECTED_FAVORITE = 3;
    private int mMenuSelection = 0;

    private static MovieDbHelper m_FavoriteMovieDbHelper = null;
    private boolean mTowPaneLayout = false;

    private static final String SORT_QUERY = "sort_query";
    private static final String MENU_SELECTION = "menu_selection";
    private static final String MOVIE_DETAILS_LIST = "MovieDetailObjectsArrayList";
    private static final String MOVIEDETAIL_FRAGMENT_TAG = "DETAILTAG";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_main_fragment, container, false);

        //Initialize GridView member variable
        GridView moviePostersGrid = (GridView) rootView.findViewById(R.id.gridView_home);

        //Initialize MovieAdapter and attach it with the GridView
        mMovieAdapter = new PopularMovieAdapter(getActivity(), R.layout.movie_item);
        moviePostersGrid.setAdapter(mMovieAdapter);

        //Set Click listener for GridView items
        moviePostersGrid.setOnItemClickListener(this);

        if (m_FavoriteMovieDbHelper == null) {
            m_FavoriteMovieDbHelper = new MovieDbHelper(getActivity());
        }

        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get the sort query type from savedInstanceState (if available)
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(SORT_QUERY))) {
            mSortQuery = savedInstanceState.getString(SORT_QUERY);
            mMenuSelection = Integer.parseInt(savedInstanceState.getString(MENU_SELECTION));
        } else {
            mSortQuery = "popularity.desc";
        }

        //Get the MovieDetailsArrayList from the savedInstanceState (if available)
        if ((savedInstanceState != null) && savedInstanceState.containsKey(MOVIE_DETAILS_LIST)) {
            mMovieDetailsArrayList = savedInstanceState.getParcelableArrayList(MOVIE_DETAILS_LIST);
            Log.e("GS_APP_DB", "Retrieved Saved Movies 1 :" + mMovieDetailsArrayList.size());
        } else {
            mMovieDetailsArrayList = new ArrayList<MovieDetailsObject>();
            Log.e("GS_APP_DB", "Retrieved Saved Movies 2 :" + mMovieDetailsArrayList.size());


            if (mMenuSelection == MENU_SELECTED_FAVORITE) {
                //Fetch Favorite Movie Details from the Database
                fetchFavoriteMoviesFromDB();
            } else {
                //Fetch movie details and update the GridView with movie posters
                updateMovieGridView();
            }
        }
        //Check to see if it is two pane layout
        MainActivity main_activity = (MainActivity) getActivity();
        if (main_activity.mTwoPaneLayout) {
            mTowPaneLayout = true;
        }
    }

    @Override
    public void onStart() {
        if (mMenuSelection == MENU_SELECTED_FAVORITE) {
            //Fetch Favorite Movie Details from the Database
            fetchFavoriteMoviesFromDB();
        } else {
            //Fetch movie details and update the GridView with movie posters
            updateMovieGridView();
        }
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SORT_QUERY, mSortQuery);
        outState.putString(MENU_SELECTION, Integer.toString(mMenuSelection));
        outState.putParcelableArrayList(MOVIE_DETAILS_LIST, mMovieDetailsArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    private void updateMovieGridView() {
        //Check if network is available and fetch the movie details from TMDB
        if (isNetworkAvailable()) {
            FetchMovieDetails movieDetails = new FetchMovieDetails();
            movieDetails.execute();
        } else {
            //If network is not available inform the user to restroe the network connection
            Toast toastNoConnection = Toast.makeText(getActivity(), "Please restore the network " +
                    "connection and press \"Refresh\" menu item.", Toast.LENGTH_LONG);
            toastNoConnection.show();
        }
    }

    //For favorite movies, fetch the details from database
    private void fetchFavoriteMoviesFromDB() {
        FetchFavoriteMovieDetailsFromDB movieDetailsFromDB = new FetchFavoriteMovieDetailsFromDB();
        movieDetailsFromDB.execute();
    }

    //This method checks if network connection is available or not
    private boolean isNetworkAvailable() {
        ConnectivityManager connectionManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ntwkInfo = connectionManager.getActiveNetworkInfo();
        return ((ntwkInfo != null) && ntwkInfo.isConnected());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            if (mMenuSelection == MENU_SELECTED_FAVORITE) {
                fetchFavoriteMoviesFromDB();
            } else {
                updateMovieGridView();
            }
            return true;
        } else if (id == R.id.action_sort_by_popularity) {
            mMenuSelection = MENU_SELECTED_POPULARITY;
            mSortQuery = "popularity.desc";
            updateMovieGridView();
            return true;
        } else if (id == R.id.action_sort_by_user_rating) {
            mMenuSelection = MENU_SELECTED_RATING;
            mSortQuery = "vote_average.desc";
            updateMovieGridView();
            return true;
        } else if (id == R.id.action_my_favorite_movies) {
            mMenuSelection = MENU_SELECTED_FAVORITE;
            fetchFavoriteMoviesFromDB();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        displayMovieDetailsUI(position);
    }

    private void displayMovieDetailsUI(int position) {
        Intent movieIntent = new Intent(getActivity(), MovieDetailActivity.class);
        if (mMovieDetailsArrayList.size() >= position + 1) {
            MovieDetailsObject movieDetails = mMovieDetailsArrayList.get(position);
            movieIntent.putExtra("movieTitle", movieDetails.m_strMovieTitle);
            movieIntent.putExtra("moviePosterFullPath", movieDetails.m_strMoviePosterFullPath);
            movieIntent.putExtra("movieUserRating", movieDetails.m_strMovieUserRating);
            movieIntent.putExtra("movieReleaseDate", movieDetails.m_strMovieReleaseDate);
            movieIntent.putExtra("moviePlot", movieDetails.m_strMoviePlot);
            movieIntent.putExtra("movieId", movieDetails.m_nMovieId);
        }
        if (mTowPaneLayout) {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailActivityFragment.DETAIL_URI, movieIntent);

            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(args);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, MOVIEDETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            startActivity(movieIntent);
        }
    }

    //MovieDetailsObject holds the movie details like MoviePosterFullPath,
    //MovieTitle, MoviePlot, UserRating, ReleaseData etc.
    @SuppressWarnings("CanBeFinal")
    public class MovieDetailsObject implements Parcelable {

        final String m_strMoviePosterFullPath;
        final String m_strMovieTitle;
        final String m_strMoviePlot;
        final String m_strMovieUserRating;
        String m_strMovieReleaseDate = null;
        final int m_nMovieId;


        MovieDetailsObject(String moviePosterFullPath, String movieTitle, String moviePlot,
                           String movieUserRating, String movieReleaseDate, int movieId) {
            m_strMoviePosterFullPath = moviePosterFullPath;
            m_strMovieTitle = movieTitle;
            m_strMoviePlot = moviePlot;
            m_strMovieUserRating = movieUserRating;
            m_strMovieReleaseDate = movieReleaseDate;
            m_nMovieId = movieId;
        }

        private MovieDetailsObject(Parcel parcel) {
            m_strMoviePosterFullPath = parcel.readString();
            m_strMovieTitle = parcel.readString();
            m_strMoviePlot = parcel.readString();
            m_strMovieUserRating = parcel.readString();
            m_strMovieReleaseDate = parcel.readString();
            m_nMovieId = parcel.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(m_strMoviePosterFullPath);
            dest.writeString(m_strMovieTitle);
            dest.writeString(m_strMoviePlot);
            dest.writeString(m_strMovieUserRating);
            dest.writeString(m_strMovieReleaseDate);
            dest.writeInt(m_nMovieId);
        }

        public final Parcelable.Creator<MovieDetailsObject> CREATOR = new Parcelable.ClassLoaderCreator<MovieDetailsObject>() {

            @Override
            public MovieDetailsObject createFromParcel(Parcel source) {
                return new MovieDetailsObject(source);
            }

            @Override
            public MovieDetailsObject[] newArray(int size) {
                return new MovieDetailsObject[size];
            }

            @Override
            public MovieDetailsObject createFromParcel(Parcel source, ClassLoader loader) {
                return null;
            }
        };
    }

    //Movie Adapter class for GridView
    public class PopularMovieAdapter extends ArrayAdapter<MovieDetailsObject> {
        private final Context mContext;
        private int mImageCount;

        public PopularMovieAdapter(Context context, int resource) {
            super(context, resource);
            mContext = context;
        }

        @Override
        public int getCount() {
            int count = 0;
            if (mMovieDetailsArrayList != null) {
                count = mMovieDetailsArrayList.size();
            }
            return count;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            final ImageView movieImageView;

            ViewHolder(View v) {
                movieImageView = (ImageView) v.findViewById(R.id.imageView_item);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.movie_item, parent, false);
                holder = new ViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }
            if (mMovieDetailsArrayList.size() >= position) {
                MovieDetailsObject tempMovieObject = mMovieDetailsArrayList.get(position);
                holder.movieImageView.setTag(tempMovieObject);
                int errorResId;
                if (mTowPaneLayout) {
                    errorResId = R.drawable.placeholder_poster_small;
                } else {
                    errorResId = R.drawable.placeholder_poster_large;
                }
                Picasso.with(getActivity()).load(tempMovieObject.m_strMoviePosterFullPath)
                        .error(errorResId)
                        .into(holder.movieImageView);
            }
            return row;
        }
    }

    //FetchMovieDetails class to fetch the movie details using TMDB APIs
    private class FetchMovieDetails extends AsyncTask {

        @Override
        protected ArrayList<MovieDetailsObject> doInBackground(Object[] params) {

            String movieDBJSONString = null;
            HttpURLConnection urlConnection;
            BufferedReader reader = null;
            String LOG_TAG = FetchMovieDetails.class.getSimpleName();
            try {

                //Create Query for themoviedb.org
                final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
                String SORT_PARAM = "sort_by";
                String APIKEY_PARAM = "api_key";

                String api_Key = "API_KEY_GOES_HERE";


                Uri builtURI = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, mSortQuery)
                        .appendQueryParameter(APIKEY_PARAM, api_Key)
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
                return getMoviesDetailsfromJSON(movieDBJSONString);
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
                for (int i = 0; i < mMovieDetailsArrayList.size(); ++i) {
                    mMovieAdapter.add(mMovieDetailsArrayList.get(i));
                }
                if (mMovieDetailsArrayList.size() > 0 && mTowPaneLayout) {
                    displayMovieDetailsUI(0);
                }
            }

        }
    }

    private ArrayList<MovieDetailsObject> getMoviesDetailsfromJSON(String movieDBJSONString) throws JSONException {
        final String TMDB_RESULTS = "results";
        final String TMDB_MOVIE_TITLE = "original_title";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_PLOT = "overview";
        final String TMDB_USER_RATING = "vote_average";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_MOVIE_ID = "id";

        JSONObject movieDBJSONObject = new JSONObject(movieDBJSONString);
        JSONArray movieDBArray = movieDBJSONObject.getJSONArray(TMDB_RESULTS);

        String posterBasePath;

        if (mTowPaneLayout) {
            posterBasePath = "http://image.tmdb.org/t/p/w185/";
        } else {
            posterBasePath = "http://image.tmdb.org/t/p/w500/";
        }

        String movie_title, movie_poster_path, movie_plot;
        String movie_user_rating, movie_release_date;
        int movie_id;

        int length = movieDBArray.length();
        mMovieDetailsArrayList.clear();

        for (int i = 0; i < length; ++i) {
            JSONObject movieInfo = movieDBArray.getJSONObject(i);

            movie_title = movieInfo.getString(TMDB_MOVIE_TITLE);
            movie_poster_path = posterBasePath + movieInfo.getString(TMDB_POSTER_PATH);
            movie_plot = movieInfo.getString(TMDB_PLOT);
            movie_user_rating = movieInfo.getString(TMDB_USER_RATING);
            movie_release_date = movieInfo.getString(TMDB_RELEASE_DATE);
            movie_id = movieInfo.getInt(TMDB_MOVIE_ID);

            MovieDetailsObject movie = new MovieDetailsObject(movie_poster_path, movie_title,
                    movie_plot, movie_user_rating, movie_release_date, movie_id);
            mMovieDetailsArrayList.add(movie);
        }
        return mMovieDetailsArrayList;
    }


    private class FetchFavoriteMovieDetailsFromDB extends AsyncTask {

        @Override
        protected ArrayList<MovieDetailsObject> doInBackground(Object[] params) {

            Cursor cursor = getActivity().getContentResolver().query(
                    MovieAppContract.MovieDetailsEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            mMovieDetailsArrayList.clear();
            try {
                if (cursor.moveToFirst()) {
                    do {
                        MovieDetailsObject movie = new MovieDetailsObject(
                                cursor.getString(cursor.getColumnIndex(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_POSTER_PATH)),
                                cursor.getString(cursor.getColumnIndex(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_TITLE)),
                                cursor.getString(cursor.getColumnIndex(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_PLOT)),
                                cursor.getString(cursor.getColumnIndex(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_USER_RATING)),
                                cursor.getString(cursor.getColumnIndex(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_RELEASE_DATE)),
                                Integer.parseInt(cursor.getString(cursor.getColumnIndex(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_ID))));
                        mMovieDetailsArrayList.add(movie);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
            return mMovieDetailsArrayList;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result != null) {
                for (int i = 0; i < mMovieDetailsArrayList.size(); ++i) {
                    mMovieAdapter.add(mMovieDetailsArrayList.get(i));
                }
            }
        }
    }

}




