package com.example.muffin.youtubeapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.fragments.FragmentVideo;

public class PlaylistActivity extends AppCompatActivity {

    private static final String EXTRA_PLAYLIST_ID = "com.example.muffin.youtubeapp.activities.playlistid";

    FragmentVideo mFragmentVideo;

    public static Intent newIntent(Context context,String playListId){
        Intent intent = new Intent(context,PlaylistActivity.class);
        intent.putExtra(EXTRA_PLAYLIST_ID,playListId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        mFragmentVideo = (FragmentVideo) getFragmentManager().findFragmentById(R.id.fragment_video);
        mFragmentVideo.setPlayListId(getIntent().getStringExtra(EXTRA_PLAYLIST_ID));
    }
}
