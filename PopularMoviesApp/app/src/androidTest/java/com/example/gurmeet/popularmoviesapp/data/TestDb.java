package com.example.gurmeet.popularmoviesapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.gurmeet.popularmoviesapp.data.MovieAppContract.MovieDetailsEntry;

import java.util.HashSet;

/**
 * Created by Gurmeet on 09-09-2015.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieAppContract.MovieDetailsEntry.TABLE_NAME);
        tableNameHashSet.add(MovieAppContract.MovieTrailerEntry.TABLE_NAME);
        tableNameHashSet.add(MovieAppContract.MovieReviewsEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without the movie details entry, trailers entry" +
                        " and reviews entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieAppContract.MovieDetailsEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> moviedetailsColumnHashSet = new HashSet<String>();
        moviedetailsColumnHashSet.add(MovieDetailsEntry._ID);
        moviedetailsColumnHashSet.add(MovieDetailsEntry.COLUMN_MOVIE_ID);
        moviedetailsColumnHashSet.add(MovieDetailsEntry.COLUMN_MOVIE_PLOT);
        moviedetailsColumnHashSet.add(MovieDetailsEntry.COLUMN_MOVIE_POSTER_PATH);
        moviedetailsColumnHashSet.add(MovieDetailsEntry.COLUMN_MOVIE_RELEASE_DATE);
        moviedetailsColumnHashSet.add(MovieDetailsEntry.COLUMN_MOVIE_TITLE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            moviedetailsColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                moviedetailsColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testMovieDetailsTable() {
        // First step: Get reference to writable database
        MovieDbHelper dbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = TestUtilities.createMovieDetailsTestValues();

        // Insert ContentValues into database and get a row ID back
        long rowID;
        rowID = db.insert(MovieDetailsEntry.TABLE_NAME,null, testValues);

        assertTrue(rowID != -1);
        // Query the database and receive a Cursor back

        Cursor cursor = db.query(MovieDetailsEntry.TABLE_NAME, null, null,
                null, null,null, null);

        // Move the cursor to a valid database row
        assertTrue("There is no row in the cursor", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        for(int i = 0; i < cursor.getColumnCount(); ++i) {
            Log.i("TEST_DB", cursor.getString(i));
        }
        // Finally, close the cursor and database
        cursor.close();
        db.close();
    }

    public void testMovieTrailerTable() {
        // First step: Get reference to writable database
        MovieDbHelper dbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = TestUtilities.createMovieTrailerTestValues();

        // Insert ContentValues into database and get a row ID back
        long rowID;
        rowID = db.insert(MovieAppContract.MovieTrailerEntry.TABLE_NAME,null, testValues);

        assertTrue(rowID != -1);
        // Query the database and receive a Cursor back

        Cursor cursor = db.query(MovieAppContract.MovieTrailerEntry.TABLE_NAME, null, null,
                null, null,null, null);

        // Move the cursor to a valid database row
        assertTrue("There is no row in the cursor", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        for(int i = 0; i < cursor.getColumnCount(); ++i) {
            Log.i("TEST_DB_TRAILERS", cursor.getString(i));
        }
        // Finally, close the cursor and database
        cursor.close();
        db.close();
    }


    public void testMovieReviewTable() {
        // First step: Get reference to writable database
        MovieDbHelper dbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = TestUtilities.createMovieReviewTestValues();

        // Insert ContentValues into database and get a row ID back
        long rowID;
        rowID = db.insert(MovieAppContract.MovieReviewsEntry.TABLE_NAME,null, testValues);

        assertTrue(rowID != -1);
        // Query the database and receive a Cursor back

        Cursor cursor = db.query(MovieAppContract.MovieReviewsEntry.TABLE_NAME, null, null,
                null, null,null, null);

        // Move the cursor to a valid database row
        assertTrue("There is no row in the cursor", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        for(int i = 0; i < cursor.getColumnCount(); ++i) {
            Log.i("TEST_DB_REVIEWS", cursor.getString(i));
        }
        // Finally, close the cursor and database
        cursor.close();
        db.close();
    }
}
