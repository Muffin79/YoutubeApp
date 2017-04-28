package com.example.muffin.youtubeapp.fragments;

import android.os.Bundle;

import com.example.muffin.youtubeapp.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

public class FragmentVideo extends YouTubePlayerFragment implements
        YouTubePlayer.OnInitializedListener{

    private YouTubePlayer player;
    private String videoId;
    private String playListId;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        //initialize("AIzaSyB__E_VxHmsJ5qw1kvbjLpiEwXvspCJCFI",this);
        initialize(getString(R.string.youtube_api_key),this);

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

        if(!b && playListId != null){
            player.cuePlaylist(playListId);
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

    public void setPlayListId(String playListId) {
        if(playListId != null && !playListId.equals(this.playListId)) {
            this.playListId = playListId;
            if(player != null){
                player.cuePlaylist(playListId);
            }
        }
    }

    public String getVideoId() {
        return videoId;
    }
}
