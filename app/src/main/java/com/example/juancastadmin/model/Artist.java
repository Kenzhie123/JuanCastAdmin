package com.example.juancastadmin.model;

import java.util.ArrayList;

public class Artist {

    private String artistName;
    private ArrayList<String> tags;

    public Artist(String artistName, ArrayList<String> tags) {
        this.artistName = artistName;
        this.tags = tags;
    }

    public String getArtistName() {
        return artistName;
    }

    public ArrayList<String> getTags() {
        return tags;
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
                "artistName='" + artistName + '\'' +
                ", tags=" + tags +
                '}';
    }
}
