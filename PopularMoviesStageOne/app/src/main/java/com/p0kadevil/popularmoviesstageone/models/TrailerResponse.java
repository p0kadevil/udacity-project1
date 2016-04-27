package com.p0kadevil.popularmoviesstageone.models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;


public class TrailerResponse
{
    @SerializedName("id")
    private int id;

    @SerializedName("results")
    private ArrayList<TrailerInfo> results;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public ArrayList<TrailerInfo> getResults()
    {
        return results;
    }

    public void setResults(ArrayList<TrailerInfo> results)
    {
        this.results = results;
    }
}
