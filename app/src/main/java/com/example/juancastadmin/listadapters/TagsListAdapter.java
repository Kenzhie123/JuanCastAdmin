package com.example.juancastadmin.listadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juancastadmin.AddArtist;
import com.example.juancastadmin.AddPollArtists;
import com.example.juancastadmin.R;

import java.util.ArrayList;

public class TagsListAdapter extends RecyclerView.Adapter<TagsListAdapter.TagsListViewHolder>{

    Context context;
    ArrayList<String> tagsList;

    AppCompatActivity activity;
    public TagsListAdapter(Context context, ArrayList<String> tagsList, AddArtist addArtist)
    {
        this.context = context;
        this.tagsList = tagsList;
        this.activity = addArtist;
    }

    public TagsListAdapter(Context context, ArrayList<String> tagsList, AddPollArtists addPollArtists)
    {
        this.context = context;
        this.tagsList = tagsList;
        this.activity = addPollArtists;
    }

    @NonNull
    @Override
    public TagsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TagsListViewHolder(LayoutInflater.from(context).inflate(R.layout.taglist_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TagsListViewHolder holder, int position) {
        holder.TLI_TagName.setText(tagsList.get(holder.getAdapterPosition()));
        holder.TLI_ConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(activity.getClass() == AddPollArtists.class)
                {
                    ((AddPollArtists)activity).addToTagList(holder.getAdapterPosition());
                }
                else
                {
                    ((AddArtist)activity).addToTagList(holder.getAdapterPosition());
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return tagsList.size();
    }

    public class TagsListViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout TLI_ConstraintLayout;
        TextView TLI_TagName;
        public TagsListViewHolder(@NonNull View itemView) {
            super(itemView);
            TLI_ConstraintLayout = itemView.findViewById(R.id.TLI_ConstraintLayout);
            TLI_TagName = itemView.findViewById(R.id.TLI_TagName);
        }
    }
}
