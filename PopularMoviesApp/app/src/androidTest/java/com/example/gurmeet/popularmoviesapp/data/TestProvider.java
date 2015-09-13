package com.example.gurmeet.popularmoviesapp.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.test.AndroidTestCase;

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

        String movieID_review = "94076";
        type = mContext.getContentResolver().getType(
                MovieAppContract.MovieReviewsEntry.buildMovieReviewsForMovieIdUri(movieID_review));

        assertEquals("Incorrect MovieReviewsEntry for MovieId CONTENT_TYPE",
                MovieAppContract.MovieReviewsEntry.CONTENT_TYPE, type);
    }

}
