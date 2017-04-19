package com.example.muffin.youtubeapp.adapters;

import android.content.Context;
import android.nfc.Tag;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muffin.youtubeapp.GsonModels.VideoItem;
import com.example.muffin.youtubeapp.R;
import com.example.muffin.youtubeapp.fragments.PlayListFragment;
import com.example.muffin.youtubeapp.utils.OnVideoSelectedListener;
import com.example.muffin.youtubeapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Adapter using for create list item, for youtube playList
 */

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {

    private final static String TAG = "PlayListAdapter";
    private OnVideoSelectedListener callback;

    private List<VideoItem> items = new ArrayList<>();

    public PlayListAdapter(List<VideoItem> items, OnVideoSelectedListener callback){
        this.callback = callback;
        this.items = items;
    }

    @Override
    public PlayListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_item,parent,false);
        return new ViewHolder(v,parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindHolder(items.get(position));
    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Context context;
        private VideoItem item;
        private ImageView imgThumbnail;
        private TextView txtTitle,txtDuration,txtPublishAt,txtChannelName;


        ViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.context = context;
            imgThumbnail = (ImageView)itemView.findViewById(R.id.imgThumbnail);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtDuration = (TextView) itemView.findViewById(R.id.txtDuration);
            txtPublishAt = (TextView) itemView.findViewById(R.id.txtPublishedAt);
            txtChannelName = (TextView) itemView.findViewById(R.id.txtChannelName);
        }

        void bindHolder(VideoItem item){
            this.item = item;
            Log.d(TAG,"Title : " + item.getSnippet().getTitle());
            txtTitle.setText(item.getSnippet().getTitle());
            txtDuration.setText(Utils.getTimeFromString(item.getContentDetails().getDuration()));
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
            callback.onVideoSelected(item.getId());
        }
    }
}
