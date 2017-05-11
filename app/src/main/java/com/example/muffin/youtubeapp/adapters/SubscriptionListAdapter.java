package com.example.muffin.youtubeapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muffin.youtubeapp.GsonModels.channel.ChannelItem;
import com.example.muffin.youtubeapp.GsonModels.subscriptions.SubscriptionItem;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.activities.ChannelActivity;
import com.example.muffin.youtubeapp.utils.Utils;

import java.util.List;


public class SubscriptionListAdapter extends RecyclerView.Adapter<SubscriptionListAdapter.ChannelItemHolder>{

    List<SubscriptionItem> mItems;

    public SubscriptionListAdapter(List<SubscriptionItem> items) {
        mItems = items;
    }

    @Override
    public ChannelItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                       .inflate(R.layout.video_list_item,parent,false);
        return new ChannelItemHolder(v,parent.getContext());
    }

    @Override
    public void onBindViewHolder(ChannelItemHolder holder, int position) {
        holder.bindHolder(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ChannelItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Context mContext;
        private SubscriptionItem mItem;
        private ImageView mImgThumbnail;
        private TextView mTxtTitle, mTxtPublishAt;

        public ChannelItemHolder(View itemView,Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            mContext = context;
            mImgThumbnail = (ImageView)itemView.findViewById(R.id.imgThumbnail);
            mTxtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            mTxtPublishAt = (TextView) itemView.findViewById(R.id.txtPublishedAt);
        }

        /**Bind UI components of holder*/
        void bindHolder(SubscriptionItem item){
            this.mItem = item;
            mTxtTitle.setText(item.getSnippet().getTitle());
            mTxtPublishAt.setText(Utils.formatPublishedDate(mContext,
                    item.getSnippet().getPublishTime()));

            Glide.with(mContext)
                    .load(item.getSnippet().getThumbnails().getHighThumbnailUrl())
                    .placeholder(R.drawable.loading_spinner)
                    .into(mImgThumbnail);
        }


        @Override
        public void onClick(View v) {
            mContext.startActivity(ChannelActivity.newIntent(mContext,
                                mItem.getSnippet().getChannelId(),mItem.getSnippet().getTitle()));
        }
    }
}
