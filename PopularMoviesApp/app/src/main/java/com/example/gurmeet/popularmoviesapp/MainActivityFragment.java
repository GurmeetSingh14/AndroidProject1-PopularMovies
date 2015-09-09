package com.example.gurmeet.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.Network;
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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayList<MovieDetailsObject> mMovieDetailsArrayList = null;
    private PopularMovieAdapter mMovieAdapter = null;
    private GridView moviePostersGrid = null;
    private String mSortQuery = null;

//    private ArrayList<MovieTrailersObject> mMovieTrailerObject = null;
//    private ArrayList<MovieReviewsObject> mMovieReviewObject = null;

    static final String SORT_QUERY = "sort_query";
    static final String MOVIE_DETAILS_LIST = "MovieDetailObjectsArrayList";
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Initialize GridView member variable
        moviePostersGrid = (GridView) rootView.findViewById(R.id.gridView_home);

        //Initialize MovieAdapter and attach it with the GridView
        mMovieAdapter = new PopularMovieAdapter(getActivity(), R.layout.movie_item);
        moviePostersGrid.setAdapter(mMovieAdapter);

        //Set Click listener for GridView items
        moviePostersGrid.setOnItemClickListener(this);

        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get the sort query type from savedInstanceState (if available)
        if((savedInstanceState!= null) && (savedInstanceState.containsKey(SORT_QUERY))) {
            mSortQuery = savedInstanceState.getString(SORT_QUERY);
        } else {
            mSortQuery = "popularity.desc";
        }

        //Get the MovieDetailsArrayList from the savedInstanceState (if available)
        if((savedInstanceState != null) && savedInstanceState.containsKey(MOVIE_DETAILS_LIST) ) {
            mMovieDetailsArrayList = savedInstanceState.getParcelableArrayList(MOVIE_DETAILS_LIST);

        } else {
            mMovieDetailsArrayList = new ArrayList<MovieDetailsObject>();
            //Fetch movie details and update the GridView with movie posters
            updateMovieGridView();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SORT_QUERY, mSortQuery);
        outState.putParcelableArrayList(MOVIE_DETAILS_LIST, mMovieDetailsArrayList);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void updateMovieGridView() {
        //Check if network is available and fetch the movie details from TMDB
        if (isNetworkAvailable()) {
            FetchMovieDetails movieDetails = new FetchMovieDetails();
            movieDetails.execute();
        }
        else{
        //If network is not available inform the user to restroe the network connection
            Toast toastNoConnection = Toast.makeText(getActivity(), "Please restore the network " +
                    "connection and press \"Refresh\" menu item.", Toast.LENGTH_LONG);
            toastNoConnection.show();
        }
    }

    //This method checks if network connection is available or not
    private boolean isNetworkAvailable() {
        ConnectivityManager connectionManager = (ConnectivityManager) getActivity()
                .getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo ntwkInfo = connectionManager.getActiveNetworkInfo();
        return ((ntwkInfo != null) && ntwkInfo.isConnected());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            updateMovieGridView();
            return true;
        }
        else if (id == R.id.action_sort_by_popularity) {
            mSortQuery = "popularity.desc";
            updateMovieGridView();
            return true;
        }
        else if (id == R.id.action_sort_by_user_rating) {
            mSortQuery = "vote_average.desc";
            updateMovieGridView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("GS_APP_1", Integer.toString(mMovieDetailsArrayList.get(position).m_nMovieId));

//        FetchMovieTrailersAndReviews movie = new FetchMovieTrailersAndReviews();
//        movie.execute(mMovieDetailsArrayList.get(position).m_nMovieId);

        Intent movieIntent = new Intent(getActivity(), MovieDetailActivity.class);
        PopularMovieAdapter.ViewHolder holder = (PopularMovieAdapter.ViewHolder)view.getTag();
        MovieDetailsObject movieDetails = (MovieDetailsObject) holder.movieImageView.getTag();


        movieIntent.putExtra("movieTitle", movieDetails.m_strMovieTitle);
        movieIntent.putExtra("moviePosterFullPath", movieDetails.m_strMoviePosterFullPath);
        movieIntent.putExtra("movieUserRating", movieDetails.m_strMovieUserRating);
        movieIntent.putExtra("movieReleaseDate", movieDetails.m_strMovieReleaseDate);
        movieIntent.putExtra("moviePlot", movieDetails.m_strMoviePlot);
        movieIntent.putExtra("movieId", movieDetails.m_nMovieId);
        Log.e("GS_DETAIL_IN", Integer.toString(movieDetails.m_nMovieId));
/*        Log.e("GS_APP_DETAIL_IN", Integer.toString(mMovieTrailerObject.size()));
        Log.e("GS_APP_DETAIL_IN", Integer.toString(mMovieReviewObject.size()));


        movieIntent.putParcelableArrayListExtra("mMovieTrailerObject", mMovieTrailerObject);
        movieIntent.putParcelableArrayListExtra("mMovieReviewObject", mMovieReviewObject);*/
        startActivity(movieIntent);
    }






    //MovieDetailsObject holds the movie details like MoviePosterFullPath,
    //MovieTitle, MoviePlot, UserRating, ReleaseData etc.
    public class MovieDetailsObject implements Parcelable {

        String m_strMoviePosterFullPath, m_strMovieTitle, m_strMoviePlot = null;
        String m_strMovieUserRating, m_strMovieReleaseDate = null;
        int m_nMovieId;


        MovieDetailsObject(String moviePosterFullPath, String movieTitle, String moviePlot,
                           String movieUserRating, String movieReleaseDate, int movieId)
        {
            m_strMoviePosterFullPath = moviePosterFullPath;
            m_strMovieTitle = movieTitle;
            m_strMoviePlot = moviePlot;
            m_strMovieUserRating = movieUserRating;
            m_strMovieReleaseDate = movieReleaseDate;
            m_nMovieId = movieId;
        }

        private MovieDetailsObject(Parcel parcel)
        {
            Log.v("GS_TAG", "In Parcabel function");
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

        public final Parcelable.Creator<MovieDetailsObject> CREATOR = new Parcelable.ClassLoaderCreator<MovieDetailsObject>(){

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










    //FetchMovieDetails class to fetch the movie details using TMDB APIs
    public class FetchMovieDetails extends AsyncTask {

        @Override
        protected ArrayList<MovieDetailsObject> doInBackground(Object[] params) {

            String movieDBJSONString = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String LOG_TAG = FetchMovieDetails.class.getSimpleName();
            try {

                //Create Query for themoviedb.org
                String themovieDBQuery = null;
                final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
                String SORT_PARAM = "sort_by";
                String APIKEY_PARAM = "api_key";

                String api_Key = "eff5e06e071bf6e65d367677e3368ea9";


                Uri builtURI = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, (String) mSortQuery)
                        .appendQueryParameter(APIKEY_PARAM, (String) api_Key)
                        .build();

                URL url = new URL(builtURI.toString());

                Log.v(LOG_TAG, "BuildURL:" + url);
            if(isNetworkAvailable()) {

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
                    if(reader != null) {
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
            if(result != null){
                for(int i = 0; i < mMovieDetailsArrayList.size(); ++i) {
                    mMovieAdapter.add(mMovieDetailsArrayList.get(i));
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

            String posterBasePath = "http://image.tmdb.org/t/p/w500/";
            String movie_title, movie_poster_path, movie_plot = null;
            String movie_user_rating, movie_release_date = null;
            int movie_id = 0;

            ImageView posterImageView = null;
            int length = movieDBArray.length();
            mMovieDetailsArrayList.clear();


            String moviePath;
            for (int i = 0; i < length; ++i) {
                JSONObject movieInfo = movieDBArray.getJSONObject(i);

                movie_title = movieInfo.getString(TMDB_MOVIE_TITLE);
                movie_poster_path = posterBasePath + movieInfo.getString(TMDB_POSTER_PATH);
                movie_plot = movieInfo.getString(TMDB_PLOT);
                movie_user_rating = movieInfo.getString(TMDB_USER_RATING);
                movie_release_date = movieInfo.getString(TMDB_RELEASE_DATE);
                movie_id = movieInfo.getInt(TMDB_MOVIE_ID);

                Log.e("GS_APP", "Movie ID: " + Integer.toString(movie_id));

                MovieDetailsObject movie = new MovieDetailsObject(movie_poster_path, movie_title,
                        movie_plot, movie_user_rating, movie_release_date, movie_id);
                mMovieDetailsArrayList.add(movie);
            }

            return mMovieDetailsArrayList;
        }

        //Movie Adapter class for GridView
        public class PopularMovieAdapter extends ArrayAdapter<MovieDetailsObject> {
            private Context mContext;
            private int mImageCount;

            public PopularMovieAdapter(Context context, int resource) {
                super(context, resource);
                mContext = context;
            }


            @Override
            public int getCount() {
                int count = 0;
                if(mMovieDetailsArrayList != null){
                    count = mMovieDetailsArrayList.size();
                }
                return count;
            }


            @Override
            public long getItemId(int position) {
                return position;
            }


            class ViewHolder {

                ImageView movieImageView;
                ViewHolder(View v)
                {
                    movieImageView = (ImageView) v.findViewById(R.id.imageView_item);
                }
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = convertView;
                ViewHolder holder = null;
                if(row == null){
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(R.layout.movie_item, parent, false);
                    holder = new ViewHolder(row);
                    row.setTag(holder);
                }
                else {
                    holder = (ViewHolder)row.getTag();
                }
                MovieDetailsObject tempMovieObject = mMovieDetailsArrayList.get(position);
                holder.movieImageView.setTag(tempMovieObject);
                Picasso.with(getActivity()).load(tempMovieObject.m_strMoviePosterFullPath).into(holder.movieImageView);

                return row;
            }
        }
    }




