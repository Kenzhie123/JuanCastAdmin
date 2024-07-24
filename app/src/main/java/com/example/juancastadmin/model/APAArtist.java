package com.example.juancastadmin.model;

import java.io.Serializable;
import java.util.ArrayList;

public class APAArtist extends Artist implements  Serializable{



    private boolean isChecked;


    public APAArtist(String artistID, String artistName, ArrayList<String> tags, boolean isChecked) {
        super(artistID, artistName, tags);
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
