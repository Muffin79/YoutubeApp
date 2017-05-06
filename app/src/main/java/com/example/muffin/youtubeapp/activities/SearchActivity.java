package com.example.muffin.youtubeapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.example.muffin.youtubeapp.GsonModels.search.SearchList;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.fragments.FragmentSearch;


public class SearchActivity extends AppCompatActivity {
    private static final String EXTRA_QUERY = "com.example.muffin.youtubeapp.activities.extra_query";
    private final String TAG = "SearchActivity";

    private FragmentSearch mFragment;
    private SearchList mSearchList;
    //private Gson gson = new GsonBuilder().create();

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
        final String q = getIntent().getStringExtra(EXTRA_QUERY);
        mFragment = (FragmentSearch) getSupportFragmentManager().findFragmentById(R.id.search_fragment_container);
        mFragment.loadVideosByQuery(q);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_item_search).getActionView();
        searchView.setQuery(getIntent().getStringExtra(EXTRA_QUERY),false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mFragment.loadVideosByQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return true;
    }
}
