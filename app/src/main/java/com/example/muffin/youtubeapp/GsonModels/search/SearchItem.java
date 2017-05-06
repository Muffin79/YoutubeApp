package com.example.muffin.youtubeapp.GsonModels.search;


import com.example.muffin.youtubeapp.GsonModels.Snippet;

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

    public Id getId() {
        return id;
    }
}
