package com.p0kadevil.popularmoviesstageone.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.p0kadevil.popularmoviesstageone.R;
import com.p0kadevil.popularmoviesstageone.adapters.PosterAdapter;
import com.p0kadevil.popularmoviesstageone.models.MovieInfo;
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

    public static DetailFragment newInstance(MovieInfo movieInfo)
    {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE_INFO_KEY, movieInfo);
        fragment.setArguments(args);
        return fragment;
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
}
