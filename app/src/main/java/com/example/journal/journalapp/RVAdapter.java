package com.example.journal.journalapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MainAdapterViewHolder> {

    public static ArrayList<String> data;
    private Context applicationContext;
    private OnItemClicked onClick;

    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public RVAdapter(ArrayList<String> myData, Context context){
        data=myData;
        applicationContext=context;
    }

    public class MainAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MainAdapterViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    @Override
    public RVAdapter.MainAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.notepad;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MainAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RVAdapter.MainAdapterViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });
        try{
            JSONObject notepad=new JSONObject(data.get(position));
            ((TextView) holder.itemView.findViewById(R.id.header)).setText(notepad.getString("header"));
//            ((TextView) holder.itemView.findViewById(R.id.body)).setText(notepad.getString("body"));
        }catch(JSONException e){}
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnClick(OnItemClicked onClick){
        this.onClick=onClick;
    }

    public void remove(String dataToDelete) {
        if(data.contains(dataToDelete)){
            int index=data.indexOf(dataToDelete);
            data.remove(index);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, getItemCount());
        }
    }

    public void remove(int index) {
        data.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, getItemCount());
    }

    public void updateAt(int position,String updateData) {
        data.set(position,updateData);
        notifyItemChanged(position);
    }

    public void add(String dataToAdd) {
        if(!data.contains(dataToAdd)) {
            data.add(dataToAdd);
            notifyItemInserted(getItemCount() - 1);
        }
    }
}