package com.example.gurmeet.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
public class MovieDetailActivityFragment extends Fragment implements AdapterView.OnItemClickListener{

    private ArrayList<MovieTrailersObject> mMovieTrailerObject = null;
    private ArrayList<MovieReviewsObject> mMovieReviewObject = null;

    private ListView mMovieTrailerListView = null;
    private ListView mMovieReviewListView = null;

    private MovieTrailerAdapter mMovieTrailerAdapter = null;
    private MovieReviewAdapter mMovieReviewAdapter = null;
    int m_nMovieId;

    private static final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();
    private static final String SHARE_INTENT_STRING_BASE = "Checkout the %s trailer at: " +
            " https://www.youtube.com/watch?v=%s #PopularMovieApp";

    private String m_strMovieTitle;
    private String mPopularMovieStr;

    MovieTrailersReviewsObject mMovieTrailersReviewsObject = null;

    ShareActionProvider mShareActionProvider = null;

    public MovieDetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent movieIntent = getActivity().getIntent();


        if(movieIntent != null){
            m_strMovieTitle = movieIntent.getStringExtra("movieTitle");
            String strMoviePosterFullPath = movieIntent.getStringExtra("moviePosterFullPath");
            String strMovieUserRating = movieIntent.getStringExtra("movieUserRating");
            String strMovieReleaseDate = movieIntent.getStringExtra("movieReleaseDate");
            String strMoviePlot = movieIntent.getStringExtra("moviePlot");
            m_nMovieId = movieIntent.getIntExtra("movieId", 0);

            ImageView moviePosterImageView = (ImageView) rootView.findViewById(R.id.imageView_moviePoster);
            Picasso.with(getActivity()).load(strMoviePosterFullPath).into(moviePosterImageView);

            TextView movieTitleTextView = (TextView)rootView.findViewById(R.id.textView_movieTitle);
            movieTitleTextView.setText(m_strMovieTitle);

            TextView movieUserRatingTextView = (TextView)rootView.findViewById(R.id.textView_movieRating);
            movieUserRatingTextView.setText(strMovieUserRating);

            TextView movieReleaseDateTextView = (TextView)rootView.findViewById(R.id.textView_movieReleaseDate);
            movieReleaseDateTextView.setText(strMovieReleaseDate);

            TextView moviePlotTextView = (TextView)rootView.findViewById(R.id.textView_moviePlot);
            moviePlotTextView.setText(strMoviePlot);

            mMovieTrailerObject = new ArrayList<MovieTrailersObject>();
            mMovieReviewObject = new ArrayList<MovieReviewsObject>();

            Log.e("GS_DETAIL", Integer.toString(m_nMovieId));
            FetchMovieTrailersAndReviews movie = new FetchMovieTrailersAndReviews();
            movie.execute(m_nMovieId);

            //Set adapter for MovieTrailerListView
            mMovieTrailerListView = (ListView) rootView.findViewById(R.id.listview_movie_trailers);
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


            //Set adapter for MovieReviewsListView
            mMovieReviewListView = (ListView) rootView.findViewById(R.id.listview_movie_reviews);
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

            //Fix of Scroll Issue when ListView is present in the layout
            //For More info, checkout below link:
            //http://stackoverflow.com/questions/4119441/how-to-scroll-to-top-of-long-scrollview-layout

            ScrollView detail_scrollview = (ScrollView) rootView.findViewById(R.id.scrollView);
            detail_scrollview.smoothScrollTo(0,0);
        }

