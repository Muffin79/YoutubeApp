package com.example.muffin.youtubeapp.GsonModels;

import java.io.Serializable;



public class Id implements Serializable{

    private String videoId;
    private String kind;
    private String channelId;
    private String playlistId;

    public String getPlaylistId() {
        return playlistId;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getKind() {
        return kind;
    }

    public String getChannelId() {
        return channelId;
    }
}
