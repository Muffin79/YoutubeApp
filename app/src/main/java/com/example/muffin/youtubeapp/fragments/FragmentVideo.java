package com.example.muffin.youtubeapp.fragments;

import android.os.Bundle;

import com.example.muffin.youtubeapp.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

public class FragmentVideo extends YouTubePlayerFragment implements
        YouTubePlayer.OnInitializedListener{

    private YouTubePlayer mPlayer;
    private String mVideoId;
    private String mPlayListId;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        //initialize("AIzaSyB__E_VxHmsJ5qw1kvbjLpiEwXvspCJCFI",this);
        initialize(getString(R.string.youtube_api_key),this);

    }

    @Override
    public void onDestroy() {
        if(mPlayer != null){
            mPlayer.release();
        }
        super.onDestroy();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        mPlayer = youTubePlayer;

        if(!b && mVideoId != null){
            mPlayer.cueVideo(mVideoId);
        }

        if(!b && mPlayListId != null){
            mPlayer.cuePlaylist(mPlayListId);
        }


        mPlayer.play();
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        mPlayer = null;
    }

    public void setVideoId(String videoId) {
        if (videoId != null && !videoId.equals(this.mVideoId)) {
            this.mVideoId = videoId;
            if (mPlayer != null) {
                mPlayer.cueVideo(videoId);
            }
        }
    }

    public void setPlayListId(String playListId) {
        if(playListId != null && !playListId.equals(this.mPlayListId)) {
            this.mPlayListId = playListId;
            if(mPlayer != null){
                mPlayer.cuePlaylist(playListId);
            }
        }
    }

    public String getVideoId() {
        return mVideoId;
    }
}
