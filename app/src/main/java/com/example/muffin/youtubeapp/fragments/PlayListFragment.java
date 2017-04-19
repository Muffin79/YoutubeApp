package com.example.muffin.youtubeapp.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.muffin.youtubeapp.GsonModels.PlayList;
import com.example.muffin.youtubeapp.GsonModels.VideoItem;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.adapters.PlayListAdapter;
import com.example.muffin.youtubeapp.utils.OnVideoSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayListFragment extends Fragment {

    private static final String ARGS_VIDEO_LIST = "argument_video_list";

    private RecyclerView playListRecycler;
    private PlayListAdapter adapter;
    private OnVideoSelectedListener callback;
    int lastPosition = 0;


    private List<VideoItem> videoItems = new ArrayList<>();

    public static PlayListFragment newInstance(PlayList playList){
        PlayListFragment fragment = new PlayListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_VIDEO_LIST,playList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_play_list, container, false);
        playListRecycler = (RecyclerView) v.findViewById(R.id.play_list_recyclerView);
        playListRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        PlayList playList = (PlayList)getArguments().getSerializable(ARGS_VIDEO_LIST);
        videoItems = playList.getItems();


        adapter = new PlayListAdapter(videoItems,callback);
        playListRecycler.setAdapter(adapter);


        return v;
    }





    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            callback = (OnVideoSelectedListener) activity;

        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + " must implement OnVideoSelectedListener");
        }
    }

    public void addVideoItems(List<VideoItem> videoItems) {
        lastPosition = playListRecycler.getVerticalScrollbarPosition();
        this.videoItems.addAll(videoItems);
        adapter.notifyDataSetChanged();
        playListRecycler.scrollToPosition(lastPosition);
    }



}
