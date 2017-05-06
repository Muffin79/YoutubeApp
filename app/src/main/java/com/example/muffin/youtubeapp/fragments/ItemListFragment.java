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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.utils.OnVideoSelectedListener;
import com.example.muffin.youtubeapp.utils.Utils;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;


public abstract class ItemListFragment extends Fragment{
    private final String TAG = "ItemListFragment";

    protected RecyclerView.Adapter mAdapter;
    protected OnVideoSelectedListener mCallback;
    protected RecyclerView mRecyclerView;
    protected TextView mLoadMoreTxt;
    protected CircleProgressBar mProgressBar;
    protected LinearLayout mRetryLayout;

    protected String mNextPageToken = "";

    protected Uri.Builder mUriBuilder;

    protected abstract RecyclerView.Adapter createAdapter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_play_list, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.play_list_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d(TAG,"Fragment create view");
        mAdapter = createAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mLoadMoreTxt = (TextView) v.findViewById(R.id.txt_load_more);
        mLoadMoreTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewVideo();
            }
        });
        mProgressBar = (CircleProgressBar) v.findViewById(R.id.progressBar);

        mProgressBar.setColorSchemeColors(Color.RED);

        mRetryLayout = (LinearLayout) v.findViewById(R.id.lytRetry);

        if(!Utils.isNetworkAvailableAndConnected(getContext())){
            mRetryLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
        }


        return v;
    }

    protected void startLoadingAnim() {
        Log.d(TAG,"start loading anim");
        mLoadMoreTxt.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    protected void endLoadingAnim() {
        if(mNextPageToken != null)
        mLoadMoreTxt.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    protected abstract void loadNewVideo();
}
