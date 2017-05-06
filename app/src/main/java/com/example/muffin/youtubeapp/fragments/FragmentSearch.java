package com.example.muffin.youtubeapp.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muffin.youtubeapp.GsonModels.search.SearchItem;
import com.example.muffin.youtubeapp.GsonModels.search.SearchList;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.adapters.SearchListAdapter;
import com.example.muffin.youtubeapp.tasks.GetResponseTask;
import com.example.muffin.youtubeapp.utils.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;


public class FragmentSearch extends ItemListFragment {
    private final String TAG = "FragmentSearch";
    private final String SEARCH_API_URL = "https://www.googleapis.com/youtube/v3/search";

    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_PLAYLIST = "playlist";

    private static final String ARG_SEARCH_LIST = "search_list";
    private static final String ARG_QUERY = "query";
    private static final String ARG_CHANNEL = "channelId";
    private static final String ARG_TYPE = "type";


    public static FragmentSearch newInstance(SearchList searchList) {
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SEARCH_LIST, searchList);
        fragment.setArguments(args);
        return fragment;
    }


    private List<SearchItem> mSearchItems = new ArrayList<>();
    private boolean mLoadChannel = false;

    public static FragmentSearch newInstance(String channelId, String type) {
        FragmentSearch fragmentSearch = new FragmentSearch();
        Bundle args = new Bundle();
        args.putString(ARG_CHANNEL, channelId);
        args.putString(ARG_TYPE, type);
        fragmentSearch.setIsLoadChannel(true);
        fragmentSearch.setArguments(args);
        return fragmentSearch;
    }

    @Override
    protected RecyclerView.Adapter createAdapter() {
        return new SearchListAdapter(mSearchItems, mCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        if (mLoadChannel) {
            loadChannelContent(getArguments().getString(ARG_CHANNEL),
                    getArguments().getString(ARG_TYPE));
        }
        return v;
    }


    public void loadVideosByQuery(String q) {
        if (!Utils.isNetworkAvailableAndConnected(getContext())) return;
        startLoadingAnim();
        mUriBuilder = Uri.parse(SEARCH_API_URL)
                .buildUpon()
                .appendQueryParameter("key", getString(R.string.youtube_api_key))
                .appendQueryParameter("part", "snippet")
                .appendQueryParameter("q", q)
                .appendQueryParameter("maxResults", "30");

        String urlStr = mUriBuilder.build().toString();
        Request request = new Request.Builder()
                .url(urlStr)
                .build();
        new SearchVideoTask().execute(request);
    }

    public void loadRelatedVideos(String videoId) {
        if (!Utils.isNetworkAvailableAndConnected(getContext())) return;
        startLoadingAnim();
        mUriBuilder = Uri.parse(SEARCH_API_URL)
                .buildUpon()
                .appendQueryParameter("key", getString(R.string.youtube_api_key))
                .appendQueryParameter("part", "snippet")
                .appendQueryParameter("relatedToVideoId", videoId)
                .appendQueryParameter("maxResults", "50");

        String urlStr = mUriBuilder.build().toString();
        Request request = new Request.Builder()
                .url(urlStr)
                .build();
        new SearchVideoTask().execute(request);
    }

    @Override
    protected void loadNewVideo() {
        startLoadingAnim();
        String urlStr = mUriBuilder
                .appendQueryParameter("pageToken", mNextPageToken)
                .build().toString();
        Request request = new Request.Builder()
                .url(urlStr)
                .build();
        new LoadNewVideoTask().execute(request);

    }

    public void setIsLoadChannel(boolean b) {
        mLoadChannel = b;
    }

    public void loadChannelContent(String channelId, String type) {
        Log.d(TAG, "channelVideosLoad");
        startLoadingAnim();
        mUriBuilder = Uri.parse(SEARCH_API_URL)
                .buildUpon()
                .appendQueryParameter("key", getString(R.string.youtube_api_key))
                .appendQueryParameter("part", "snippet")
                .appendQueryParameter("channelId", channelId)
                .appendQueryParameter("maxResults", "50")
                .appendQueryParameter("type", type);

        String urlStr = mUriBuilder.build().toString();
        Request request = new Request.Builder()
                .url(urlStr)
                .build();
        new SearchVideoTask().execute(request);
    }


    public class SearchVideoTask extends GetResponseTask {
        @Override
        protected void onPostExecute(Response response) {
            Log.d(TAG, "Search task executed");
            try {
                String str = response.body().string();
                mSearchItems.clear();
                SearchList list = mGson.fromJson(str, SearchList.class);
                mSearchItems.addAll(list.getItems());
                Log.d(TAG, "List size " + mSearchItems.size());
                mNextPageToken = list.getNextPageToken();
                endLoadingAnim();
                mAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class LoadNewVideoTask extends GetResponseTask {
        @Override
        protected void onPostExecute(Response response) {
            try {
                SearchList list = mGson.fromJson(response.body().string(), SearchList.class);
                mSearchItems.addAll(list.getItems());
                int c = list.getItems().size();
                mNextPageToken = list.getNextPageToken();
                endLoadingAnim();
                mAdapter.notifyItemRangeInserted(mSearchItems.size() - c, c);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
