package com.example.muffin.youtubeapp.GsonModels.subscriptions;

import java.util.List;


public class Subscriptions {

    private List<SubscriptionItem> items;
    private String nextPageToken;

    public List<SubscriptionItem> getItems() {
        return items;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
}
