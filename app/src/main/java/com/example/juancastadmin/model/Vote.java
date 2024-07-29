package com.example.juancastadmin.model;

public class Vote {
    private String artistID;
    private long sunVotes;
    private long startVotes;


    public Vote(String artistID, long sunVotes, long startVotes) {
        this.artistID = artistID;
        this.sunVotes = sunVotes;
        this.startVotes = startVotes;
    }

    public void setArtistID(String artistID) {
        this.artistID = artistID;
    }

    public void setSunVotes(long sunVotes) {
        this.sunVotes = sunVotes;
    }

    public void setStartVotes(long startVotes) {
        this.startVotes = startVotes;
    }
}
