package com.example.muffin.youtubeapp.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muffin.youtubeapp.GsonModels.subscriptions.SubscriptionItem;
import com.example.muffin.youtubeapp.GsonModels.subscriptions.Subscriptions;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.activities.ChannelActivity;
import com.example.muffin.youtubeapp.adapters.SubscriptionListAdapter;
import com.example.muffin.youtubeapp.tasks.GetResponseTask;
import com.example.muffin.youtubeapp.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class FragmentSubscriptions extends ItemListFragment{

    private List<SubscriptionItem> mSubscriptionItems = new ArrayList<>();

    @Override
    protected RecyclerView.Adapter createAdapter() {
        return new SubscriptionListAdapter(mSubscriptionItems);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        loadSubscriptions();

        return v;
    }

    private void loadSubscriptions(){
        startLoadingAnim();
        mUriBuilder = Uri.parse("https://www.googleapis.com/youtube/v3/subscriptions")
                .buildUpon()
                .appendQueryParameter("key", getString(R.string.youtube_api_key))
                .appendQueryParameter("part", "snippet,contentDetails")
                .appendQueryParameter("maxResults","30")
                .appendQueryParameter("access_token", Utils.getStringFromPrefs(getContext(), Utils.ACCESS_TOKEN_PREF))
                .appendQueryParameter("mine","true");
        Request request = new Request.Builder()
                .url(mUriBuilder.build().toString())
                .build();
        new LoadSubscriptionsTask().execute(request);
    }


    @Override
    protected void loadNewVideo() {
        mUriBuilder.appendQueryParameter("pageToken",mNextPageToken);
        Request request = new Request.Builder()
                .url(mUriBuilder.build().toString())
                .build();
    }

    private class LoadSubscriptionsTask extends GetResponseTask{
        @Override
        protected void onPostExecute(Response response) {
            try {
                Subscriptions subscriptions = mGson.fromJson(response.body().string(),Subscriptions.class);
                mSubscriptionItems.addAll(subscriptions.getItems());
                mAdapter.notifyDataSetChanged();
                mNextPageToken = subscriptions.getNextPageToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private  class LoadNewTask extends GetResponseTask{

        @Override
        protected void onPostExecute(Response response) {
            try {
                String str = response.body().string();
                Subscriptions playList = mGson.fromJson(str, Subscriptions.class);
                mSubscriptionItems.addAll(playList.getItems());
                mNextPageToken = playList.getNextPageToken();
                endLoadingAnim();
                int itemCount = playList.getItems().size();
                mAdapter.notifyItemRangeInserted(mSubscriptionItems.size() - itemCount, itemCount);
            } catch (IOException e) {
                Log.e(ChannelActivity.TAG, Log.getStackTraceString(e));
            }
        }
    }
}
