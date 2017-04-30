package com.example.muffin.youtubeapp.activities;

import android.accounts.Account;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.fragments.PlayListFragment;
import com.example.muffin.youtubeapp.tasks.GetResponseTask;
import com.example.muffin.youtubeapp.utils.OnVideoSelectedListener;
import com.example.muffin.youtubeapp.utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        OnVideoSelectedListener {


    private final String TAG = "MainActivity";
    private final int RC_AUTH_CODE = 1;
    private final String VIDEO_ID_KEY ="video_id";

    private GoogleApiClient apiClient;
    private GoogleSignInAccount account;
    private SectionsPagerAdapter adapter;

    private ViewPager viewPager;



    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private MenuItem signIn;
    private MenuItem signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .requestScopes(new Scope("https://www.googleapis.com/auth/youtube"))
                .build();

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        viewPager = (ViewPager)findViewById(R.id.main_viewPager);
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);



    }


    private void getRecommendedVideos(){
        String  urlStr = Uri.parse("https://www.googleapis.com/youtube/v3/activities")
                .buildUpon()
                .appendQueryParameter("access_token",accessToken)
                .appendQueryParameter("mine","true")
                .appendQueryParameter("part","snippet,contentDetails")
                .appendQueryParameter("maxResults","10")
                .appendQueryParameter("key",getString(R.string.youtube_api_key))
                .build().toString();
        try {
            URL url = new URL(urlStr);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            new GetResponseTask().execute(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        signIn = menu.findItem(R.id.item_login);
        signOut = menu.findItem(R.id.item_logout);
        if(Utils.getStringFromPrefs(this,Utils.ACCESS_TOKEN_PREF).isEmpty()){
            signIn.setVisible(true);
            signOut.setVisible(false);
        }else{
            signIn.setVisible(false);
            signOut.setVisible(true);
        }

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_item_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startActivity(SearchActivity.newIntent(MainActivity.this,query));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_login:
                singIn();
                //item.setVisible(false);
                break;
            case R.id.item_logout:
                Auth.GoogleSignInApi.signOut(apiClient);
                changeSingInItemsVisible();
                Utils.removeFromPrefs(this,Utils.ACCESS_TOKEN_PREF);
                break;
            case R.id.item_refresh:
                Utils.getNewAccessToken(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeSingInItemsVisible(){
        signIn.setVisible(!signIn.isVisible());
        signOut.setVisible(!signOut.isVisible());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_AUTH_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                account = result.getSignInAccount();
                String authCode = account.getServerAuthCode();
                changeSingInItemsVisible();
                getAccessToken(authCode);
            }
        }
    }

    private void getAccessToken(String authCode){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("client_id", getString(R.string.server_client_id))
                .add("client_secret", getString(R.string.client_secret))
                .add("code", authCode)
                .build();
        Log.d(TAG,"Request body : " + requestBody.toString());
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();
        Log.d(TAG,"Request : " + request.url().toString());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String str = response.body().string();
                    JSONObject jsonObj = new JSONObject(str);
                    Log.d("access_token",str);
                    accessToken = jsonObj.get("access_token").toString();
                    Utils.writeStringToPrefs(MainActivity.this,Utils.ACCESS_TOKEN_PREF,accessToken);
                    tokenType = jsonObj.get("token_type").toString();
                    refreshToken = jsonObj.get("refresh_token").toString();
                    Utils.writeStringToPrefs(MainActivity.this,Utils.REFRESH_TOKEN_PREF,refreshToken);
                    adapter.notifyAll();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }





    private void singIn() {
        Intent singInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);

        startActivityForResult(singInIntent,RC_AUTH_CODE);
    }




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onVideoSelected(String videoId) {
        Intent intent = VideoActivity.newIntent(this,videoId);
        startActivity(intent);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter{

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    PlayListFragment fragment = new PlayListFragment();
                    fragment.setLoadPop(true);
                    return fragment;
                case 1:
                    if(!Utils.getStringFromPrefs(MainActivity.this,Utils.ACCESS_TOKEN_PREF).isEmpty()){
                        PlayListFragment fragment1 = new PlayListFragment();
                        fragment1.setLoadLiked(true);
                        return fragment1;
                    }else {
                        return ChannelActivity.PlaceholderFragment.newInstance(position);
                    }
                case 2:
                    return ChannelActivity.PlaceholderFragment.newInstance(position);
                default:
                    return ChannelActivity.PlaceholderFragment.newInstance(position);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Popular";
                case 1:
                    return "Liked";
                case 2:
                    return "Account";
            }
            return super.getPageTitle(position);
        }

        @Override
        public int getCount() {
            return 3;
        }





    }

}
