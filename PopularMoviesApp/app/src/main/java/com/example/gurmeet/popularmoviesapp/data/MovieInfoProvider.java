package com.example.gurmeet.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Gurmeet on 12-09-2015.
 */
public class MovieInfoProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper movieDbHelper;

    static final int MOVIE_DETAILS = 100;
    static final int MOVIE_TRAILERS = 101;
    static final int MOVIE_REVIEWS = 102;
    static final int MOVIE_DETAILS_FOR_MOVIE_ID = 103;
    static final int MOVIE_TRAILERS_FOR_MOVIE_ID = 104;
    static final int MOVIE_REVIEWS_FOR_MOVIE_ID = 105;


/*    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static{
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID);
    }*/






    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_DETAILS:
                return MovieAppContract.MovieDetailsEntry.CONTENT_TYPE;
            case MOVIE_DETAILS_FOR_MOVIE_ID:
                return MovieAppContract.MovieDetailsEntry.CONTENT_ITEM_TYPE;
            case MOVIE_TRAILERS:
                return MovieAppContract.MovieTrailerEntry.CONTENT_TYPE;
            case MOVIE_TRAILERS_FOR_MOVIE_ID:
                return MovieAppContract.MovieTrailerEntry.CONTENT_TYPE;
            case MOVIE_REVIEWS:
                return MovieAppContract.MovieReviewsEntry.CONTENT_TYPE;
            case MOVIE_REVIEWS_FOR_MOVIE_ID:
                return MovieAppContract.MovieReviewsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }



    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieAppContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieAppContract.PATH_MOVIE_DETAILS, MOVIE_DETAILS);
        matcher.addURI(authority, MovieAppContract.PATH_MOVIE_DETAILS + "/*", MOVIE_DETAILS_FOR_MOVIE_ID);

        matcher.addURI(authority, MovieAppContract.PATH_MOVIE_TRAILERS, MOVIE_TRAILERS);
        matcher.addURI(authority, MovieAppContract.PATH_MOVIE_TRAILERS + "/*", MOVIE_TRAILERS_FOR_MOVIE_ID);

        matcher.addURI(authority, MovieAppContract.PATH_MOVIE_REVIEWS, MOVIE_REVIEWS);
        matcher.addURI(authority, MovieAppContract.PATH_MOVIE_REVIEWS + "/*", MOVIE_REVIEWS_FOR_MOVIE_ID);

        return matcher;
    }

}
