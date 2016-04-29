package com.p0kadevil.popularmoviesstageone.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.p0kadevil.popularmoviesstageone.R;
import com.p0kadevil.popularmoviesstageone.models.MovieInfo;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


public class PosterAdapter extends BaseAdapter
{
    public static final String TAG = PosterAdapter.class.getSimpleName();

    public static final String BASE_URL = "http://image.tmdb.org/t/p/w342/";

    private Context mContext;
    private ArrayList<MovieInfo> mMovieInfos;

    public PosterAdapter(Context c, ArrayList<MovieInfo> movieInfos) {
        mContext = c;
        mMovieInfos = movieInfos;
    }

    public void setDataSource(ArrayList<MovieInfo> movieInfos)
    {
        mMovieInfos = movieInfos;
        notifyDataSetChanged();
    }

    public MovieInfo getMovieInfoAtIndex(int index){
        return mMovieInfos.get(index);
    }

    @Override
    public int getCount() {

        if(mMovieInfos == null)
        {
            return 0;
        }

        return mMovieInfos.size();
    }

    @Override
    public Object getItem(int position) {return mMovieInfos.get(position);}

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;

        if(convertView == null){

            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);
        }
        else{

            imageView = (ImageView) convertView;
        }

        try{

            Picasso.with(mContext).load(BASE_URL + mMovieInfos.get(position).getPosterPath()).into(imageView);

        }catch(Exception e){

            Log.e(TAG, "An exception was thrown while downloading the image at index " + position + ": " + e.getMessage());
            e.printStackTrace();

            imageView.setImageResource(R.drawable.not_found);
        }


        return imageView;
    }
}
