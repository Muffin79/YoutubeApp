package com.example.muffin.youtubeapp.fragments;

import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.muffin.youtubeapp.GsonModels.SearchItem;
import com.example.muffin.youtubeapp.GsonModels.SearchList;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.activities.SearchActivity;
import com.example.muffin.youtubeapp.adapters.SearchListAdapter;
import com.example.muffin.youtubeapp.tasks.GetResponseTask;
import com.example.muffin.youtubeapp.utils.OnVideoSelectedListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;


public class FragmentSearch extends VideoListFragment {
    private final String TAG = "FragmentSearch";

    private static final String ARG_SEARCH_LIST = "search_list";
    private static final String ARG_QUERY = "query";

    public static FragmentSearch newInstance(SearchList searchList){
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SEARCH_LIST,searchList);
        fragment.setArguments(args);
        return fragment;
    }

    public static FragmentSearch newInstance(String q){
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY,q);
        fragment.setArguments(args);
        return fragment;
    }

    private List<SearchItem> searchItems = new ArrayList<>();
/*
    private SearchListAdapter adapter;
    private OnVideoSelectedListener callback;
    private RecyclerView recyclerView;
    private TextView loadMoreTxt;
    private CircleProgressBar progressBar;
    private Gson gson = new GsonBuilder().create();

    private String nextPageToken;

    private Uri.Builder uriBuilder;*/

    @Override
    protected RecyclerView.Adapter createAdapter() {
        return new SearchListAdapter(searchItems,callback);
    }
/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_play_list, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.play_list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SearchListAdapter(searchItems,callback);
        recyclerView.setAdapter(adapter);

        loadMoreTxt = (TextView) v.findViewById(R.id.txt_load_more);
        progressBar = (CircleProgressBar) v.findViewById(R.id.progressBar);

        progressBar.setColorSchemeColors(Color.RED);
        Log.d(TAG,"fragment created");
        Log.d(TAG,"List size " + searchItems.size());
        return v;
    }*/

    public void loadVideosByQuery(String q){
         startLoadingAnim();
         uriBuilder = Uri.parse("https://www.googleapis.com/youtube/v3/search")
                .buildUpon()
                .appendQueryParameter("key",getString(R.string.youtube_api_key))
                .appendQueryParameter("part","snippet")
                .appendQueryParameter("q",q)
                .appendQueryParameter("maxResults","30");

        String  urlStr = uriBuilder.build().toString();
        try {
            URL url = new URL(urlStr);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            new SearchVideoTask().execute(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void loadRelatedVideos(String videoId){
        startLoadingAnim();
        uriBuilder = Uri.parse("https://www.googleapis.com/youtube/v3/search")
                .buildUpon()
                .appendQueryParameter("key",getString(R.string.youtube_api_key))
                .appendQueryParameter("part","snippet")
                .appendQueryParameter("relatedToVideoId",videoId)
                .appendQueryParameter("maxResults","50");

        String  urlStr = uriBuilder.build().toString();
        try {
            URL url = new URL(urlStr);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            new SearchVideoTask().execute(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadNewVideo(){
        String urlStr = uriBuilder
                .appendQueryParameter("pageToken",nextPageToken)
                .build().toString();
        try {
            URL url = new URL(urlStr);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            new SearchVideoTask().execute(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    public class SearchVideoTask extends GetResponseTask {
        @Override
        protected void onPostExecute(Response response) {
            Log.d(TAG,"Search task executed");
            try {
                searchItems.clear();
                SearchList list = gson.fromJson(response.body().string(), SearchList.class);
                searchItems.addAll(list.getItems());
                Log.d(TAG,"List size " + searchItems.size());
                nextPageToken = list.getNextPageToken();
                endLoadingAnim();
                //progressBar.setVisibility(View.GONE);
                Log.d(TAG,"List size : " + searchItems.size());
                adapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class  LoadNewVideoTask extends GetResponseTask{
        @Override
        protected void onPostExecute(Response response) {
            try {
                SearchList list = gson.fromJson(response.body().string(),SearchList.class);
                searchItems.addAll(list.getItems());
                adapter.notifyItemRangeInserted(searchItems.size() - 10,10);
                nextPageToken = list.getNextPageToken();
                endLoadingAnim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
