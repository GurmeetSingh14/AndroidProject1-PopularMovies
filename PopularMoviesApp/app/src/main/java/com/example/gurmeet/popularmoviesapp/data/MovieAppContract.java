package com.example.gurmeet.popularmoviesapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Gurmeet on 09-09-2015.
 * Defines Contract between MovieDatabse and View. Also defines names of Table and Columns
 * in the database
 */

public class MovieAppContract {

    //Content Authority for PopularMoviesApp content provider
    public static final String CONTENT_AUTHORITY = "com.example.gurmeet.popularmoviesapp";

    //Base content uri for all the Uri's of PopularMoviesApp
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE_DETAILS = "movie_details";
    public static final String PATH_MOVIE_TRAILERS = "movie_trailers";
    public static final String PATH_MOVIE_REVIEWS = "movie_reviews";

    public static final class MovieDetailsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_DETAILS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DETAILS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DETAILS;


        public static final String TABLE_NAME = "movie_details";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_POSTER_PATH = "movie_poster_path";
        public static final String COLUMN_MOVIE_USER_RATING = "movie_user_rating";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_PLOT = "movie_plot";

        public static Uri buildMovieDetailsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieDetailsForMovieIdUri(String strMovieId) {
            return CONTENT_URI.buildUpon().appendPath(strMovieId).build();
        }
    }

    public static final class MovieTrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_TRAILERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_TRAILERS;

        public static final String TABLE_NAME = "movie_trailers";

        public static final String COLUMN_TRAILER_SOURCE = "trailer_source";
        public static final String COLUMN_TRAILER_NAME = "trailer_name";
        public static final String COLUMN_TRAILER_TYPE = "trailer_type";

        public static final String COLUMN_MOVIE_KEY = "movie_id";


        public static Uri buildMovieTrailersUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildMovieTrailersForMovieIdUri(String strMovieId) {
            return CONTENT_URI.buildUpon().appendPath(strMovieId).build();
        }

    }

    public static final class MovieReviewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEWS;

        public static final String TABLE_NAME = "movie_reviews";

        public static final String COLUMN_REVIEW_AUTHOR = "review_author";
        public static final String COLUMN_REVIEW_CONTENT = "review_content";
        public static final String COLUMN_REVIEW_URL = "review_url";

        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static Uri buildMovieReviewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieReviewsForMovieIdUri(String strMovieId) {
            return CONTENT_URI.buildUpon().appendPath(strMovieId).build();
        }
    }
}
