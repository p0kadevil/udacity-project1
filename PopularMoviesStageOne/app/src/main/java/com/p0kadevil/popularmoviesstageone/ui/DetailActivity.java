package com.p0kadevil.popularmoviesstageone.ui;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.p0kadevil.popularmoviesstageone.R;
import com.p0kadevil.popularmoviesstageone.adapters.PosterAdapter;
import com.p0kadevil.popularmoviesstageone.models.MovieInfo;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity
{
    private static final String SAVED_INSTANCE_KEY_MOVIE = "mMovieInfo";

    private MovieInfo mMovieInfo;
    private TextView mTextViewTitle;
    private ImageView mImageViewPoster;
    private TextView mTextViewInfoYear;
    private TextView mTextViewInfoVote;
    private TextView mTextViewInfoOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTextViewTitle = (TextView) findViewById(R.id.tv_title);
        mImageViewPoster = (ImageView) findViewById(R.id.iv_poster);
        mTextViewInfoYear = (TextView) findViewById(R.id.tv_info_year);
        mTextViewInfoVote = (TextView) findViewById(R.id.tv_info_vote);
        mTextViewInfoOverview = (TextView) findViewById(R.id.tv_info_overview);

        if(getIntent() != null && getIntent().hasExtra(MainActivity.EXTRA_MOVIE_DETAIL_OBJECT))
        {
            mMovieInfo = getIntent().getParcelableExtra(MainActivity.EXTRA_MOVIE_DETAIL_OBJECT);
        }
        else if(savedInstanceState != null)
        {
            mMovieInfo = savedInstanceState.getParcelable(SAVED_INSTANCE_KEY_MOVIE);
        }

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fillDetailScreen();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
    {
        outState.putParcelable(SAVED_INSTANCE_KEY_MOVIE, mMovieInfo);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fillDetailScreen()
    {
        mTextViewTitle.setText(mMovieInfo.getOriginalTitle());
        Picasso.with(this).load(PosterAdapter.BASE_URL + mMovieInfo.getPosterPath()).into(mImageViewPoster);
        mTextViewInfoYear.setText(mMovieInfo.getReleaseDate());

        String voteAverage = mMovieInfo.getVoteAverage() + "/10";
        mTextViewInfoVote.setText(voteAverage);

        mTextViewInfoOverview.setText(mMovieInfo.getOverview());
    }
}
