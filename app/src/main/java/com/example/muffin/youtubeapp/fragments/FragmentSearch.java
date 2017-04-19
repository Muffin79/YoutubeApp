package com.example.muffin.youtubeapp.fragments;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muffin.youtubeapp.GsonModels.SearchItem;
import com.example.muffin.youtubeapp.GsonModels.SearchList;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.adapters.SearchListAdapter;
import com.example.muffin.youtubeapp.utils.OnVideoSelectedListener;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentSearch extends Fragment {
    private final String TAG = "FragmentSearch";

    private static final String ARG_SEARCH_LIST = "search_list";

    public static FragmentSearch newInstance(SearchList searchList){
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SEARCH_LIST,searchList);
        fragment.setArguments(args);
        return fragment;
    }


    private List<SearchItem> searchItems;
    private SearchListAdapter adapter;
    private OnVideoSelectedListener callback;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.search_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchItems = ((SearchList)getArguments().getSerializable(ARG_SEARCH_LIST)).getItems();
        adapter = new SearchListAdapter(searchItems,callback);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Log.d(TAG,"fragment created");
        Log.d(TAG,"List size " + searchItems.size());
        return v;
    }
}
