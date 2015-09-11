package com.example.gurmeet.popularmoviesapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/**
 * Created by Gurmeet on 09-09-2015.
 */
public class TestUtilities extends AndroidTestCase{

    static long insertMovieDetails(Context context) {
     long movie_detail_row;

        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieDetailsTestValues();

        movie_detail_row = db.insert(MovieAppContract.MovieDetailsEntry.TABLE_NAME, null, testValues);

//        // Verify we got a row back.
        assertTrue("Error: Failure to insert values", movie_detail_row != -1);

     return movie_detail_row;
    }


    static ContentValues createMovieDetailsTestValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_ID, "1000");
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_PLOT, "Plot");
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_POSTER_PATH, "poster_path");
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_RELEASE_DATE, "09/10/2015");
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_TITLE, "Dummy Movie");
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_USER_RATING, "10.0");


        return contentValues;
    }


    static ContentValues createMovieTrailerTestValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieAppContract.MovieTrailerEntry.COLUMN_MOVIE_KEY, "1000");
        contentValues.put(MovieAppContract.MovieTrailerEntry.COLUMN_TRAILER_NAME, "Trailer Name");
        contentValues.put(MovieAppContract.MovieTrailerEntry.COLUMN_TRAILER_SOURCE, "Trailer Source");
        contentValues.put(MovieAppContract.MovieTrailerEntry.COLUMN_TRAILER_TYPE, "Trailer Type");

        return contentValues;
    }


    static ContentValues createMovieReviewTestValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieAppContract.MovieReviewsEntry.COLUMN_MOVIE_KEY, "1000");
        contentValues.put(MovieAppContract.MovieReviewsEntry.COLUMN_REVIEW_AUTHOR, "Review Author");
        contentValues.put(MovieAppContract.MovieReviewsEntry.COLUMN_REVIEW_CONTENT, "Review Content");
        contentValues.put(MovieAppContract.MovieReviewsEntry.COLUMN_REVIEW_URL, "Review URL");
        return contentValues;
    }
}
