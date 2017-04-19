package com.example.muffin.youtubeapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.muffin.youtubeapp.GsonModels.ContentDetails;
import com.example.muffin.youtubeapp.GsonModels.SearchList;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.fragments.FragmentSearch;
import com.example.muffin.youtubeapp.tasks.GetResponseTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    private static final String EXTRA_QUERY = "com.example.muffin.youtubeapp.activities.extra_query";
    private final String TAG = "SearchActivity";

    private CircleProgressBar progressBar;
    private FragmentSearch fragment;
    private SearchList searchList;
    private Gson gson = new GsonBuilder().create();

    public static Intent newIntent(Context context,String q){
        Intent intent = new Intent(context,SearchActivity.class);
        intent.putExtra(EXTRA_QUERY,q);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = (CircleProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        loadSearchVideo(getIntent().getStringExtra(EXTRA_QUERY));
    }

    private void loadSearchVideo(String q){
        String  urlStr = Uri.parse("https://www.googleapis.com/youtube/v3/search")
                .buildUpon()
                .appendQueryParameter("key",getString(R.string.youtube_api_key))
                .appendQueryParameter("part","snippet")
                .appendQueryParameter("q",q)
                .appendQueryParameter("maxResults","10")
                .build().toString();
        URL url = null;
        try {
            url = new URL(urlStr);
            Request request = new Request.Builder()
                .url(url)
                .build();
            new SearchVideoTask().execute(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private class SearchVideoTask extends GetResponseTask {
        @Override
        protected void onPostExecute(Response response) {
            try {
                searchList = gson.fromJson(response.body().string(), SearchList.class);
                progressBar.setVisibility(View.GONE);
                Log.d(TAG,"List size : " + searchList.getItems().size());
                FragmentManager fm = getSupportFragmentManager();
                fragment = FragmentSearch.newInstance(searchList);
                fm.beginTransaction().replace(R.id.search_fragment_container,fragment).commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