        return rootView;
    }

    private Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");

        if(mMovieTrailerObject.size() != 0) {

            mPopularMovieStr = String.format(SHARE_INTENT_STRING_BASE, m_strMovieTitle
                    ,(mMovieTrailerObject.get(0)).m_strTrailerSource);
        }
        //mPopularMovieStr = "Test Message";
        shareIntent.putExtra(Intent.EXTRA_TEXT, mPopularMovieStr);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        } else {
            Log.e(LOG_TAG, "NULL share action provider");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public class MovieTrailersReviewsObject {
        ArrayList<MovieTrailersObject> m_movieTrailerObject;
        ArrayList<MovieReviewsObject> m_movieReviewsObjet;

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
        private Context mContext;
        private int mImageCount;

        public MovieTrailerAdapter(Context context, int resource) {
            super(context, resource);
            mContext = context;
        }


        @Override
        public int getCount() {
            int count = 0;
            if(mMovieTrailerObject != null){
                count = mMovieTrailerObject.size();
            }
            Log.e("GS_DETAIL", "Item Count is: " + Integer.toString(count));
            return count;
        }


        @Override
        public long getItemId(int position) {
            return position;
        }


        class ViewHolder {

            TextView trailerNameTextView;
            TextView trailerTypeTextView;
            ViewHolder(View v)
            {
                trailerNameTextView = (TextView) v.findViewById(R.id.textview_movie_trailer_name);
                trailerTypeTextView = (TextView) v.findViewById(R.id.textview_movie_trailer_type);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.movie_trailer_item, parent, false);
            /*ViewHolder holder = null;
            if(row == null){
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.movie_item, parent, false);
                holder = new ViewHolder(row);
                row.setTag(holder);
            }
            else {
                holder = (ViewHolder)row.getTag();
            }*/
            MovieTrailersObject tempMovieTrailerObject = mMovieTrailerObject.get(position);
            /*holder.trailerNameTextView.setTag(tempMovieTrailerObject.m_strTrailerName);
            holder.trailerTypeTextView.setTag(tempMovieTrailerObject.m_strTrailerType);
*/
            TextView trailerName = (TextView)row.findViewById(R.id.textview_movie_trailer_name);
            trailerName.setText(tempMovieTrailerObject.m_strTrailerName);

            TextView trailerType = (TextView)row.findViewById(R.id.textview_movie_trailer_type);
            trailerType.setText(tempMovieTrailerObject.m_strTrailerType);

            return row;
        }
    }


    //Movie Adapter class for GridView
    public class MovieReviewAdapter extends ArrayAdapter<MovieReviewsObject> {
        private Context mContext;
        private int mImageCount;

        public MovieReviewAdapter(Context context, int resource) {
            super(context, resource);
            mContext = context;
        }


        @Override
        public int getCount() {
            int count = 0;
            if(mMovieReviewObject != null){
                count = mMovieReviewObject.size();
            }
            Log.e("GS_DETAIL", "Item Count is: " + Integer.toString(count));
            return count;
        }


        @Override
        public long getItemId(int position) {
            return position;
        }


        class ViewHolder {

            TextView trailerNameTextView;
            TextView trailerTypeTextView;
            ViewHolder(View v)
            {
                trailerNameTextView = (TextView) v.findViewById(R.id.textview_movie_trailer_name);
                trailerTypeTextView = (TextView) v.findViewById(R.id.textview_movie_trailer_type);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.movie_review_item, parent, false);
            /*ViewHolder holder = null;
            if(row == null){
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.movie_item, parent, false);
                holder = new ViewHolder(row);
                row.setTag(holder);
            }
            else {
                holder = (ViewHolder)row.getTag();
            }*/
            MovieReviewsObject tempMovieReviewsObject = mMovieReviewObject.get(position);
            /*holder.trailerNameTextView.setTag(tempMovieTrailerObject.m_strTrailerName);
            holder.trailerTypeTextView.setTag(tempMovieTrailerObject.m_strTrailerType);
*/
            TextView reviewContent = (TextView)row.findViewById(R.id.textViewReview);
            reviewContent.setText(tempMovieReviewsObject.m_strReviewContent);

            TextView reviewAuthor = (TextView)row.findViewById(R.id.textViewAuthor);
            reviewAuthor.setText("Reviewed By: " + tempMovieReviewsObject.m_strReviewAuthor);

            TextView reviewURL = (TextView)row.findViewById(R.id.textViewURL);
            reviewURL.setText("Review URL: "+ tempMovieReviewsObject.m_strReviewURL);

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

    public class FetchMovieTrailersAndReviews extends AsyncTask {

        @Override
        protected MovieTrailersReviewsObject doInBackground(Object[] params) {

            String movieDBJSONString = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String LOG_TAG = FetchMovieTrailersAndReviews.class.getSimpleName();

            String strMovieId = (params[0]).toString();

            try {

                //Create Query for themoviedb.org
                //http://api.themoviedb.org/3/movie/76341?
                // api_key=eff5e06e071bf6e65d367677e3368ea9&append_to_response=trailers,reviews
                String themovieDBQuery = null;
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String MOVIE_URL = BASE_URL + strMovieId;
                String APIKEY_PARAM = "api_key";
                String APPEND_PARAM = "append_to_response";

                String api_Key = "eff5e06e071bf6e65d367677e3368ea9";
                String appendTrailersReviews = "trailers,reviews";


                Uri builtURI = Uri.parse(MOVIE_URL).buildUpon()
                        .appendQueryParameter(APIKEY_PARAM, (String) api_Key)
                        .appendQueryParameter(APPEND_PARAM, (String) appendTrailersReviews)
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
            if(result!= null) {
                mMovieTrailersReviewsObject = (MovieTrailersReviewsObject)result;
                for(int i = 0; i < mMovieTrailersReviewsObject.m_movieTrailerObject.size(); ++i) {
                    mMovieTrailerAdapter.add(mMovieTrailerObject.get(i));
                }

                for(int i = 0; i < mMovieTrailersReviewsObject.m_movieReviewsObjet.size(); ++i) {
                    mMovieReviewAdapter.add(mMovieReviewObject.get(i));
                }

                if(mShareActionProvider != null) {
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
                getActivity().CONNECTIVITY_SERVICE);
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
        String m_strTrailerName, m_strTrailerSource, m_strTrailerType;

        for(int i = 0; i < movieYouTubeTrailersArrayLength; ++i) {
            JSONObject movieYouTubeTrailer = movieDBYouTubeTrailersArray.getJSONObject(i);
            m_strTrailerName = movieYouTubeTrailer.getString("name");
            m_strTrailerSource = movieYouTubeTrailer.getString("source");
            m_strTrailerType = movieYouTubeTrailer.getString("type");
            Log.e("GS_APP", m_strTrailerName);
            Log.e("GS_APP", m_strTrailerSource);
            Log.e("GS_APP", m_strTrailerType);
            mMovieTrailerObject.add(new MovieTrailersObject(m_strTrailerName, m_strTrailerSource,
                    m_strTrailerType));
        }
        Log.e("GS_APP_DETAIL_PREPARE", Integer.toString(mMovieTrailerObject.size()));
        JSONObject movieDBReviewssObject = movieDBJSONObject.getJSONObject(TMDB_REVIEWS);
        JSONArray movieDBReviewResultsArray = movieDBReviewssObject.getJSONArray(TMDB_RESULTS);

        int movieReviewsArrayLength = movieDBReviewResultsArray.length();
        String m_strReviewAuthor, m_strReviewContent, m_strReviewURL;

        for(int i = 0; i < movieReviewsArrayLength; ++i) {
            JSONObject movieReview = movieDBReviewResultsArray.getJSONObject(i);
            m_strReviewAuthor = movieReview.getString("author");
            m_strReviewContent = movieReview.getString("content");
            m_strReviewURL = movieReview.getString("url");
            Log.e("GS_APP_2", m_strReviewAuthor);
            Log.e("GS_APP_2", m_strReviewContent);
            Log.e("GS_APP_2", m_strReviewURL);
            mMovieReviewObject.add(new MovieReviewsObject(m_strReviewAuthor, m_strReviewContent,
                    m_strReviewURL));
        }

        mMovieTrailersReviewsObject = new MovieTrailersReviewsObject(mMovieTrailerObject, mMovieReviewObject);


        Log.e("GS_APP_DETAIL_IN", Integer.toString(mMovieTrailerObject.size()));
        Log.e("GS_APP_DETAIL_IN", Integer.toString(mMovieReviewObject.size()));

        return mMovieTrailersReviewsObject;
    }
}
