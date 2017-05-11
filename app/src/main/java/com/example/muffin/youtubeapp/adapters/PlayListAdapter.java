package com.example.muffin.youtubeapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muffin.youtubeapp.GsonModels.video.VideoItem;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.activities.VideoActivity;
import com.example.muffin.youtubeapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link RecyclerView.Adapter} using for create list items, for youtube playList
 */
public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {

    private final static String TAG = "PlayListAdapter";


    private List<VideoItem> mItems = new ArrayList<>();
    private int mLastPosition = 5;

    public PlayListAdapter(List<VideoItem> items){
        this.mItems = items;
    }

    @Override
    public PlayListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_item,parent,false);
        return new ViewHolder(v,parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindHolder(mItems.get(position));

        if(position > mLastPosition){
        }
    }



    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * A {@link ViewHolder} that contains info about {@link VideoItem}
     * */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Context mContext;
        private VideoItem mItem;
        private ImageView mImgThumbnail;
        private TextView mTxtTitle, mTxtDuration, mTxtPublishAt, mTxtChannelName;


        ViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.mContext = context;
            mImgThumbnail = (ImageView)itemView.findViewById(R.id.imgThumbnail);
            mTxtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            mTxtDuration = (TextView) itemView.findViewById(R.id.txtDuration);
            mTxtPublishAt = (TextView) itemView.findViewById(R.id.txtPublishedAt);
            mTxtChannelName = (TextView) itemView.findViewById(R.id.txtChannelName);
        }

        /**Bind UI components of holder*/
        void bindHolder(VideoItem item){
            this.mItem = item;
            Log.d(TAG,"Title : " + item.getSnippet().getTitle());
            mTxtTitle.setText(item.getSnippet().getTitle());
            mTxtDuration.setText(Utils.getTimeFromString(item.getContentDetails().getDuration()));
            mTxtPublishAt.setText(Utils.formatPublishedDate(mContext,
                                                item.getSnippet().getPublishTime()));
            mTxtChannelName.setText(item.getSnippet().getChannelTitle());

            Glide.with(mContext)
                    .load(item.getSnippet().getThumbnails().getHighThumbnailUrl())
                    .placeholder(R.drawable.loading_spinner)
                    .into(mImgThumbnail);
        }

        @Override
        public void onClick(View v) {
            mContext.startActivity(VideoActivity.newIntent(mContext,mItem.getId()));
        }
    }
}
