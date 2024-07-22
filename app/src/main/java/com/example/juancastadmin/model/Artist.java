package com.example.juancastadmin.model;

import java.util.ArrayList;

public class Artist {

    private String artistName;
    private ArrayList<String> tags;
    private String artistProfile;

    public Artist(String artistName, ArrayList<String> tags, String artistProfile) {
        this.artistName = artistName;
        this.tags = tags;
        this.artistProfile = artistProfile;
    }

    public String getArtistName() {
        return artistName;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public String getArtistProfile() {
        return artistProfile;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setArtistProfile(String artistProfile) {
        this.artistProfile = artistProfile;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "artistName='" + artistName + '\'' +
                ", tags=" + tags +
                ", artistProfile='" + artistProfile + '\'' +
                '}';
    }
}
