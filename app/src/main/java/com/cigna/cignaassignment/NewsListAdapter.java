package com.cigna.cignaassignment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pc272562 on 04/08/17.
 */

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.MyViewHolder> {

    List<NewsItem> mData;
    public NewsListAdapter(List<NewsItem> pData) {
        this.mData = pData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NewsItem lDataObject = mData.get(position);
        holder.title.setText(lDataObject.getTitle());
        holder.subHeader.setText(lDataObject.getAuthor());
        holder.description.setText(lDataObject.getDescription());
        holder.dateTime.setText(lDataObject.getPublishedAt());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subHeader, description, dateTime;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            subHeader = (TextView) view.findViewById(R.id.desc);
            description = (TextView) view.findViewById(R.id.desc1);
            dateTime = (TextView) view.findViewById(R.id.time);
        }
    }
}
