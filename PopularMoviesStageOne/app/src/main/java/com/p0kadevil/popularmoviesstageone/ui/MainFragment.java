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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.p0kadevil.popularmoviesstageone.R;
import com.p0kadevil.popularmoviesstageone.adapters.PosterAdapter;
import com.p0kadevil.popularmoviesstageone.models.MovieDbResponse;
import com.p0kadevil.popularmoviesstageone.models.MovieInfo;
import com.p0kadevil.popularmoviesstageone.services.MovieDbIntentService;
import com.p0kadevil.popularmoviesstageone.util.PrefsManager;


public class MainFragment extends Fragment
{
    public static final String TAG = MainFragment.class.getSimpleName();
    public static final String EXTRA_MOVIE_DETAIL_OBJECT = "EXTRA_MOVIE_DETAIL_OBJECT";
    private static final String SAVED_INSTANCE_KEY_MOVIE_RESULTS = "mMovieDbResponseResults";

    private MovieDbResultReceiver mMovieDbResultReceiver;

    private MovieDbResponse mMovieDbResponse;

    private GridView mGridView;
    private TextView mErrorTextView;

    private PosterAdapter mPosterAdapter;

    public static MainFragment newInstance()
    {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mMovieDbResultReceiver = new MovieDbResultReceiver();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_activity_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        IntentFilter filter = new IntentFilter(MovieDbIntentService.BROADCAST_MOVIE_DB_RESULT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMovieDbResultReceiver, filter);
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
                getParent().showMovieDetail(mPosterAdapter.getMovieInfoAtIndex(position));
            }
        });

        if(savedInstanceState != null)
        {
            mMovieDbResponse = new MovieDbResponse();
            mMovieDbResponse.setResults(savedInstanceState.<MovieInfo> getParcelableArrayList(SAVED_INSTANCE_KEY_MOVIE_RESULTS));
        }
        else if(mMovieDbResponse == null)
        {
            int lastSortOrder = PrefsManager.getInt(getParent(), PrefsManager.KEY_SORT_ORDER);

            getParent().showProgressDialog(getResources().getString(R.string.please_wait), getResources().getString(R.string.loading_get_images));
            Intent movieDbIntent = new Intent(getParent(), MovieDbIntentService.class);
            movieDbIntent.putExtra(MovieDbIntentService.EXTRA_SORT_FILTER, lastSortOrder);
            getParent().startService(movieDbIntent);
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
    public void onPause()
    {
        super.onPause();
        LocalBroadcastManager.getInstance(getParent()).unregisterReceiver(mMovieDbResultReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelableArrayList(SAVED_INSTANCE_KEY_MOVIE_RESULTS, mMovieDbResponse.getResults());
        super.onSaveInstanceState(outState);
    }

    private MainActivity getParent()
    {
        return (MainActivity) getActivity();
    }

    private void setErrorTextViewVisibility(boolean visible)
    {
        mErrorTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
        mGridView.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    private void reloadGridViewWithPosters(MovieDbResponse response){

        mMovieDbResponse = response;

        if(mMovieDbResponse == null)
        {
            return;
        }

        if(mPosterAdapter == null)
        {
            mPosterAdapter = new PosterAdapter(getParent(), response.getResults());
        }
        else
        {
            mPosterAdapter.setDataSource(mMovieDbResponse.getResults());
        }

        mGridView.setAdapter(mPosterAdapter);
    }

    private class MovieDbResultReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent != null && intent.hasExtra(MovieDbIntentService.EXTRA_RESULT_JSON))
            {
                Gson gson = new Gson();

                try
                {
                    mMovieDbResponse = gson.fromJson(intent.getStringExtra(MovieDbIntentService.EXTRA_RESULT_JSON), MovieDbResponse.class);
                    setErrorTextViewVisibility(false);
                    reloadGridViewWithPosters(mMovieDbResponse);

                    if(getParent().getDetailFragment() != null)
                    {
                        getParent().getDetailFragment().fillDetailFragmentWithMovieInfo(mMovieDbResponse.getResults().get(0));
                    }
                }
                catch(Exception e)
                {
                    Log.e(TAG, "An exception was thrown while parsing the JSON and setting the Adapter for the gridView: " + e.getMessage());
                    e.printStackTrace();

                    setErrorTextViewVisibility(true);
                }
                finally
                {
                    getParent().dismissProgressDialog();
                }
            }
            else
            {
                setErrorTextViewVisibility(true);
            }
        }
    }
}
