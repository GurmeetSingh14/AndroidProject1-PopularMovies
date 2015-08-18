package com.example.gurmeet.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
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

    ArrayList<MovieDetailsObject> mMovieDetailsArrayList = null;
    PopularMovieAdapter mMovieAdapter = null;
    GridView moviePostersGrid = null;
    String mSortQuery = null;

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

        //Temporarily fill mMovieDetailsArrayList for first time view
        MovieDetailsObject obj = new MovieDetailsObject(
                "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg", "Intersteller",
                "PLOT", "5/5", "03Aug2015");
        mMovieDetailsArrayList = new ArrayList<MovieDetailsObject>();
        mSortQuery = "popularity.desc";

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public void onStart() {
        super.onStart();
        //Fetch movie details and update the GridView with movie posters
        updateMovieGridView();
    }

    public void updateMovieGridView()
    {
        FetchMovieDetails movieDetails = new FetchMovieDetails();
        movieDetails.execute();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_by_popularity) {
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
        Log.v("MOVIE_APP_4", "In OnItemClick" );

        Intent movieIntent = new Intent(getActivity(), MovieDetailsActivity.class);
        PopularMovieAdapter.ViewHolder holder = (PopularMovieAdapter.ViewHolder)view.getTag();
        MovieDetailsObject movieDetails = (MovieDetailsObject) holder.movieImageView.getTag();
        movieIntent.putExtra("movieTitle", movieDetails.m_strMovieTitle);
        movieIntent.putExtra("moviePosterFullPath", movieDetails.m_strMoviePosterFullPath);
        movieIntent.putExtra("movieUserRating", movieDetails.m_strMovieUserRating);
        movieIntent.putExtra("movieReleaseDate", movieDetails.m_strMovieReleaseDate);
        movieIntent.putExtra("moviePlot", movieDetails.m_strMoviePlot);

        startActivity(movieIntent);
    }

    //MovieDetailsObject holds the movie details like MoviePosterFullPath,
    //MovieTitle, MoviePlot, UserRating, ReleaseData etc.
    public class MovieDetailsObject {

        String m_strMoviePosterFullPath, m_strMovieTitle, m_strMoviePlot = null;
        String m_strMovieUserRating, m_strMovieReleaseDate = null;


        MovieDetailsObject(String moviePosterFullPath, String movieTitle, String moviePlot,
                           String movieUserRating, String movieReleaseDate)
        {
            m_strMoviePosterFullPath = moviePosterFullPath;
            m_strMovieTitle = movieTitle;
            m_strMoviePlot = moviePlot;
            m_strMovieUserRating = movieUserRating;
            m_strMovieReleaseDate = movieReleaseDate;
        }
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

                String api_Key = "<API_KEY_GOES_HERE>";


                Uri builtURI = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, (String) mSortQuery)
                        .appendQueryParameter(APIKEY_PARAM, (String) api_Key)
                        .build();

                URL url = new URL(builtURI.toString());

                Log.v(LOG_TAG, "BuildURL:" + url);


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


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                try {
                    reader.close();
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

            JSONObject movieDBJSONObject = new JSONObject(movieDBJSONString);
            JSONArray movieDBArray = movieDBJSONObject.getJSONArray(TMDB_RESULTS);

            String posterBasePath = "http://image.tmdb.org/t/p/w500/";
            String movie_title, movie_poster_path, movie_plot = null;
            String movie_user_rating, movie_release_date = null;
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
                MovieDetailsObject movie = new MovieDetailsObject(movie_poster_path, movie_title,
                        movie_plot, movie_user_rating, movie_release_date);
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




