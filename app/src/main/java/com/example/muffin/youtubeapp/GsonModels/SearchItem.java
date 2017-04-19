package com.example.muffin.youtubeapp.GsonModels;


import java.io.Serializable;

public class SearchItem implements Serializable{

    private Snippet snippet;
    private Id id;

    public Snippet getSnippet() {
        return snippet;
    }

    public String getVideoId(){
        return id.getVideoId();
    }

}
