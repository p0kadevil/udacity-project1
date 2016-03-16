package com.p0kadevil.popularmoviesstageone.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.p0kadevil.popularmoviesstageone.R;
import com.p0kadevil.popularmoviesstageone.models.MovieInfo;

public class DetailActivity extends AppCompatActivity
{
    MovieInfo mMovieInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(getIntent() != null && getIntent().hasExtra(MainActivity.EXTRA_MOVIE_DETAIL_OBJECT))
        {
            mMovieInfo = getIntent().getParcelableExtra(MainActivity.EXTRA_MOVIE_DETAIL_OBJECT);
        }
    }
}
