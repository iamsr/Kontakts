package com.example.shubhamr.kontakts.RecyclerView.RecentLogs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shubhamr.kontakts.R;

import java.util.List;

public class recentRecyclerViewAdapter extends RecyclerView.Adapter<recentRecyclerViewAdapter.recentViewHolder> {

    private List<recentModelClass> recentList;


    public class recentViewHolder extends RecyclerView.ViewHolder{

        public TextView dateView;
        public TextView timeView;
        public TextView durationView;
        public ImageView typeImage;

        public recentViewHolder(View view) {
            super(view);
            dateView = (TextView)view.findViewById(R.id.recentDate);
            timeView = (TextView)view.findViewById(R.id.recentTime);
            durationView = (TextView)view.findViewById(R.id.recentDuration);
            typeImage= (ImageView) view.findViewById(R.id.callType);
        }
        }

    public recentRecyclerViewAdapter(List<recentModelClass> recentList){
        this.recentList =recentList;
    }

    @Override
    public recentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_recyclerview, parent, false);
        return new recentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(recentViewHolder holder, int position) {

        recentModelClass recent = recentList.get(position);
        holder.dateView.setText(recent.getDate());
        holder.timeView.setText(recent.getTime());
        holder.durationView.setText(recent.getDuration());
        if(recent.getType()=="OUTGOING"){
            holder.typeImage.setImageResource(R.drawable.outgoingcall);
        }
        else if(recent.getType()=="INCOMING"){
            holder.typeImage.setImageResource(R.drawable.receivedcall);
        }
        else {
            holder.typeImage.setImageResource(R.drawable.missedcall);
        }
    }

    public int getItemCount() {
        return recentList.size();
    }





}
