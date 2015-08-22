package com.example.gurmeet.popularmoviesapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class MovieDetailsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Intent movieIntent = getIntent();
        if(movieIntent != null){
            String strMovieTitle = movieIntent.getStringExtra("movieTitle");
            String strMoviePosterFullPath = movieIntent.getStringExtra("moviePosterFullPath");
            String strMovieUserRating = movieIntent.getStringExtra("movieUserRating");
            String strMovieReleaseDate = movieIntent.getStringExtra("movieReleaseDate");
            String strMoviePlot = movieIntent.getStringExtra("moviePlot");


            ImageView moviePosterImageView = (ImageView) findViewById(R.id.imageView_moviePoster);
            Picasso.with(this).load(strMoviePosterFullPath).into(moviePosterImageView);

            TextView movieTitleTextView = (TextView)findViewById(R.id.textView_movieTitle);
            movieTitleTextView.setText(strMovieTitle);

            TextView movieUserRatingTextView = (TextView)findViewById(R.id.textView_movieRating);
            movieUserRatingTextView.setText(strMovieUserRating);

            TextView movieReleaseDateTextView = (TextView)findViewById(R.id.textView_movieReleaseDate);
            movieReleaseDateTextView.setText(strMovieReleaseDate);

            TextView moviePlotTextView = (TextView)findViewById(R.id.textView_moviePlot);
            moviePlotTextView.setText(strMoviePlot);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
