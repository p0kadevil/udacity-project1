package com.p0kadevil.popularmoviesstageone.models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;


public class MovieDbResponse
{
    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private ArrayList<MovieInfo> results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<MovieInfo> getResults() {
        return results;
    }

    public void setResults(ArrayList<MovieInfo> results) {
        this.results = results;
    }
}
