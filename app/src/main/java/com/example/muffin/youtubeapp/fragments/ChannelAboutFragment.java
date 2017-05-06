package com.example.muffin.youtubeapp.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.muffin.youtubeapp.GsonModels.channel.ChannelItem;
import com.example.muffin.youtubeapp.GsonModels.channel.ChannelList;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.tasks.GetResponseTask;
import com.example.muffin.youtubeapp.utils.Utils;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass that contains info
 * about channel
 */
public class ChannelAboutFragment extends Fragment {

    private static String KEY_CHANNEL_ID = "channel_ID";


    private ChannelItem mChannelItem;
    private TextView mTxtDescription, mTxtJoined, mTxtViewCount, mTxtSubscribersCount;

    public static ChannelAboutFragment newInstance(String id){
        Bundle args = new Bundle();
        args.putString(KEY_CHANNEL_ID,id);
        ChannelAboutFragment fragment = new ChannelAboutFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_channel_about, container, false);

        mTxtDescription = (TextView)v.findViewById(R.id.description_txt);
        mTxtJoined = (TextView) v.findViewById(R.id.txt_joined);
        mTxtViewCount = (TextView) v.findViewById(R.id.txt_view_count);
        mTxtSubscribersCount = (TextView) v.findViewById(R.id.txt_subsribers_count);

        String channelId= getArguments().getString(KEY_CHANNEL_ID);

        loadChannelInfo(channelId);

        return v;
    }

    private void loadChannelInfo(String channelId){
        String urlStr = Uri.parse(Utils.CHANNEL_API_URL)
                .buildUpon()
                .appendQueryParameter("key",getString(R.string.youtube_api_key))
                .appendQueryParameter("part","snippet,statistics")
                .appendQueryParameter("id",channelId)
                .appendQueryParameter("maxResults","1")
                .build().toString();
        Request request = new Request.Builder()
                .url(urlStr)
                .build();
        new GetChannelInfoTask().execute(request);
    }

    private class GetChannelInfoTask extends GetResponseTask {
        @Override
        protected void onPostExecute(Response response) {
            try {
                String str = response.body().string();
                Log.d("ChannelActivity",str);
                ChannelList list = mGson.fromJson(str,ChannelList.class);
                Log.d("ChannelActivity","List size " + list.getItems().size());
                mChannelItem = list.getItems().get(0);
                //txtDescription.setText(channelItem.getSnippet().getDescription());
                mTxtJoined.setText(getString(R.string.joined,
                        Utils.getFormatedDate(mChannelItem.getSnippet().getPublishTime())));
                mTxtViewCount.setText(getString(R.string.views_count,
                        String.valueOf(mChannelItem.getStatistic().getViewCount())));
                mTxtSubscribersCount.setText(getString(R.string.subscribers_count,
                        String.valueOf(mChannelItem.getStatistic().getSubscriberCount())));
            } catch (IOException e) {
                //Log.d(TAG,"Response is not valid");
            }catch(NullPointerException e){
                Log.d("ChannelActivity",Log.getStackTraceString(e));

            }
        }
    }

}
