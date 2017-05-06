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
import com.example.muffin.youtubeapp.GsonModels.search.SearchItem;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.activities.ChannelActivity;
import com.example.muffin.youtubeapp.activities.PlaylistActivity;
import com.example.muffin.youtubeapp.activities.VideoActivity;
import com.example.muffin.youtubeapp.utils.OnVideoSelectedListener;
import com.example.muffin.youtubeapp.utils.Utils;

import java.util.List;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchItemHolder>{
    private final String TAG = "SearchListAdapter";
    private List<SearchItem> items;
    private OnVideoSelectedListener callback;

    public SearchListAdapter(List<SearchItem> items, OnVideoSelectedListener callback) {
        this.items = items;
        this.callback = callback;
    }

    @Override
    public SearchItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.video_list_item,parent,false);
        return new SearchItemHolder(v,parent.getContext());
    }

    @Override
    public void onBindViewHolder(SearchItemHolder holder, int position) {
        holder.bindHolder(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class SearchItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Context mContext;
        private SearchItem mItem;
        private ImageView mImgThumbnail;
        private TextView mTxtTitle, mTxtPublishAt, mTxtChannelName;


        public SearchItemHolder(View itemView,Context context) {
            super(itemView);
            this.mContext = context;
            mImgThumbnail = (ImageView)itemView.findViewById(R.id.imgThumbnail);
            mTxtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            mTxtPublishAt = (TextView) itemView.findViewById(R.id.txtPublishedAt);
            mTxtChannelName = (TextView) itemView.findViewById(R.id.txtChannelName);
            itemView.setOnClickListener(this);
        }

        public void bindHolder(SearchItem item){
            this.mItem = item;
            mTxtTitle.setText(item.getSnippet().getTitle());
            mTxtPublishAt.setText(Utils.formatPublishedDate(mContext,
                    item.getSnippet().getPublishTime()));
            mTxtChannelName.setText(item.getSnippet().getChannelTitle());

            Glide.with(mContext)
                    .load(item.getSnippet().getThumbnails().getHighThumbnailUrl())
                    .centerCrop()
                    .placeholder(R.drawable.loading_spinner)
                    .into(mImgThumbnail);
        }

        @Override
        public void onClick(View v) {
            if(mItem.getId().getKind().equals(Utils.KIND_VIDEO)) {
                Intent intent = VideoActivity.newIntent(mContext, mItem.getId().getVideoId());
                mContext.startActivity(intent);
            }else if(mItem.getId().getKind().equals(Utils.KIND_CHANNEL)){
                mContext.startActivity(ChannelActivity.newIntent(mContext, mItem.getId().getChannelId()
                                                            , mItem.getSnippet().getTitle()));
            }else if(mItem.getId().getKind().equals(Utils.KIND_PLAYLIST)){
                mContext.startActivity(PlaylistActivity.newIntent(mContext, mItem.getId().getPlaylistId()));
            }
        }
    }
}
