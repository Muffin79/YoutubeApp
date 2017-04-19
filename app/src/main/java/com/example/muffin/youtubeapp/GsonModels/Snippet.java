package com.example.muffin.youtubeapp.GsonModels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Muffin on 10.04.2017.
 */

public class Snippet implements Serializable {

    @SerializedName("publishedAt")
    private String publishTime;
    private String channelId;
    private String channelTitle;
    private String title;
    private String description;
    private Thumbnails thumbnails;

    public String getChannelTitle() {
        return channelTitle;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getTitle() {
        return title;
    }

    public Thumbnails getThumbnails() {
        return thumbnails;
    }

    public String getDescription() {
        return description;
    }
}
