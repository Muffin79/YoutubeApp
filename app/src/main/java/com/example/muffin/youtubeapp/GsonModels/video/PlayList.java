package com.example.muffin.youtubeapp.GsonModels.video;



import java.io.Serializable;
import java.util.List;

public class PlayList implements Serializable{

    private String nextPageToken;
    private List<VideoItem> items;

    public String getNextPageToken() {
        return nextPageToken;
    }

    public List<VideoItem> getItems() {
        return items;
    }


}
