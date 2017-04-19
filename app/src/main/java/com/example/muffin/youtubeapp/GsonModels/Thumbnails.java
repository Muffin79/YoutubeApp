package com.example.muffin.youtubeapp.GsonModels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Muffin on 12.04.2017.
 */

public class Thumbnails implements Serializable{

    @SerializedName("default")
    private ThumbnailItem defaultThumbnail;

    public String getDefaultThumbnailUrl(){
        return defaultThumbnail.getUrl();
    }
}
