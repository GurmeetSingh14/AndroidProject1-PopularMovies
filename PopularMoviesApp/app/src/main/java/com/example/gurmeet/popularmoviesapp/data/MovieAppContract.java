package com.example.gurmeet.popularmoviesapp.data;

import android.provider.BaseColumns;

/**
 * Created by Gurmeet on 09-09-2015.
 */

public class MovieAppContract {

    public static final class MovieDetailsEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie_details";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_POSTER_PATH = "movie_poster_path";
        public static final String COLUMN_MOVIE_USER_RATING = "movie_user_rating";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_PLOT = "movie_plot";
    }

    public static final class MovieTrailerEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie_trailers";

        public static final String COLUMN_TRAILER_SOURCE = "trailer_source";
        public static final String COLUMN_TRAILER_NAME = "trailer_name";
        public static final String COLUMN_TRAILER_TYPE = "trailer_type";

        public static final String COLUMN_MOVIE_KEY = "movie_id";


    }

    public static final class MovieReviewsEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie_reviews";

        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_REVIEW_AUTHOR = "review_author";
        public static final String COLUMN_REVIEW_CONTENT = "review_content";
        public static final String COLUMN_REVIEW_URL = "review_url";

        public static final String COLUMN_MOVIE_KEY = "movie_id";
    }
}
