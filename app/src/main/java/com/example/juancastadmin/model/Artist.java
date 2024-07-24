package com.example.juancastadmin.model;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Artist {

    private String artistID;
    private String artistName;
    private ArrayList<String> tags;

    public Artist()
    {

    }
    public Artist(String artistID,String artistName, ArrayList<String> tags) {
        this.artistID = artistID;
        this.artistName = artistName;
        this.tags = tags;
    }

    public String getArtistID() {
        return artistID;
    }

    public String getArtistName() {
        return artistName;
    }

    public ArrayList<String> getTags() {
        return tags;
    }


    public void setArtistID(String artistID) {
        this.artistID = artistID;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }


    @Override
    public String toString() {
        return "Artist{" +
                "artistID='" + artistID + '\'' +
                ", artistName='" + artistName + '\'' +
                ", tags=" + tags ;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        return artistID.equals(((Artist) obj).getArtistID());
    }
}
