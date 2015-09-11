package com.example.gurmeet.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Movie;

import com.example.gurmeet.popularmoviesapp.data.MovieAppContract.MovieDetailsEntry;
import com.example.gurmeet.popularmoviesapp.data.MovieAppContract.MovieReviewsEntry;
import com.example.gurmeet.popularmoviesapp.data.MovieAppContract.MovieTrailerEntry;

/**
 * Created by Gurmeet on 09-09-2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "popular_movie_app.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_DETAILS_TABLE = "CREATE TABLE " + MovieDetailsEntry.TABLE_NAME + " ("
                + MovieDetailsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MovieDetailsEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL,"
                + MovieDetailsEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL,"
                + MovieDetailsEntry.COLUMN_MOVIE_USER_RATING + " TEXT NOT NULL,"
                + MovieDetailsEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL,"
                + MovieDetailsEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL,"
                + MovieDetailsEntry.COLUMN_MOVIE_PLOT + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_MOVIE_DETAILS_TABLE);


        final String SQL_CREATE_MOVIE_TRAILER_TABLE = "CREATE TABLE " + MovieTrailerEntry.TABLE_NAME + " ("
                + MovieTrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MovieTrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL,"
                + MovieTrailerEntry.COLUMN_TRAILER_TYPE + " TEXT NOT NULL,"
                + MovieTrailerEntry.COLUMN_TRAILER_SOURCE + " TEXT NOT NULL,"
                + MovieTrailerEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL,"

                // Set up the location column as a foreign key to location table.
                + " FOREIGN KEY (" + MovieTrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieDetailsEntry.TABLE_NAME + " (" + MovieDetailsEntry.COLUMN_MOVIE_ID + "));";


        db.execSQL(SQL_CREATE_MOVIE_TRAILER_TABLE);



        final String SQL_CREATE_MOVIE_REVIEW_TABLE = "CREATE TABLE " + MovieReviewsEntry.TABLE_NAME + " ("
                + MovieReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MovieReviewsEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL,"
                + MovieReviewsEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL,"
                + MovieReviewsEntry.COLUMN_REVIEW_URL + " TEXT NOT NULL,"
                + MovieReviewsEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL,"

        // Set up the location column as a foreign key to location table.
        + " FOREIGN KEY (" + MovieReviewsEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieDetailsEntry.TABLE_NAME + " (" + MovieDetailsEntry.COLUMN_MOVIE_ID + "));";

        db.execSQL(SQL_CREATE_MOVIE_REVIEW_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieDetailsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieTrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieReviewsEntry.TABLE_NAME);
        onCreate(db);
    }
}
