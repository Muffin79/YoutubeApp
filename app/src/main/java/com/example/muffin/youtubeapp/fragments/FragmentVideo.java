package com.example.muffin.youtubeapp.fragments;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;



public class FragmentVideo extends YouTubePlayerFragment implements
        YouTubePlayer.OnInitializedListener{

    private YouTubePlayer player;
    private String videoId;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initialize("AIzaSyB__E_VxHmsJ5qw1kvbjLpiEwXvspCJCFI",this);
    }

    @Override
    public void onDestroy() {
        if(player != null){
            player.release();
        }
        super.onDestroy();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        player = youTubePlayer;

        if(!b && videoId != null){
            player.cueVideo(videoId);
        }
        player.play();
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        player = null;
    }

    public void setVideoId(String videoId) {
        if (videoId != null && !videoId.equals(this.videoId)) {
            this.videoId = videoId;
            if (player != null) {
                player.cueVideo(videoId);
            }
        }
    }

    public String getVideoId() {
        return videoId;
    }
}
