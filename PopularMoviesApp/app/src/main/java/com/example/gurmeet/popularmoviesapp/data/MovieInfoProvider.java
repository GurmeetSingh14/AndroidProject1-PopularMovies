package com.example.gurmeet.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Gurmeet on 12-09-2015.
 */
public class MovieInfoProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper movieDbHelper;

    private static final int MOVIE_DETAILS = 100;
    private static final int MOVIE_TRAILERS = 101;
    private static final int MOVIE_REVIEWS = 102;
    private static final int MOVIE_DETAILS_FOR_MOVIE_ID = 103;
    private static final int MOVIE_TRAILERS_FOR_MOVIE_ID = 104;
    private static final int MOVIE_REVIEWS_FOR_MOVIE_ID = 105;


    private static final SQLiteQueryBuilder sMovieDetailsByTrailersAndReviewsQueryBuilder;

    static{
        sMovieDetailsByTrailersAndReviewsQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMovieDetailsByTrailersAndReviewsQueryBuilder.setTables(
                MovieAppContract.MovieDetailsEntry.TABLE_NAME + " INNER JOIN " +
                        MovieAppContract.MovieTrailerEntry.TABLE_NAME +
                        " ON " + MovieAppContract.MovieDetailsEntry.TABLE_NAME +
                        "." + MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_ID +
                        " = " + MovieAppContract.MovieTrailerEntry.TABLE_NAME +
                        "." + MovieAppContract.MovieTrailerEntry.COLUMN_MOVIE_KEY +
                        " INNER JOIN " +
                        MovieAppContract.MovieReviewsEntry.TABLE_NAME +
                        " ON " + MovieAppContract.MovieTrailerEntry.TABLE_NAME +
                        "." + MovieAppContract.MovieTrailerEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieAppContract.MovieReviewsEntry.TABLE_NAME +
                        "." + MovieAppContract.MovieReviewsEntry.COLUMN_MOVIE_KEY
        );
    }


    //location.location_setting = ?
    private static final String sMovieByIdSelection =
            MovieAppContract.MovieDetailsEntry.TABLE_NAME+
                    "." + MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_ID + " = ? ";



    private Cursor getMovieDetails(
            Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder movieDetailsQueryBuilder = new SQLiteQueryBuilder();

        movieDetailsQueryBuilder.setTables(MovieAppContract.MovieDetailsEntry.TABLE_NAME);

        return movieDetailsQueryBuilder.query(movieDbHelper.getReadableDatabase(),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }


    private Cursor getMovieDetailsForMovieId(
            Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String strMovieId = MovieAppContract.MovieDetailsEntry.getMovieIdFromUri(uri);

        return sMovieDetailsByTrailersAndReviewsQueryBuilder.query(movieDbHelper.getReadableDatabase(),
                null,
                sMovieByIdSelection,
                new String[]{strMovieId},
                null,
                null,
                null
        );
    }

    private Cursor getMovieTrailers(
            Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder movieTrailersQueryBuilder = new SQLiteQueryBuilder();

        movieTrailersQueryBuilder.setTables(MovieAppContract.MovieTrailerEntry.TABLE_NAME);

        return movieTrailersQueryBuilder.query(movieDbHelper.getReadableDatabase(),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }


    private Cursor getMovieTrailersForMovieId(
            Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String strMovieId = MovieAppContract.MovieTrailerEntry.getMovieIdFromUri(uri);

        return sMovieDetailsByTrailersAndReviewsQueryBuilder.query(movieDbHelper.getReadableDatabase(),
                null,
                sMovieByIdSelection,
                new String[]{strMovieId},
                null,
                null,
                null
        );
    }

    private Cursor getMovieReviews(
            Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder movieReviewsQueryBuilder = new SQLiteQueryBuilder();

        movieReviewsQueryBuilder.setTables(MovieAppContract.MovieReviewsEntry.TABLE_NAME);

        return movieReviewsQueryBuilder.query(movieDbHelper.getReadableDatabase(),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }


    private Cursor getMovieReviewsForMovieId(
            Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String strMovieId = MovieAppContract.MovieReviewsEntry.getMovieIdFromUri(uri);

        return sMovieDetailsByTrailersAndReviewsQueryBuilder.query(movieDbHelper.getReadableDatabase(),
                null,
                sMovieByIdSelection,
                new String[]{strMovieId},
                null,
                null,
                null
        );
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Log.e("GS_APP_PROVIDER", "In Query Method of ContentProvider");
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case MOVIE_DETAILS:
            {
                Log.e("GS_APP_PROVIDER", "In MOVIE_DETAILS Method of ContentProvider");
                retCursor = getMovieDetails(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            // "weather/*"
            case MOVIE_DETAILS_FOR_MOVIE_ID: {
                Log.e("GS_APP_PROVIDER", "In MOVIE_DETAILS_FOR_MOVIE_ID Method of ContentProvider");
                retCursor = getMovieDetailsForMovieId(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            // "weather"
            case MOVIE_TRAILERS: {
                Log.e("GS_APP_PROVIDER", "In MOVIE_TRAILERS Method of ContentProvider");
                retCursor = getMovieTrailers(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            // "location"
            case MOVIE_TRAILERS_FOR_MOVIE_ID: {
                Log.e("GS_APP_PROVIDER", "In MOVIE_TRAILERS_FOR_MOVIE_ID Method of ContentProvider");
                retCursor = getMovieTrailersForMovieId(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }

            case MOVIE_REVIEWS: {
                Log.e("GS_APP_PROVIDER", "In MOVIE_REVIEWS Method of ContentProvider");
                retCursor = getMovieReviews(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }

            case MOVIE_REVIEWS_FOR_MOVIE_ID: {
                Log.e("GS_APP_PROVIDER", "In MOVIE_REVIEWS_FOR_MOVIE_ID Method of ContentProvider");
                retCursor = getMovieReviewsForMovieId(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
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

        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri resultUri;

        switch(match) {
            case MOVIE_DETAILS:
                long rowId = db.insert(MovieAppContract.MovieDetailsEntry.TABLE_NAME, null, values);
                if(rowId > 0){
                    resultUri = MovieAppContract.MovieDetailsEntry.buildMovieDetailsUri(rowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            case MOVIE_TRAILERS:
                long rowTrailerId = db.insert(MovieAppContract.MovieTrailerEntry.TABLE_NAME, null, values);
                if(rowTrailerId > 0){
                    resultUri = MovieAppContract.MovieDetailsEntry.buildMovieDetailsUri(rowTrailerId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            case MOVIE_REVIEWS:
                long rowReviewId = db.insert(MovieAppContract.MovieReviewsEntry.TABLE_NAME, null, values);
                if(rowReviewId > 0){
                    resultUri = MovieAppContract.MovieDetailsEntry.buildMovieDetailsUri(rowReviewId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIE_DETAILS:
                rowsDeleted = db.delete(
                        MovieAppContract.MovieDetailsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_TRAILERS:
                rowsDeleted = db.delete(
                        MovieAppContract.MovieTrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_REVIEWS:
                rowsDeleted = db.delete(
                        MovieAppContract.MovieReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE_DETAILS:
                rowsUpdated = db.update(MovieAppContract.MovieDetailsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MOVIE_TRAILERS:
                rowsUpdated = db.update(MovieAppContract.MovieTrailerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MOVIE_REVIEWS:
                rowsUpdated = db.update(MovieAppContract.MovieReviewsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }



    private static UriMatcher buildUriMatcher() {
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
