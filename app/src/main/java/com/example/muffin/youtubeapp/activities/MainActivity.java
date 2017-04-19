package com.example.muffin.youtubeapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.muffin.youtubeapp.GsonModels.PlayList;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.fragments.FragmentVideo;
import com.example.muffin.youtubeapp.fragments.PlayListFragment;
import com.example.muffin.youtubeapp.tasks.GetResponseTask;
import com.example.muffin.youtubeapp.utils.OnVideoSelectedListener;
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

    private FragmentVideo fragmentVideo;
    private PlayListFragment playListFragment;
    private TextView loadMore;
    private CircleProgressBar progressBar;

    private Gson gson = new GsonBuilder().create();

    private PlayList playList;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            //TODO: add actions for tabs
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .requestScopes(new Scope("https://www.googleapis.com/auth/youtube.readonly"))
                .build();

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        fragmentVideo = (FragmentVideo) getFragmentManager().findFragmentById(R.id.fragment_video);
        if(savedInstanceState != null){
            fragmentVideo.setVideoId(savedInstanceState.getString(VIDEO_ID_KEY));
        }
        loadMore = (TextView) findViewById(R.id.btn_load_more);
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewVideo();
            }
        });
        progressBar = (CircleProgressBar)findViewById(R.id.progressBar);
        getPopularList();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(VIDEO_ID_KEY,fragmentVideo.getVideoId());
    }

    private void getPopularList(){
        startLoadingAnim();
        String  urlStr = Uri.parse("https://www.googleapis.com/youtube/v3/videos")
                .buildUpon()
                .appendQueryParameter("key",getString(R.string.youtube_api_key))
                .appendQueryParameter("part","snippet,contentDetails")
                .appendQueryParameter("chart","mostpopular")
                .appendQueryParameter("maxResults","10")
                .build().toString();
        Log.d(TAG,"Url : " + urlStr);
        try {
            URL url = new URL(urlStr);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            new LoadPopularVideoTask().execute(request);
        } catch (MalformedURLException e) {
            Log.d(TAG,"Invalid url :" + urlStr);
            e.printStackTrace();
        }
    }

    private void showPopularFragment(){
        FragmentManager fm = getSupportFragmentManager();
        playListFragment = (PlayListFragment) fm.findFragmentById(R.id.fragment_container);
        if(playListFragment == null){
            playListFragment = PlayListFragment.newInstance(playList);
            fm.beginTransaction()
                    .add(R.id.fragment_container,playListFragment).addToBackStack(null)
                    .commit();
        }
    }

    private void loadNewVideo(){
        startLoadingAnim();
        String  urlStr = Uri.parse("https://www.googleapis.com/youtube/v3/videos")
                .buildUpon()
                .appendQueryParameter("key",getString(R.string.youtube_api_key))
                .appendQueryParameter("part","snippet,contentDetails")
                .appendQueryParameter("chart","mostpopular")
                .appendQueryParameter("maxResults","10")
                .appendQueryParameter("pageToken",playList.getNextPageToken())
                .build().toString();
        Log.d(TAG,"Url : " + urlStr);
        try {
            URL url = new URL(urlStr);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            new LoadNewVideosTask().execute(request);
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);

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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_AUTH_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                account = result.getSignInAccount();
                String authCode = account.getServerAuthCode();
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
                    JSONObject jsonObj = new JSONObject(response.body().string());
                    accessToken = jsonObj.get("access_token").toString();
                    tokenType = jsonObj.get("token_type").toString();
                    refreshToken = jsonObj.get("id_token").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getNewAccessToken(){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("refresh_token",refreshToken)
                .add("client_id", getString(R.string.server_client_id))
                .add("client_secret", getString(R.string.client_secret))
                .add("grant_type", "refresh_token")
                .build();
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObj = new JSONObject(response.body().string());
                    accessToken = jsonObj.get("access_token").toString();
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

    private void startLoadingAnim(){
        progressBar.setVisibility(View.VISIBLE);
        loadMore.setVisibility(View.GONE);
    }

    private void endLoadingAnim(){
        progressBar.setVisibility(View.GONE);
        if(playList.getNextPageToken() != null)
            loadMore.setVisibility(View.VISIBLE);
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onVideoSelected(String videoId) {
        fragmentVideo.setVideoId(videoId);
    }

    private class LoadNewVideosTask extends GetResponseTask {
        @Override
        protected void onPostExecute(Response response) {
            try {
                playList = gson.fromJson(response.body().string(),PlayList.class);
                Log.d(TAG,"Play list size : " + playList.getItems().size());
                if(fragmentVideo.getVideoId() == null)
                    fragmentVideo.setVideoId(playList.getItems().get(0).getId());
                playListFragment.addVideoItems(playList.getItems());
                endLoadingAnim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class LoadPopularVideoTask extends GetResponseTask{
        @Override
        protected void onPostExecute(Response response) {
            try {
                String str = response.body().string();
                playList = gson.fromJson(str,PlayList.class);
                Log.d(TAG,"Play list size : " + playList.getItems().size());
                if(fragmentVideo.getVideoId() == null)
                    fragmentVideo.setVideoId(playList.getItems().get(0).getId());
                showPopularFragment();
                endLoadingAnim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
