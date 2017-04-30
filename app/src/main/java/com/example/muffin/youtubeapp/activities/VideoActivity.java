package com.example.muffin.youtubeapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muffin.youtubeapp.GsonModels.PlayList;
import com.example.muffin.youtubeapp.GsonModels.VideoItem;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.fragments.FragmentSearch;
import com.example.muffin.youtubeapp.fragments.FragmentVideo;
import com.example.muffin.youtubeapp.tasks.GetResponseTask;
import com.example.muffin.youtubeapp.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Request;
import okhttp3.Response;

public class VideoActivity extends AppCompatActivity {
    private final static String EXTRA_VIDEO_ID =
            "com.example.muffin.youtubeapp.activities.extra_video_id";


    public static Intent newIntent(Context context,String videoID){
        Intent intent = new Intent(context,VideoActivity.class);
        intent.putExtra(EXTRA_VIDEO_ID,videoID);
        return intent;
    }

    private VideoItem videoItem;
    private String accessToken;

    //Gson gson = new GsonBuilder().create();

    private FragmentManager fm;
    private VideoItem video;
    private String videoId;
    //private FragmentSearch fragmentSearch;

    private ImageView likeImg;
    private ImageView dislikeImg;
    private ImageView shareImg;
    private TextView publishTimeTxt;
    private TextView viewCountTxt;
    private TextView titleTxt;
    private TextView likeCountTxt;
    private TextView dislikeCountTxt;
    private TextView channelNameTxt;
    private CircleProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoId = getIntent().getStringExtra(EXTRA_VIDEO_ID);

        //fragmentSearch = (FragmentSearch) getSupportFragmentManager().findFragmentById(R.id.fragment_search);
        loadVideoInfo();
        loadVideoRating();

        /*fragmentSearch.loadRelatedVideos(videoId);*/
        progressBar = (CircleProgressBar)findViewById(R.id.video_activity_progressBar);
        progressBar.setColorSchemeColors(Color.RED);

        ((FragmentVideo)getFragmentManager()
                .findFragmentById(R.id.fragment_video)).setVideoId(videoId);
        initViews();

        accessToken = Utils.getStringFromPrefs(this,Utils.ACCESS_TOKEN_PREF);
    }



    private void initViews(){
        likeImg = (ImageView) findViewById(R.id.icon_like);
        dislikeImg = (ImageView) findViewById(R.id.icon_dislike);
        shareImg = (ImageView) findViewById(R.id.icon_share);
        shareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String urlStr = Uri.parse("https://www.youtube.com/watch").buildUpon()
                        .appendQueryParameter("v",videoId)
                        .build().toString();
                intent.putExtra(Intent.EXTRA_TEXT,urlStr);
                startActivity(intent);
            }
        });
        viewCountTxt = (TextView) findViewById(R.id.views_txt);
        titleTxt = (TextView) findViewById(R.id.title_txt);
        likeCountTxt = (TextView) findViewById(R.id.like_count_txt);
        dislikeCountTxt = (TextView) findViewById(R.id.dislike_count_txt);
        channelNameTxt = (TextView) findViewById(R.id.chanel_name_txt);
        publishTimeTxt = (TextView) findViewById(R.id.publishedAt_txt);
    }

    private void loadVideoInfo(){
        String urlStr = Uri.parse("https://www.googleapis.com/youtube/v3/videos")
                .buildUpon()
                .appendQueryParameter("key", getString(R.string.youtube_api_key))
                .appendQueryParameter("part", "snippet,contentDetails,statistics")
                .appendQueryParameter("id", videoId)
                .appendQueryParameter("maxResults", "1").build().toString();
        try {
            URL url = new URL(urlStr);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            new LoadVideoInfoTask().execute(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void loadVideoRating(){
        String accessToken = Utils.getStringFromPrefs(this,Utils.ACCESS_TOKEN_PREF);
        if(accessToken.isEmpty()) return;
        String urlStr = Uri.parse("https://www.googleapis.com/youtube/v3/videos/getRating")
                .buildUpon()
                .appendQueryParameter("key", getString(R.string.youtube_api_key))
                .appendQueryParameter("id", videoId)
                .build().toString();


    }


    private class GetVideoRating extends GetResponseTask{
        @Override
        protected void onPostExecute(Response response) {
            try {
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONArray ratingItems = jsonObject.getJSONArray("items");
                JSONObject rating = ratingItems.getJSONObject(0);
                String rateStr = rating.getString("rating");
                if(rateStr.equals(Utils.RATING_LIKE)){
                    likeImg.setImageResource(R.drawable.ic_like_blue_24dp);
                }else if(rateStr.equals(Utils.RATING_DISLIKE)){
                    dislikeImg.setImageResource(R.drawable.ic_dislike_blue_24dp);
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class LoadVideoInfoTask extends GetResponseTask{
        @Override
        protected void onPostExecute(Response response) {
            try {
                String str = response.body().string();
                Log.d("FragmentSearch",str);
                PlayList playList = gson.fromJson(str,PlayList.class);
                videoItem = playList.getItems().get(0);
                titleTxt.setText(videoItem.getSnippet().getTitle());
                viewCountTxt.setText(getString(R.string.views_count,
                        String.valueOf(videoItem.getViewCount())));
                likeCountTxt.setText(String.valueOf(videoItem.getLikeCount()));
                dislikeCountTxt.setText(String.valueOf(videoItem.getDislikeCount()));
                channelNameTxt.setText(videoItem.getSnippet().getChannelTitle());
                progressBar.setVisibility(View.GONE);
                publishTimeTxt.setText(getString(R.string.publish_on,
                        Utils.getFormatedDate(videoItem.getSnippet().getPublishTime())));
                findViewById(R.id.info_linear_layout).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.description_txt))
                        .setText(videoItem.getSnippet().getDescription());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
