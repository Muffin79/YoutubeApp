package com.example.muffin.youtubeapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muffin.youtubeapp.GsonModels.VideoItem;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.fragments.FragmentSearch;
import com.example.muffin.youtubeapp.fragments.FragmentVideo;

import org.w3c.dom.Text;

public class VideoActivity extends AppCompatActivity {
    private final static String EXTRA_VIDEO_ID =
            "com.example.muffin.youtubeapp.activities.extra_video_id";


    public static Intent newIntent(Context context,String videoID){
        Intent intent = new Intent(context,VideoActivity.class);
        intent.putExtra(EXTRA_VIDEO_ID,videoID);
        return intent;
    }

    private FragmentManager fm;
    private VideoItem video;
    private String videoId;
    private FragmentSearch fragmentSearch;
    private ImageView likeImg;
    private ImageView dislikeImg;
    private ImageView shareImg;
    private TextView viewCountTxt;
    private TextView titleTxt;
    private TextView likeCountTxt;
    private TextView dislikeCountTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoId = getIntent().getStringExtra(EXTRA_VIDEO_ID);

        fragmentSearch = new FragmentSearch();
        fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.search_fragment_container,fragmentSearch).commitNow();

        fragmentSearch.loadRelatedVideos(videoId);
        ((FragmentVideo)getFragmentManager()
                .findFragmentById(R.id.fragment_video)).setVideoId(videoId);
        initViews();


    }



    private void initViews(){
        likeImg = (ImageView) findViewById(R.id.icon_like);
        dislikeImg = (ImageView) findViewById(R.id.icon_dislike);
        shareImg = (ImageView) findViewById(R.id.icon_share);
        viewCountTxt = (TextView) findViewById(R.id.views_txt);
        titleTxt = (TextView) findViewById(R.id.title_txt);
        likeCountTxt = (TextView) findViewById(R.id.like_count_txt);
        dislikeCountTxt = (TextView) findViewById(R.id.dislike_count_txt);
    }


}
