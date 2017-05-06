package com.example.muffin.youtubeapp.GsonModels.search;


import java.io.Serializable;
import java.util.List;

public class SearchList implements Serializable{

    private String nextPageToken;
    private List<SearchItem> items;

    public String getNextPageToken() {
        return nextPageToken;
    }

    public List<SearchItem> getItems() {
        return items;
    }
}
