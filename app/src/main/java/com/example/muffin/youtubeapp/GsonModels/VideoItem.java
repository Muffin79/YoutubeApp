package com.example.muffin.youtubeapp.GsonModels;


import java.io.Serializable;

public class VideoItem implements Serializable {

    private String id;
    private Snippet snippet;
    private ContentDetails contentDetails;

    public ContentDetails getContentDetails() {
        return contentDetails;
    }


    public String getId() {
        return id;
    }

    public Snippet getSnippet() {
        return snippet;
    }
}
