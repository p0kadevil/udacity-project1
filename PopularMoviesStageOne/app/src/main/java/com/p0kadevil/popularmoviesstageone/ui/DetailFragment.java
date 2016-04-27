package com.p0kadevil.popularmoviesstageone.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.p0kadevil.popularmoviesstageone.R;
import com.p0kadevil.popularmoviesstageone.adapters.PosterAdapter;
import com.p0kadevil.popularmoviesstageone.models.MovieInfo;
import com.p0kadevil.popularmoviesstageone.models.ReviewInfo;
import com.p0kadevil.popularmoviesstageone.models.ReviewResponse;
import com.p0kadevil.popularmoviesstageone.models.TrailerInfo;
import com.p0kadevil.popularmoviesstageone.models.TrailerResponse;
import com.p0kadevil.popularmoviesstageone.services.MovieDbIntentService;
import com.squareup.picasso.Picasso;


public class DetailFragment extends Fragment
{
    public static final String TAG = DetailFragment.class.getSimpleName();
    private static final String ARG_MOVIE_INFO_KEY = "MOVIE_INFO";
    private static final String SAVED_INSTANCE_KEY_MOVIE = "mMovieInfo";

    private MovieInfo mMovieInfo;
    private TextView mTextViewTitle;
    private ImageView mImageViewPoster;
    private TextView mTextViewInfoYear;
    private TextView mTextViewInfoVote;
    private TextView mTextViewInfoOverview;

    private TrailerResponse mTrailerResponse;
    private ReviewResponse mReviewResponse;

    private TrailersAndReviewsReceiver mTrailersAndReviewsReceiver;

    public static DetailFragment newInstance(MovieInfo movieInfo)
    {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE_INFO_KEY, movieInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mTrailersAndReviewsReceiver = new TrailersAndReviewsReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mTextViewTitle = (TextView) view.findViewById(R.id.tv_title);
        mImageViewPoster = (ImageView) view.findViewById(R.id.iv_poster);
        mTextViewInfoYear = (TextView) view.findViewById(R.id.tv_info_year);
        mTextViewInfoVote = (TextView) view.findViewById(R.id.tv_info_vote);
        mTextViewInfoOverview = (TextView) view.findViewById(R.id.tv_info_overview);

        if(savedInstanceState != null)
        {
            mMovieInfo = savedInstanceState.getParcelable(SAVED_INSTANCE_KEY_MOVIE);
        }
        else if(getArguments() != null)
        {
            mMovieInfo = getArguments().getParcelable(ARG_MOVIE_INFO_KEY);
        }

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        IntentFilter filter = new IntentFilter(MovieDbIntentService.BROADCAST_TRAILERS_AND_REVIEWS_RESULT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mTrailersAndReviewsReceiver, filter);

        Intent movieDbIntent = new Intent(getActivity(), MovieDbIntentService.class);
        movieDbIntent.putExtra(MovieDbIntentService.EXTRA_TRAILERS_AND_REVIEWS, true);
        movieDbIntent.putExtra(MovieDbIntentService.EXTRA_TRAILER_AND_REVIEW_ID, mMovieInfo.getId());
        getActivity().startService(movieDbIntent);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mTrailersAndReviewsReceiver);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        fillDetailFragmentWithMovieInfo(mMovieInfo);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable(SAVED_INSTANCE_KEY_MOVIE, mMovieInfo);
        super.onSaveInstanceState(outState);
    }

    public void fillDetailFragmentWithMovieInfo(MovieInfo movieInfo)
    {
        mMovieInfo = movieInfo;

        if(mMovieInfo == null)
        {
            return;
        }

        mTextViewTitle.setText(mMovieInfo.getOriginalTitle());

        try
        {
            Picasso.with(getActivity()).load(PosterAdapter.BASE_URL + mMovieInfo.getPosterPath()).into(mImageViewPoster);
        }
        catch(Exception e)
        {
            Log.e(TAG, "An exception was thrown while downloading the image from " + mMovieInfo.getPosterPath() + ": " + e.getMessage());
            e.printStackTrace();
            mImageViewPoster.setImageResource(R.drawable.not_found);
        }

        mTextViewInfoYear.setText(mMovieInfo.getReleaseDate());

        String voteAverage = mMovieInfo.getVoteAverage() + "/10";
        mTextViewInfoVote.setText(voteAverage);

        mTextViewInfoOverview.setText(mMovieInfo.getOverview());
    }

    private void updateTrailerView()
    {
        if(mTrailerResponse == null)
        {
            return;
        }

        for(TrailerInfo trailer : mTrailerResponse.getResults())
        {

        }
    }

    private void updateReviewView()
    {
        if(mReviewResponse == null)
        {
            return;
        }

        for(ReviewInfo review : mReviewResponse.getResults())
        {

        }
    }

    private class TrailersAndReviewsReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent != null && intent.hasExtra(MovieDbIntentService.EXTRA_RESULT_JSON_TRAILERS)
                     && intent.hasExtra(MovieDbIntentService.EXTRA_RESULT_JSON_REVIEWS))
            {
                Gson gson = new Gson();

                try
                {
                    mTrailerResponse = gson.fromJson(intent.getStringExtra(MovieDbIntentService.EXTRA_RESULT_JSON_TRAILERS), TrailerResponse.class);
                    mReviewResponse = gson.fromJson(intent.getStringExtra(MovieDbIntentService.EXTRA_RESULT_JSON_REVIEWS), ReviewResponse.class);

                    updateTrailerView();
                    updateReviewView();
                }
                catch(Exception e)
                {
                    Log.e(TAG, "An exception was thrown while parsing the JSON for trailers and reviews: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
