package com.example.muffin.youtubeapp.fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.adapters.SearchListAdapter;
import com.example.muffin.youtubeapp.utils.OnVideoSelectedListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;


public abstract class VideoListFragment extends Fragment{
    private final String TAG = "VideoListFragment";

    protected RecyclerView.Adapter adapter;
    protected OnVideoSelectedListener callback;
    protected RecyclerView recyclerView;
    protected TextView loadMoreTxt;
    protected CircleProgressBar progressBar;
    protected Gson gson = new GsonBuilder().create();

    protected String nextPageToken = "";

    protected Uri.Builder uriBuilder;

    protected abstract RecyclerView.Adapter createAdapter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_play_list, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.play_list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d(TAG,"Fragment create view");
        adapter = createAdapter();
        recyclerView.setAdapter(adapter);

        loadMoreTxt = (TextView) v.findViewById(R.id.txt_load_more);
        loadMoreTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewVideo();
            }
        });
        progressBar = (CircleProgressBar) v.findViewById(R.id.progressBar);

        progressBar.setColorSchemeColors(Color.RED);

        return v;
    }

    protected void startLoadingAnim() {
        Log.d(TAG,"start loading anim");
        loadMoreTxt.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void endLoadingAnim() {
        if(!nextPageToken.isEmpty())
            loadMoreTxt.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    protected abstract void loadNewVideo();
}
