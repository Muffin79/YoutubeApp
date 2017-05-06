package com.example.muffin.youtubeapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muffin.youtubeapp.GsonModels.video.PlayList;
import com.example.muffin.youtubeapp.GsonModels.video.VideoItem;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.fragments.FragmentVideo;
import com.example.muffin.youtubeapp.tasks.GetResponseTask;
import com.example.muffin.youtubeapp.utils.Utils;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class VideoActivity extends AppCompatActivity {
    private final static String EXTRA_VIDEO_ID =
            "com.example.muffin.youtubeapp.activities.extra_video_id";


    public static Intent newIntent(Context context, String videoID) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(EXTRA_VIDEO_ID, videoID);
        return intent;
    }

    private VideoItem mVideoItem;
    private String mAccessToken;

    private String mVideoId;

    private ImageView mLikeImg;
    private ImageView mDislikeImg;
    private ImageView mShareImg;
    private TextView mPublishTimeTxt;
    private TextView mViewCountTxt;
    private TextView mTitleTxt;
    private TextView mLikeCountTxt;
    private TextView mDislikeCountTxt;
    private TextView mChannelNameTxt;
    private CircleProgressBar mProgressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mVideoId = getIntent().getStringExtra(EXTRA_VIDEO_ID);

        //fragmentSearch = (FragmentSearch) getSupportFragmentManager().findFragmentById(R.id.fragment_search);
        loadVideoInfo();
        if (!Utils.getStringFromPrefs(this, Utils.ACCESS_TOKEN_PREF).isEmpty())
            loadVideoRating();

        /*fragmentSearch.loadRelatedVideos(videoId);*/
        mProgressbar = (CircleProgressBar) findViewById(R.id.video_activity_progressBar);
        mProgressbar.setColorSchemeColors(Color.RED);

        ((FragmentVideo) getFragmentManager()
                .findFragmentById(R.id.fragment_video)).setVideoId(mVideoId);
        initViews();

        mAccessToken = Utils.getStringFromPrefs(this, Utils.ACCESS_TOKEN_PREF);
    }


    private void initViews() {
        mLikeImg = (ImageView) findViewById(R.id.icon_like);
        mDislikeImg = (ImageView) findViewById(R.id.icon_dislike);
        mShareImg = (ImageView) findViewById(R.id.icon_share);
        mShareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String urlStr = Uri.parse("https://www.youtube.com/watch").buildUpon()
                        .appendQueryParameter("v", mVideoId)
                        .build().toString();
                intent.putExtra(Intent.EXTRA_TEXT, urlStr);
                startActivity(intent);
            }
        });
        mViewCountTxt = (TextView) findViewById(R.id.views_txt);
        mTitleTxt = (TextView) findViewById(R.id.title_txt);
        mLikeCountTxt = (TextView) findViewById(R.id.like_count_txt);
        mDislikeCountTxt = (TextView) findViewById(R.id.dislike_count_txt);
        mChannelNameTxt = (TextView) findViewById(R.id.chanel_name_txt);
        mPublishTimeTxt = (TextView) findViewById(R.id.publishedAt_txt);
    }

    private void loadVideoInfo() {
        String urlStr = Uri.parse("https://www.googleapis.com/youtube/v3/videos")
                .buildUpon()
                .appendQueryParameter("key", getString(R.string.youtube_api_key))
                .appendQueryParameter("part", "snippet,contentDetails,statistics")
                .appendQueryParameter("id", mVideoId)
                .appendQueryParameter("maxResults", "1").build().toString();
        Request request = new Request.Builder()
                .url(urlStr)
                .build();
        new LoadVideoInfoTask().execute(request);

    }

    private void loadVideoRating() {
        String accessToken = Utils.getStringFromPrefs(this, Utils.ACCESS_TOKEN_PREF);
        if (accessToken.isEmpty()) return;
        String urlStr = Uri.parse("https://www.googleapis.com/youtube/v3/videos/getRating")
                .buildUpon()
                .appendQueryParameter("key", getString(R.string.youtube_api_key))
                .appendQueryParameter("id", mVideoId)
                .appendQueryParameter("access_token",
                        Utils.getStringFromPrefs(this, Utils.ACCESS_TOKEN_PREF))
                .build().toString();
        Request request = new Request.Builder()
                .url(urlStr)
                .build();
        new GetVideoRating().execute(request);


    }


    private class GetVideoRating extends GetResponseTask {
        @Override
        protected void onPostExecute(Response response) {
            try {
                String str = response.body().string();
                JSONObject jsonObject = new JSONObject(str);
                JSONArray ratingItems = jsonObject.getJSONArray("items");
                JSONObject rating = ratingItems.getJSONObject(0);
                String rateStr = rating.getString("rating");
                if (rateStr.equals(Utils.RATING_LIKE)) {
                    mLikeImg.setImageResource(R.drawable.ic_like_blue_24dp);
                } else if (rateStr.equals(Utils.RATING_DISLIKE)) {
                    mDislikeImg.setImageResource(R.drawable.ic_dislike_blue_24dp);
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class LoadVideoInfoTask extends GetResponseTask {
        @Override
        protected void onPostExecute(Response response) {
            try {
                String str = response.body().string();
                Log.d("FragmentSearch", str);
                PlayList playList = mGson.fromJson(str, PlayList.class);
                mVideoItem = playList.getItems().get(0);
                mTitleTxt.setText(mVideoItem.getSnippet().getTitle());
                mViewCountTxt.setText(getString(R.string.views_count,
                        String.valueOf(mVideoItem.getVideoStatistic().getViewCount())));
                mLikeCountTxt.setText(String.valueOf(mVideoItem.getVideoStatistic().getLikeCount()));
                mDislikeCountTxt.setText(String.valueOf(mVideoItem.getVideoStatistic().getDislikeCount()));
                mChannelNameTxt.setText(mVideoItem.getSnippet().getChannelTitle());
                mProgressbar.setVisibility(View.GONE);
                mPublishTimeTxt.setText(getString(R.string.publish_on,
                        Utils.getFormatedDate(mVideoItem.getSnippet().getPublishTime())));
                findViewById(R.id.info_linear_layout).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.description_txt))
                        .setText(mVideoItem.getSnippet().getDescription());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
