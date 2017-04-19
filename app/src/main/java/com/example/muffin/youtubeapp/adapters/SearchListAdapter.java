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
import com.example.muffin.youtubeapp.GsonModels.SearchItem;
import com.example.muffin.youtubeapp.R;
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

        private Context context;
        private SearchItem item;
        private ImageView imgThumbnail;
        private TextView txtTitle,txtPublishAt,txtChannelName;


        public SearchItemHolder(View itemView,Context context) {
            super(itemView);
            this.context = context;
            imgThumbnail = (ImageView)itemView.findViewById(R.id.imgThumbnail);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtPublishAt = (TextView) itemView.findViewById(R.id.txtPublishedAt);
            txtChannelName = (TextView) itemView.findViewById(R.id.txtChannelName);
            itemView.setOnClickListener(this);
        }

        public void bindHolder(SearchItem item){
            this.item = item;
            txtTitle.setText(item.getSnippet().getTitle());
            txtPublishAt.setText(Utils.formatPublishedDate(context,
                    item.getSnippet().getPublishTime()));
            txtChannelName.setText(item.getSnippet().getChannelTitle());

            Glide.with(context)
                    .load(item.getSnippet().getThumbnails().getDefaultThumbnailUrl())
                    .centerCrop()
                    .placeholder(R.drawable.loading_spinner)
                    .into(imgThumbnail);
        }

        @Override
        public void onClick(View v) {
            callback.onVideoSelected(item.getVideoId());
        }
    }
}
