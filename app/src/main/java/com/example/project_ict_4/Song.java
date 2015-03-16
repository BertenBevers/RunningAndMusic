package com.example.project_ict_4;

/**
 * Created by Berten on 9/03/2015.
 /* bepalen welke info er bij elke track word opgeslagen (id , titel , artiest)*/
public class Song {
    private long id;
    private String title;
    private String artist;

    public Song (long songID, String songTitle , String songArtist)
    {
        id=songID;
        title=songTitle;
        artist=songArtist;

    }
    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}

}
