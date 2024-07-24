package com.example.juancastadmin.listadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juancastadmin.AddArtist;
import com.example.juancastadmin.AddPollArtists;
import com.example.juancastadmin.R;

import java.util.ArrayList;

public class AddedTagListAdapter extends RecyclerView.Adapter<AddedTagListAdapter.AddedTagListViewHolder>{


    Context context;
    ArrayList<String> addedTagList;
    AppCompatActivity activity;

    public AddedTagListAdapter(Context context, ArrayList<String> addedTagList, AddArtist addArtist)
    {
        this.context = context;
        this.addedTagList = addedTagList;
        this.activity = addArtist;
    }
    public AddedTagListAdapter(Context context, ArrayList<String> addedTagList, AddPollArtists addPollArtists)
    {
        this.context = context;
        this.addedTagList = addedTagList;
        this.activity = addPollArtists;
    }


    @NonNull
    @Override
    public AddedTagListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddedTagListViewHolder(LayoutInflater.from(context).inflate(R.layout.added_taglist_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddedTagListViewHolder holder, int position) {
        holder.ATI_TagName.setText(addedTagList.get(holder.getAdapterPosition()));
        holder.ATI_RemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.getClass() == AddPollArtists.class)
                {
                    ((AddPollArtists)activity).removeFromTagList(holder.getAdapterPosition());
                }
                else
                {
                    ((AddArtist)activity).removeFromTagList(holder.getAdapterPosition());
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return addedTagList.size();
    }

    public class AddedTagListViewHolder extends RecyclerView.ViewHolder{

        TextView ATI_TagName;
        Button ATI_RemoveButton;
        public AddedTagListViewHolder(@NonNull View itemView) {
            super(itemView);
            ATI_TagName = itemView.findViewById(R.id.ATI_TagName);
            ATI_RemoveButton = itemView.findViewById(R.id.ATI_RemoveButton);

        }
    }
}
