package com.example.muffin.youtubeapp.GsonModels.channel;


import com.example.muffin.youtubeapp.GsonModels.Snippet;

import java.io.Serializable;

public class ChannelItem implements Serializable{

    private String id;
    private Snippet snippet;
    private ChannelStatistic statistics;

    public String getId() {
        return id;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public ChannelStatistic getStatistic() {
        return statistics;
    }
}
