package com.example.muffin.youtubeapp.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.example.muffin.youtubeapp.GsonModels.video.PlayList;
import com.example.muffin.youtubeapp.GsonModels.video.VideoItem;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.adapters.PlayListAdapter;
import com.example.muffin.youtubeapp.tasks.GetResponseTask;
import com.example.muffin.youtubeapp.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoListFragment extends ItemListFragment {

    private static final String SEARCH_API_URL = "https://www.googleapis.com/youtube/v3/videos";
    private static final String ARGS_VIDEO_LIST = "argument_video_list";
    private static final String TAG = "VideoListFragment";


    private int mLastPosition = 0;
    private List<VideoItem> mVideoItems = new ArrayList<>();
    //If set loading popular videos when fragment created.
    private boolean mLoadPop = false;
    //If set loading liked videos when fragment created.
    private boolean mLoadLiked = false;

    public static VideoListFragment newInstance(PlayList playList) {
        VideoListFragment fragment = new VideoListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_VIDEO_LIST, playList);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG,"onCreateView");

        Button retry = (Button) v.findViewById(R.id.raisedRetry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginLoading();
            }
        });

        beginLoading();
        return v;
    }

    /**Loading items if have internet connection.*/
    private void beginLoading() {
        if (Utils.isNetworkAvailableAndConnected(getContext())) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mRetryLayout.setVisibility(View.GONE);
            if (mLoadPop) {
                Log.d(TAG,"Start loading pop");
                loadPopularVideo();
                mLoadPop = false;
            }
            if (mLoadLiked) {
                loadLikedVideos();
                mLoadLiked = false;
            }
        }

    }


    public void setLoadPop(boolean loadPop) {
        this.mLoadPop = loadPop;
    }

    public void setLoadLiked(boolean loadLiked) {
        this.mLoadLiked = loadLiked;
    }

    @Override
    protected RecyclerView.Adapter createAdapter() {
        return new PlayListAdapter(mVideoItems);
    }


    /**Load popular videoItems using {@link LoadVideoItemTask}.*/
    public void loadPopularVideo() {
        startLoadingAnim();
        mUriBuilder = Uri.parse("https://www.googleapis.com/youtube/v3/videos")
                .buildUpon()
                .appendQueryParameter("key", getString(R.string.youtube_api_key))
                .appendQueryParameter("part", "snippet,contentDetails")
                .appendQueryParameter("chart", "mostpopular")
                .appendQueryParameter("regionCode", "ru")
                .appendQueryParameter("maxResults", "50");

            Request request = new Request.Builder()
                    .url(mUriBuilder.build().toString())
                    .build();
            new LoadVideoItemTask().execute(request);
    }

    /**Load liked videoItems using {@link LoadVideoItemTask}.*/
    private void loadLikedVideos() {
        mUriBuilder = Uri.parse("https://www.googleapis.com/youtube/v3/videos")
                .buildUpon()
                .appendQueryParameter("key", getString(R.string.youtube_api_key))
                .appendQueryParameter("part", "snippet,contentDetails")
                .appendQueryParameter("access_token", Utils.getStringFromPrefs(getContext(), Utils.ACCESS_TOKEN_PREF))
                .appendQueryParameter("myRating", "like")
                .appendQueryParameter("maxResults", "10");
        Request request = new Request.Builder()
                .url(mUriBuilder.build().toString())
                .build();
        new LoadVideoItemTask().execute(request);

    }

    @Override
    protected void loadNewVideo() {
        startLoadingAnim();
        String urlStr = mUriBuilder
                .appendQueryParameter("pageToken", mNextPageToken)
                .build().toString();
        Log.d("NextPageToken", urlStr);
        Request request = new Request.Builder()
                .url(urlStr)
                .build();
        new LoadNewVideoTask().execute(request);
    }

    public List<VideoItem> getVideoItems() {
        return mVideoItems;
    }

    public void setVideoItems(List<VideoItem> videoItems) {
        this.mVideoItems = videoItems;
    }

    public void addVideoItems(List<VideoItem> videoItems) {
        mLastPosition = mRecyclerView.getVerticalScrollbarPosition();
        this.mVideoItems.addAll(videoItems);
        mAdapter.notifyItemRangeInserted(this.mVideoItems.size() - videoItems.size(), videoItems.size());
        mRecyclerView.scrollToPosition(mLastPosition);
    }

    /**
     * A {@link GetResponseTask} that get response with list of video items.Items add
     * to list of {@link VideoItem}.
     */
    private class LoadVideoItemTask extends GetResponseTask {
        @Override
        protected void onPostExecute(Response response) {
            try {
                String str = response.body().string();
                PlayList playList = mGson.fromJson(str, PlayList.class);
                mVideoItems.addAll(playList.getItems());
                mNextPageToken = playList.getNextPageToken();
                Log.d("NextPageToken", mNextPageToken);
                endLoadingAnim();
                mAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }
        }
    }

    /**
     * A {@link GetResponseTask} that get response with next list of video items.Items add
     * to list of {@link VideoItem}.
     */
    private class LoadNewVideoTask extends GetResponseTask {
        @Override
        protected void onPostExecute(Response response) {
            try {
                String str = response.body().string();
                PlayList playList = mGson.fromJson(str, PlayList.class);
                mVideoItems.addAll(playList.getItems());
                mNextPageToken = playList.getNextPageToken();
                endLoadingAnim();
                int itemCount = playList.getItems().size();
                mAdapter.notifyItemRangeInserted(mVideoItems.size() - itemCount, itemCount);
            } catch (IOException e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }
        }
    }


}
