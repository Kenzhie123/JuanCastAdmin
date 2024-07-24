package com.example.juancastadmin.listadapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.juancastadmin.R;
import com.example.juancastadmin.model.APAArtist;
import com.example.juancastadmin.model.Artist;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class APAArtistListAdapter extends RecyclerView.Adapter<APAArtistListAdapter.APAArtistListViewHolder> {

    Context context;
    ArrayList<APAArtist> artistList;

    FirebaseStorage storage;
    public APAArtistListAdapter(Context context, ArrayList<APAArtist> artistList)
    {
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public APAArtistListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new APAArtistListViewHolder(LayoutInflater.from(context).inflate(R.layout.apa_artistlist_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull APAArtistListViewHolder holder, int position) {
        storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference();

        APAArtist currentArtist = artistList.get(holder.getAdapterPosition());

        StorageReference imageRef = reference.child("artists").child(currentArtist.getArtistID());
        Glide.with(context).load(imageRef).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                holder.APALI_ProfileProgressBar.setVisibility(View.GONE);
                holder.APALI_ProfileImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.profile));
                return false;
            }

            @Override
            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                holder.APALI_ProfileProgressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.APALI_ProfileImage);
        holder.APALI_ArtistName.setText(currentArtist.getArtistName());
        holder.APALI_CheckBox.setChecked(currentArtist.isChecked());
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public class APAArtistListViewHolder extends RecyclerView.ViewHolder{

        ImageView APALI_ProfileImage;
        TextView APALI_ArtistName;
        CheckBox APALI_CheckBox;
        ProgressBar APALI_ProfileProgressBar;
        public APAArtistListViewHolder(@NonNull View itemView) {
            super(itemView);
            APALI_ProfileImage = itemView.findViewById(R.id.APALI_ProfileImage);
            APALI_ArtistName = itemView.findViewById(R.id.APALI_ArtistName);
            APALI_CheckBox = itemView.findViewById(R.id.APALI_CheckBox);
            APALI_ProfileProgressBar = itemView.findViewById(R.id.APALI_ProfileProgressBar);
        }
    }
}
