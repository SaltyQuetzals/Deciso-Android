package com.example.quont.queueband;

import android.widget.ImageView;

import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by quont on 6/19/2016.
 */
public class Song {
    private String title, author, source, art, id;
    private int votes;

    public Song() {
        title = "";
        author = "";
        source = "";
        votes = -1;
    }
    public Song(String title, String author, String source, int votes, String artURL, String id) {
        this.title = title;
        this.author = author;
        this.source = source;
        this.votes = votes;
        this.art = artURL;
        this.id = id;
    }

    public String getTitle()    {
        return title;
    }

    public void setTitle(String title)  {
        this.title = title;
    }

    public String getAuthor()   {
        return author;
    }

    public void setAuthor(String author)    {
        this.author = author;
    }

    public String getSource()   {
        return source;
    }

    public void setSource(String source)    {
        this.source = source;
    }

    public int getVotes()   {
        return votes;
    }

    public void setVotes(int votes)  {
        this.votes = votes;
    }

    public String getArt()   {
        return art;
    }

    public void setArt(String art)   {
        this.art = art;
    }

    public String getId() {
        return id;
    }
}
