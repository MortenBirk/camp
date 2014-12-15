package com.cac.camp.camp;

/**
 * Created by nikolaiollegaard on 05/12/14.
 */
public class Track {
    private String id, name, artist;


    public Track(String id, String name, String artist) {
        this.id = id;
        this.name = name;
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toURI() {
        return "spotify:track:"+this.id;
    }

    public String toString() {
        return name+" by "+artist;
    }

    public String getArtist() {
        return this.artist;
    }

    public String getSong() {
        return this.name;
    }
}
