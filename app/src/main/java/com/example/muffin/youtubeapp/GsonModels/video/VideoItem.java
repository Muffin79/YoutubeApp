package com.example.muffin.youtubeapp.GsonModels.video;


import com.example.muffin.youtubeapp.GsonModels.ContentDetails;
import com.example.muffin.youtubeapp.GsonModels.Snippet;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VideoItem implements Serializable {

    private String id;
    private Snippet snippet;
    private ContentDetails contentDetails;
    @SerializedName("statistics")
    private Statistic videoStatistic;

    public ContentDetails getContentDetails() {
        return contentDetails;
    }


    public String getId() {
        return id;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public Statistic getVideoStatistic() {
        return videoStatistic;
    }

    public long getViewCount(){
        return videoStatistic.getViewCount();
    }

    public long getLikeCount(){
        return videoStatistic.getLikeCount();
    }

    public long getDislikeCount(){
        return videoStatistic.getDislikeCount();
    }
}
