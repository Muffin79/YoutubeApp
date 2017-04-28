package com.example.muffin.youtubeapp.fragments;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.muffin.youtubeapp.GsonModels.PlayList;
import com.example.muffin.youtubeapp.GsonModels.VideoItem;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.adapters.PlayListAdapter;
import com.example.muffin.youtubeapp.tasks.GetResponseTask;
import com.example.muffin.youtubeapp.utils.OnVideoSelectedListener;
import com.example.muffin.youtubeapp.utils.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayListFragment extends VideoListFragment {

    private static final String ARGS_VIDEO_LIST = "argument_video_list";


    private int lastPosition = 0;
    private List<VideoItem> videoItems = new ArrayList<>();
    private boolean loadPop = false;

    public static PlayListFragment newInstance(PlayList playList) {
        PlayListFragment fragment = new PlayListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_VIDEO_LIST, playList);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater,container,savedInstanceState);

        if(loadPop){
            loadPopularVideo();
        }


        return v;
    }


    public void setLoadPop(boolean loadPop) {
        this.loadPop = loadPop;
    }

    @Override
    protected RecyclerView.Adapter createAdapter() {
        return new PlayListAdapter(videoItems,callback);
    }


    public void loadPopularVideo() {
        startLoadingAnim();
        uriBuilder = Uri.parse("https://www.googleapis.com/youtube/v3/videos")
                .buildUpon()
                .appendQueryParameter("key", getString(R.string.youtube_api_key))
                .appendQueryParameter("part", "snippet,contentDetails")
                .appendQueryParameter("chart", "mostpopular")
                .appendQueryParameter("maxResults", "50");

        try {
            URL url = new URL(uriBuilder.build().toString());
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            new LoadVideoItemTask().execute(request);
        } catch (MalformedURLException e) {

            e.printStackTrace();
        }
    }

    private void getLikedVideos(){
        String  urlStr = Uri.parse("https://www.googleapis.com/youtube/v3/videos")
                .buildUpon()
                .appendQueryParameter("key",getString(R.string.youtube_api_key))
                .appendQueryParameter("part","snippet,contentDetails")
                .appendQueryParameter("access_token", Utils.getAccessToken(getContext()))
                .appendQueryParameter("myRating","like")
                .appendQueryParameter("maxResults","10")
                .build().toString();
        try {
            URL url = new URL(urlStr);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            new LoadVideoItemTask().execute(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadNewVideo() {
        startLoadingAnim();
        String urlStr = uriBuilder
                .appendQueryParameter("pageToken", nextPageToken)
                .build().toString();
        Log.d("NextPageToken",urlStr);
        try {
            URL url = new URL(urlStr);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            new LoadNewVideoTask().execute(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (OnVideoSelectedListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnVideoSelectedListener");
        }
    }

    public void addVideoItems(List<VideoItem> videoItems) {
        lastPosition = recyclerView.getVerticalScrollbarPosition();
        this.videoItems.addAll(videoItems);
        adapter.notifyItemRangeInserted(this.videoItems.size() - videoItems.size(), videoItems.size());
        recyclerView.scrollToPosition(lastPosition);
    }

    private class LoadVideoItemTask extends GetResponseTask {
        @Override
        protected void onPostExecute(Response response) {
            try {
                String str = response.body().string();
                PlayList playList = gson.fromJson(str, PlayList.class);
                videoItems.addAll(playList.getItems());
                nextPageToken = playList.getNextPageToken();
                Log.d("NextPageToken",nextPageToken);
                endLoadingAnim();
                adapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class LoadNewVideoTask extends GetResponseTask {
        @Override
        protected void onPostExecute(Response response) {
            try {
                String str = response.body().string();
                PlayList playList = gson.fromJson(str, PlayList.class);
                videoItems.addAll(playList.getItems());
                nextPageToken = playList.getNextPageToken();
                endLoadingAnim();
                int itemCount = playList.getItems().size();
                adapter.notifyItemRangeInserted(videoItems.size() - itemCount, itemCount);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
