package com.example.muffin.youtubeapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.fragments.FragmentSubscriptions;
import com.example.muffin.youtubeapp.fragments.VideoListFragment;
import com.example.muffin.youtubeapp.tasks.GetResponseTask;
import com.example.muffin.youtubeapp.utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private final String TAG = "MainActivity";
    private final int RC_AUTH_CODE = 1;
    private final String VIDEO_ID_KEY ="video_id";

    private GoogleApiClient mApiClient;
    private GoogleSignInAccount mAccount;
    private SectionsPagerAdapter mAdapter;


    private String mAccessToken;
    private String mRefreshToken;
    private String tokenType;
    private MenuItem mSignIn;
    private MenuItem mSignOut;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout.Tab [] mTabs = new TabLayout.Tab[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.getNewAccessToken(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .requestScopes(new Scope("https://www.googleapis.com/auth/youtube"))
                .build();

        mApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        for (int i = 0; i < 3; i++) {
            mTabs[i] = tabLayout.getTabAt(i);
        }

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabs[position].select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        mSignIn = menu.findItem(R.id.item_login);
        mSignOut = menu.findItem(R.id.item_logout);
        if(Utils.getStringFromPrefs(this,Utils.ACCESS_TOKEN_PREF).isEmpty()){
            mSignIn.setVisible(true);
            mSignOut.setVisible(false);
        }else{
            mSignIn.setVisible(false);
            mSignOut.setVisible(true);
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
                Auth.GoogleSignInApi.signOut(mApiClient);
                changeSingInItemsVisible();
                Utils.removeFromPrefs(this,Utils.ACCESS_TOKEN_PREF);
                break;
            case R.id.item_refresh:
                Utils.getNewAccessToken(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeSingInItemsVisible(){
        mSignIn.setVisible(!mSignIn.isVisible());
        mSignOut.setVisible(!mSignOut.isVisible());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_AUTH_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                mAccount = result.getSignInAccount();
                String authCode = mAccount.getServerAuthCode();
                changeSingInItemsVisible();
                getAccessToken(authCode);
            }
        }
    }

    /*Send request to get accessToken for user and save it to preferences*/
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
                    mAccessToken = jsonObj.get("access_token").toString();
                    Utils.writeStringToPrefs(MainActivity.this,Utils.ACCESS_TOKEN_PREF, mAccessToken);
                    tokenType = jsonObj.get("token_type").toString();
                    mRefreshToken = jsonObj.get("refresh_token").toString();
                    Utils.writeStringToPrefs(MainActivity.this,Utils.REFRESH_TOKEN_PREF, mRefreshToken);
                    mAdapter.notifyAll();
                } catch (JSONException e) {
                    Log.e(TAG,Log.getStackTraceString(e));
                }

            }
        });
    }

    /*Send intent to authorize user*/
    private void singIn() {
        Intent singInIntent = Auth.GoogleSignInApi.getSignInIntent(mApiClient);

        startActivityForResult(singInIntent,RC_AUTH_CODE);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private VideoListFragment mFragmentPop, mFragmentLiked;


        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    if(mFragmentPop == null){
                        Log.d(TAG,"Create fragment pop!");
                        mFragmentPop = new VideoListFragment();
                        mFragmentPop.setLoadPop(true);
                    }
                    /*VideoListFragment fragment = new VideoListFragment();
                    fragment.setLoadPop(true);*/
                    return mFragmentPop;
                case 1:
                    return new FragmentSubscriptions();
                case 2:
                    if(!Utils.getStringFromPrefs(MainActivity.this,Utils.ACCESS_TOKEN_PREF).isEmpty()){
                        /*VideoListFragment fragment1 = new VideoListFragment();
                        fragment1.setLoadLiked(true);*/
                        if(mFragmentLiked == null) {
                            mFragmentLiked = new VideoListFragment();
                            mFragmentLiked.setLoadLiked(true);
                        }
                        return mFragmentLiked;
                    }else {
                        return ChannelActivity.PlaceholderFragment.newInstance(position);
                    }
                default:
                    return ChannelActivity.PlaceholderFragment.newInstance(position);
            }
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

    }
}
