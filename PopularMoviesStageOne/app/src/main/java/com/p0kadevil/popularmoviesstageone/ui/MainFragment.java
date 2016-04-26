package com.p0kadevil.popularmoviesstageone.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import com.p0kadevil.popularmoviesstageone.R;
import com.p0kadevil.popularmoviesstageone.adapters.PosterAdapter;
import com.p0kadevil.popularmoviesstageone.models.MovieDbResponse;
import com.p0kadevil.popularmoviesstageone.models.MovieInfo;

public class MainFragment extends Fragment
{
    public static final String TAG = MainFragment.class.getSimpleName();
    public static final String EXTRA_MOVIE_DETAIL_OBJECT = "EXTRA_MOVIE_DETAIL_OBJECT";
    private static final String SAVED_INSTANCE_KEY_MOVIE_RESULTS = "mMovieDbResponseResults";

    private MovieDbResponse mMovieDbResponse;

    private GridView mGridView;
    private TextView mErrorTextView;

    private PosterAdapter mPosterAdapter;

    public static MainFragment newInstance()
    {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_activity_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mErrorTextView = (TextView) view.findViewById(R.id.tv_error);
        mGridView = (GridView) view.findViewById(R.id.gv_posters);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ((MainActivity) getActivity()).showMovieDetail(mPosterAdapter.getMovieInfoAtIndex(position));
            }
        });

        if(savedInstanceState != null)
        {
            mMovieDbResponse = new MovieDbResponse();
            mMovieDbResponse.setResults(savedInstanceState.<MovieInfo> getParcelableArrayList(SAVED_INSTANCE_KEY_MOVIE_RESULTS));
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        reloadGridViewWithPosters(mMovieDbResponse);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelableArrayList(SAVED_INSTANCE_KEY_MOVIE_RESULTS, mMovieDbResponse.getResults());
        super.onSaveInstanceState(outState);
    }

    public void setErrorTextViewVisibility(boolean visible)
    {
        mErrorTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
        mGridView.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    public void reloadGridViewWithPosters(MovieDbResponse response){

        mMovieDbResponse = response;

        if(mMovieDbResponse == null)
        {
            return;
        }

        if(mPosterAdapter == null)
        {
            mPosterAdapter = new PosterAdapter(getActivity(), response.getResults());
        }
        else
        {
            mPosterAdapter.setDataSource(mMovieDbResponse.getResults());
        }

        mGridView.setAdapter(mPosterAdapter);
    }
}
