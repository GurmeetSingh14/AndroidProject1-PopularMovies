package com.example.gurmeet.popularmoviesapp.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by Gurmeet on 12-09-2015.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();




    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieInfoProvider.class.getName());
        try{
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: MovieInfoProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieAppContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieAppContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: MovieInfoProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }




    public void testGetType() {
        String type = mContext.getContentResolver().getType(MovieAppContract.MovieDetailsEntry.CONTENT_URI);

        assertEquals("Incorrect MovieDetailsEntry CONTENT_TYPE",
                MovieAppContract.MovieDetailsEntry.CONTENT_TYPE, type);

        String movieID = "94074";
        type = mContext.getContentResolver().getType(
                MovieAppContract.MovieDetailsEntry.buildMovieDetailsForMovieIdUri(movieID));

        assertEquals("Incorrect MovieDetailsEntry for MovieId CONTENT_TYPE",
                MovieAppContract.MovieDetailsEntry.CONTENT_ITEM_TYPE, type);



        String typeTrailer = mContext.getContentResolver().getType(MovieAppContract.MovieTrailerEntry.CONTENT_URI);

        assertEquals("Incorrect MovieTrailerEntry CONTENT_TYPE",
                MovieAppContract.MovieTrailerEntry.CONTENT_TYPE, typeTrailer);

        String movieID_trailer = "94075";
        type = mContext.getContentResolver().getType(
                MovieAppContract.MovieTrailerEntry.buildMovieTrailersForMovieIdUri(movieID_trailer));

        assertEquals("Incorrect MovieTrailerEntry for MovieId CONTENT_TYPE",
                MovieAppContract.MovieTrailerEntry.CONTENT_TYPE, type);




        String typeReview = mContext.getContentResolver().getType(MovieAppContract.MovieReviewsEntry.CONTENT_URI);

        assertEquals("Incorrect MovieReviewsEntry CONTENT_TYPE",
                MovieAppContract.MovieReviewsEntry.CONTENT_TYPE, typeReview);

        String movieID_review = "135397";
        type = mContext.getContentResolver().getType(
                MovieAppContract.MovieReviewsEntry.buildMovieReviewsForMovieIdUri(movieID_review));

        assertEquals("Incorrect MovieReviewsEntry for MovieId CONTENT_TYPE",
                MovieAppContract.MovieReviewsEntry.CONTENT_TYPE, type);
    }




    public void testMovieDetailsQuery() {
        // insert our test records into the database
       /* MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = getFavoriteMovieValues();

        long detailsRowId = db.insert(MovieAppContract.MovieDetailsEntry.TABLE_NAME, null, testValues);
        assertTrue("Could not insert values to database", detailsRowId != -1);

        db.close();*/

        // Test the basic content provider query
        Cursor movieDetailCursor = mContext.getContentResolver().query(
                MovieAppContract.MovieDetailsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        //movieDetailCursor.moveToFirst();

        if(movieDetailCursor.getCount() > 0){
            Log.e("GS_APP", "MovieDetailsEntry Table has values :" + Integer.toString(movieDetailCursor.getCount()) );
        }

        // Test the basic content provider query
        Cursor movieDetailByIdCursor = mContext.getContentResolver().query(
                MovieAppContract.MovieDetailsEntry.buildMovieDetailsForMovieIdUri("135397"),
                null,
                null,
                null,
                null
        );

        //movieDetailCursor.moveToFirst();

        if(movieDetailByIdCursor.getCount() > 0){
            Log.e("GS_APP", "movieDetailByIdCursor has values :" + Integer.toString(movieDetailByIdCursor.getCount()) );
        }


        // Test the basic content provider query
        Cursor movieTrailerCursor = mContext.getContentResolver().query(
                MovieAppContract.MovieTrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if(movieTrailerCursor.getCount() > 0){
            Log.e("GS_APP", "MovieTrailerEntry Table has values :" + Integer.toString(movieTrailerCursor.getCount()) );
        }


        // Test the basic content provider query
        Cursor movieReviewCursor = mContext.getContentResolver().query(
                MovieAppContract.MovieReviewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if(movieReviewCursor.getCount() > 0){
            Log.e("GS_APP", "MovieReviewsEntry Table has values :" + Integer.toString(movieReviewCursor.getCount()) );
        }
        while(movieTrailerCursor.moveToNext()){
            for(int i = 0; i < movieTrailerCursor.getColumnCount(); ++i) {
                Log.e("GS_APP", movieTrailerCursor.getString(i));
            }
        }

        movieDetailCursor.close();
        movieReviewCursor.close();
        movieTrailerCursor.close();
        movieDetailByIdCursor.close();
 /*       for(int i = 0; i < movieDetailCursor.getCount(); ++i) {
            Log.e("GS_APP", movieDetailCursor.getString(i) );
        }*/

    }

    private final int m_nMovieId = 1234;
    private final String m_strMoviePlot = "PLOT";
    private final String m_strMoviePosterFullPath = "POSTER_PATH";
    private final String m_strMovieReleaseDate = "Release Date";
    private final String m_strMovieTitle = "Movie Title";
    private final String m_strMovieUserRating = "User Rating";

    private ContentValues getFavoriteMovieValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_ID, m_nMovieId);
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_PLOT, m_strMoviePlot);
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_POSTER_PATH, m_strMoviePosterFullPath);
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_RELEASE_DATE, m_strMovieReleaseDate);
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_TITLE, m_strMovieTitle);
        contentValues.put(MovieAppContract.MovieDetailsEntry.COLUMN_MOVIE_USER_RATING, m_strMovieUserRating);

        return contentValues;
    }

}
